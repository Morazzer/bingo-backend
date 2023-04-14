package dev.morazzer.bingo.api;

import dev.morazzer.bingo.BingoBackend;
import dev.morazzer.bingo.Configuration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServer {
    private static SocketServer instance;
    public static Logger socketLogger;

    static {
        socketLogger = Logger.getLogger("SocketServer");
        socketLogger.setParent(BingoBackend.mainLogger);
    }

    public ServerSocket socket;
    private final CopyOnWriteArrayList<Socket> clients = new CopyOnWriteArrayList<>();
    final List<SocketConnection> connections = new CopyOnWriteArrayList<>();

    public SocketServer() {
        instance = this;
        socketLogger.log(Level.INFO, "Starting SocketServer...");
        try {
            this.socket = new ServerSocket(1807, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BingoBackend.run(this::run, "api-new-connection");
        BingoBackend.run(this::readIncoming, "api-read-incoming");
    }

    public void sendToAll(byte @NotNull [] packet) {
        socketLogger.log(Level.FINEST, "Sending message to %s clients".formatted(connections.size()));
        int failed = 0;
        int prevSize = connections.size();
        for (SocketConnection connection : connections) {
            try {
                connection.getSocket().getOutputStream().write(packet);
            } catch (IOException e) {
                failed++;
                connections.remove(connection);
                socketLogger.log(Level.SEVERE, "Error while sending message to %s"
                        .formatted(connection.getSocket().getInetAddress().getHostAddress()), e);
            }
        }
        socketLogger.log(Level.INFO, "Successfully sent message to %s clients and failed to send %s".formatted(prevSize - failed, failed));
    }

    public void disconnectAll() {
        socketLogger.log(Level.FINE, "Disconnecting %s clients".formatted(connections.size()));
        for (SocketConnection client : connections) {
            try {
                client.getSocket().close();
                connections.remove(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void run() {
        socketLogger.log(Level.INFO, "Listening on port %s".formatted(socket.getLocalPort()));
        //noinspection InfiniteLoopStatement - extra thread for accepting new connections
        while (true) try {
            Socket client = socket.accept();
            if (Configuration.getInstance().getMaxConnections() <= connections.size() && Configuration.instance.getMaxConnections() != -1) {
                socketLogger.log(Level.INFO, "Max connections reached, disconnecting %s".formatted(client.getInetAddress().getHostAddress()));
                client.getOutputStream().write(new byte[]{2});
                client.getOutputStream().flush();
                client.close();
                continue;
            }
            connections.add(new SocketConnection(client));
            socketLogger.log(Level.INFO, "Accepted connection -> %s".formatted(client.getInetAddress().getHostAddress()));
        } catch (IOException e) {
            socketLogger.log(Level.SEVERE, "Error while accepting client", e);
        }
    }

    public void readIncoming() {
        //noinspection InfiniteLoopStatement - extra thread for reading incoming messages
        while (true) {
            for (SocketConnection connection : connections) {
                try {
                    connection.readIncoming();
                } catch (IOException e) {
                    socketLogger.log(Level.SEVERE, "Error while reading incoming message", e);
                }
            }
        }
    }

    public ServerSocket getSocket() {
        return socket;
    }


    public static SocketServer getInstance() {
        return instance;
    }

    public void sendSplash(@NotNull String content) {
        socketLogger.log(Level.INFO, "Sending splash message to all clients");


        content = content.replaceFirst(content.substring(0, 1), content.substring(0, 1).toUpperCase());
        byte[] packet = new byte[content.length() + 1];
        packet[0] = 1;
        System.arraycopy(content.getBytes(), 0, packet, 1, content.length());
        sendToAll(packet);
    }
}
