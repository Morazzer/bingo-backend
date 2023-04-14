package dev.morazzer.bingo.discord;

import dev.morazzer.bingo.BingoBackend;
import dev.morazzer.bingo.Configuration;
import dev.morazzer.bingo.discord.listener.MessageListener;
import dev.morazzer.configuration.ConfigInstance;
import dev.morazzer.configuration.ConfigurationManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BingoBot {

    private static BingoBot instance;
    private final DiscordApi discordApi;
    private final String apikey;
    public static Logger botLogger;
    static {
        botLogger = Logger.getLogger("SocketServer");
        botLogger.setParent(BingoBackend.mainLogger);
    }


    public BingoBot() {
        Objects.requireNonNull(System.getenv("token"), "Token is null");
        Objects.requireNonNull(System.getenv("apikey"), "Apikey is null");
        BingoBot.instance = this;

        botLogger.log(Level.INFO, "Starting BingoBot...");

        final String token = System.getenv("token");
        this.apikey = System.getenv("apikey");

        this.discordApi = new DiscordApiBuilder()
                .setToken(token)
                .setIntents(
                        Intent.MESSAGE_CONTENT,
                        Intent.GUILDS,
                        Intent.GUILD_MESSAGE_TYPING,
                        Intent.GUILD_MESSAGES
                )
                .addListener(new MessageListener())
                .login()
                .join();


        ConfigurationManager.registerListener(this.discordApi);
        ConfigInstance<Configuration> config = ConfigurationManager.createConfig("config", Configuration.getInstance());
        config.createCommand(discordApi.getServers());
    }

    public static BingoBot getInstance() {
        return instance;
    }
}
