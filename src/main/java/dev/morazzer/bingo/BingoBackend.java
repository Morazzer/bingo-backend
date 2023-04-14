package dev.morazzer.bingo;

import dev.morazzer.bingo.api.SocketServer;
import dev.morazzer.bingo.discord.BingoBot;
import dev.morazzer.bingo.logging.BingoFormatting;
import dev.morazzer.bingo.logging.ExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BingoBackend {

    public static Logger mainLogger;

    static {
        mainLogger = Logger.getLogger("BingoBackend");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new BingoFormatting());
        consoleHandler.setLevel(Level.ALL);
        mainLogger.addHandler(consoleHandler);
        mainLogger.setUseParentHandlers(false);
    }


    public static void main(String[] args) {
        mainLogger.log(Level.INFO, "Starting BingoBackend...");
        run(BingoBot::new, "bot");
        run(SocketServer::new, "api");
        run(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("stop")) {
                    SocketServer.getInstance().disconnectAll();
                    System.exit(0);
                } else if (line.startsWith("splash")) {
                    // splash <message part1> <message part2>
                    String[] split = line.split(" ");
                    if (split.length < 3) {
                        System.out.println("Invalid arguments");
                        continue;
                    }
                    // packet id 1 - should be prepended to the message
                    String message = split[1] + " " + split[2];
                    byte[] bytes = message.getBytes();
                    byte id = 1;
                    byte[] data = new byte[bytes.length + 1];
                    data[0] = id;
                    System.arraycopy(bytes, 0, data, 1, bytes.length);
                    SocketServer.getInstance().sendToAll(data);
                    continue;
                }
                byte[] bytes = line.getBytes();
                byte id = 0;
                byte[] data = new byte[bytes.length + 1];
                data[0] = id;
                System.arraycopy(bytes, 0, data, 1, bytes.length);
                SocketServer.getInstance().sendToAll(data);
            }
        }, "console");
    }

    public static void run(Runnable runnable, @NotNull String name) {
        Thread thread = new Thread(runnable, name);
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();
    }

}
