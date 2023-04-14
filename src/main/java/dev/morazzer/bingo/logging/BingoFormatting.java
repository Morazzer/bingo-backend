package dev.morazzer.bingo.logging;

import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BingoFormatting extends Formatter {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(2)));

    @NotNull
    @Override
    public String format(@NotNull LogRecord record) {
        String source = record.getSourceClassName() + ":" + record.getSourceMethodName();
        if (source.equals("dev.morazzer.bingo.logging.ExceptionHandler:uncaughtException")) {
            source = "Uncaught Exception";
        }

        StringBuilder msg = new StringBuilder("[%s] %s (%s) {%s}: %s\r\n".formatted(
                dateTimeFormatter.format(record.getInstant()),
                record.getLevel().getName(),
                source,
                Thread.currentThread().getName(),
                record.getMessage()
        ));

        if (record.getThrown() != null) {
            msg.append(record.getThrown().toString()).append("\r\n");
            for (StackTraceElement stackTraceElement : record.getThrown().getStackTrace()) {
                msg.append("\tat ").append(stackTraceElement.toString()).append("\r\n");
            }
        }

        return msg.toString();
    }
}
