package dev.morazzer.bingo.api;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

public class SocketConnection {

    private final Socket socket;
    private String uuid;
    private boolean authenticated;

    public SocketConnection(Socket socket) {
        this.socket = socket;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void readIncoming() throws IOException {
        if (socket.isClosed()) {
            return;
        }
        if (socket.getInputStream().available() > 0) {
            byte[] buffer = new byte[socket.getInputStream().available()];
            int read = socket.getInputStream().read(buffer);
            if (read == -1) {
                return;
            }
            byte id = buffer[0];
            if (id == 0) {
                SocketServer.socketLogger.log(Level.FINE, "Client disconnected -> %s".formatted(socket.getInetAddress().getHostAddress()));
                SocketServer.getInstance().connections.remove(this);
            }
        }
    }
}
