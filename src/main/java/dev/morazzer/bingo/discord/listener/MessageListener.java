package dev.morazzer.bingo.discord.listener;

import dev.morazzer.bingo.Configuration;
import dev.morazzer.bingo.api.SocketServer;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.user.UserStartTypingEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.user.UserStartTypingListener;
import org.jetbrains.annotations.NotNull;

public class MessageListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(@NotNull MessageCreateEvent event) {
        if (event.getMessage().getChannel().getId() != Configuration.instance.getSplashChannel()) {
            return;
        }
        if (event.getMessage().getAuthor().isYourself()) {
            return;
        }
        if (event.getMessage().getAuthor().isBotUser()) {
            return;
        }
        if (!event.getMessage().getContent().contains("<@&" + Configuration.instance.getSplashRole() + ">")) {
            return;
        }

        String content = event.getMessage().getContent();
        content = content.replace("<@&" + Configuration.instance.getSplashRole() + ">", "");
        SocketServer.getInstance().sendSplash(content.trim());
    }
}
