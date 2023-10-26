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

    private Consumer<Object> awaitingInputConsumer = null;

    public DiscordUserIO() {
        JDA jda0 = null;
        try {
            jda0 = JDABuilder.createDefault(Systems.Credentials.get("discordToken"))
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
        output(message);
        awaitingInputConsumer = inputConsumer;
        // TODO: This approach won't work with multitasking?
        // TODO: Implement this
//        System.out.println(message);
//        inputConsumer.accept("returned input");
    }

    @Override
    public void output(String message) {
        jda.retrieveUserById(188321556009713674L).queue(u -> {
            u.openPrivateChannel().queue(ch -> {
                ch.sendMessage(message).queue();
            });
        });
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {

    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        System.out.println("Message from user: " + event.getAuthor().getName());
        // TODO: Only accepts commands from specific user (configurable)
        if (event.getAuthor().isBot())
            return;

        System.out.println("Received private message: " + event.getMessage().getContentDisplay());

        if (awaitingInputConsumer != null) {
            awaitingInputConsumer.accept(event.getMessage().getContentRaw());
            awaitingInputConsumer = null;
        } else {
            Systems.Assistant.processCommand(event.getMessage().getContentRaw());
        }
    }
}
