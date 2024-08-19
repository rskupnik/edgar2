package com.github.rskupnik.edgar.discord;

import com.github.rskupnik.edgar.UserMessageSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.UUID;

public class DiscordUserMessageSender implements UserMessageSender {

    private final JDA jda;
//    private final String authorizedUser;

    public DiscordUserMessageSender(String token) {
//        this.authorizedUser = authorizedUser;

        JDA jda0 = null;
        try {
            jda0 = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.DIRECT_MESSAGE_TYPING
                    ).build();
//            jda0.addEventListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.jda = jda0;
    }

    @Override
    public void send(String message) {
        jda.retrieveUserById(188321556009713674L).queue(u -> {
            u.openPrivateChannel().queue(ch -> {
                ch.sendMessage(message).queue();
            });
        });
    }

    @Override
    public void send(byte[] data) {
        jda.retrieveUserById(188321556009713674L).queue(u -> {
            u.openPrivateChannel().queue(ch -> {
                ch.sendFile(data, UUID.randomUUID().toString()).queue();
            });
        });
    }

    @Override
    public void sendAsImage(byte[] data) {
        jda.retrieveUserById(188321556009713674L).queue(u -> {
            u.openPrivateChannel().queue(ch -> {
                ch.sendFile(data, UUID.randomUUID() + ".jpg").queue();
            });
        });
    }
}
