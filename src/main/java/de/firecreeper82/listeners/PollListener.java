package de.firecreeper82.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public class PollListener extends ListenerAdapter {

    private final ArrayList<Message> polls;

    public PollListener() {
        polls = new ArrayList<>();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if(e.getUser() == null || e.getUser().isBot())
            return;

        Optional<Message> optionalMessage = polls.stream().filter(message ->
                        message.getId().equals(e.getMessageId())
                ).findFirst();

        if(optionalMessage.isEmpty())
            return;

        Message msg = optionalMessage.get();

        if(e.getReaction().getEmoji().equals(Emoji.fromUnicode("❌"))) {
            msg.retrieveReactionUsers(Emoji.fromUnicode("✅")).queue(users -> {
                if (users.contains(e.getUser()))
                    msg.removeReaction(Emoji.fromUnicode("✅"), e.getUser()).queue();
            });
        }
        else if(e.getReaction().getEmoji().equals(Emoji.fromUnicode("✅"))) {
            msg.retrieveReactionUsers(Emoji.fromUnicode("❌")).queue(users -> {
                if (users.contains(e.getUser()))
                    msg.removeReaction(Emoji.fromUnicode("❌"), e.getUser()).queue();
            });
        }
    }

    public void addPoll(Message message) {
        polls.add(message);
    }
}
