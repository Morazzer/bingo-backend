package dev.morazzer.bingo.logging;

import dev.morazzer.bingo.BingoBackend;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(@NotNull Thread t, Throwable e) {
        BingoBackend.mainLogger.log(Level.SEVERE, "Uncaught Exception in Thread %s".formatted(t.getName()), e);
    }
}
