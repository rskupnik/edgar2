package com.github.rskupnik.edgar.assistant;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DiscordUserIO extends ListenerAdapter implements UserIO {

    private final JDA jda;

    public DiscordUserIO() {
        JDA jda0 = null;
        try {
            jda0 = JDABuilder.createDefault("MTE2MTc1Mjg1NTY4Mjc2MDc5NA.GWf9r4.D6YBEGrn8TzR3DMTKr797O8yWGfDu7CLq__EvY")
                    .enableIntents(GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.DIRECT_MESSAGE_TYPING
                    ).build();
            jda0.addEventListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.jda = jda0;
    }

    @Override
    public void askForInput(String message, Consumer<Object> inputConsumer) {
        System.out.println(message);
        // TODO: Need UserID?
        //jda.getUserById().openPrivateChannel().flatMap(channel -> channel.sendMessage(message));
        inputConsumer.accept("returned input");
    }

    @Override
    public void output(String message) {
        System.out.println(message);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {

    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        System.out.println("Received private message: " + event.getMessage().getContentDisplay());
        Systems.Assistant.processCommand(event.getMessage().getContentRaw());
//        event.getChannel().sendMessage("Thanks for messaging me!");
    }
}
