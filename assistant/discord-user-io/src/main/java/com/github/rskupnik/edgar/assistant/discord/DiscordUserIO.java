package com.github.rskupnik.edgar.assistant.discord;

import com.github.rskupnik.edgar.assistant.*;
import com.github.rskupnik.edgar.assistant.events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.function.Consumer;

public class DiscordUserIO extends ListenerAdapter implements UserIO, Subscriber {

    private final JDA jda;
    private final String authorizedUser;

    private Consumer<Object> awaitingInputConsumer = null;

    public DiscordUserIO(String token, String authorizedUser) {
        this.authorizedUser = authorizedUser;

        JDA jda0 = null;
        try {
            jda0 = JDABuilder.createDefault(token)
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
    public void onReady(ReadyEvent event) {

    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        System.out.println("Message from user: " + event.getAuthor().getName());
        if (event.getAuthor().isBot() || !event.getAuthor().getName().equalsIgnoreCase(authorizedUser))
            return;

        System.out.println("Received private message: " + event.getMessage().getContentDisplay());

        if (awaitingInputConsumer != null) {
            awaitingInputConsumer.accept(event.getMessage().getContentRaw());
            awaitingInputConsumer = null;
            EventManager.notify(new TriggerNextStepEvent());
        } else {
            EventManager.notify(new CommandIssuedEvent(event.getMessage().getContentRaw()));
        }
    }

    @Override
    public void update(Event event) {
        switch (event) {
            case RequestInputEvent requestInputEvent -> askForInput(requestInputEvent.message(), requestInputEvent.inputConsumer());
            case TaskTerminatedEvent taskTerminatedEvent -> {
                awaitingInputConsumer = null;
            }
            default -> {}
        }
    }
}
