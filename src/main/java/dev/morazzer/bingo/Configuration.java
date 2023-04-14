package dev.morazzer.bingo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import dev.morazzer.bingo.discord.BingoBot;
import dev.morazzer.configuration.annotations.*;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;

@AutoSave
@Global
@Permission(allowed = PermissionType.BAN_MEMBERS)
public class Configuration implements dev.morazzer.configuration.Configuration {

    public static Configuration instance;

    @NotNull
    private static Path path = Paths.get("config.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @ConfigValue
    @CommandOptionType(type = SlashCommandOptionType.CHANNEL)
    private long splashChannel = -1;
    @ConfigValue
    @CommandOptionType(type = SlashCommandOptionType.ROLE)
    private long splashRole = -1;
    @ConfigValue
    @CommandOptionType(type = SlashCommandOptionType.LONG)
    private long maxConnections = 300;
    public long getSplashChannel() {
        return splashChannel;
    }

    public static Configuration getInstance() {
        if (instance == null) {
            try {
                InputStream inputStream = Files.newInputStream(path, StandardOpenOption.CREATE);
                instance = gson.fromJson(new String(inputStream.readAllBytes()), Configuration.class);
                inputStream.close();

                if (instance == null) {
                    instance = new Configuration();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    public long getMaxConnections() {
        return maxConnections;
    }

    public long getSplashRole() {
        return splashRole;
    }

    @Override
    public void save() {
        try {
            OutputStream fileOutputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);
            fileOutputStream.write(gson.toJson(this).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
