package de.firecreeper82.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

public class PollListener extends ListenerAdapter {

    private final HashMap<Message, int[]> polls;

    public PollListener() {
        polls = new HashMap<>();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if(e.getUser() == null || e.getUser().isBot())
            return;

        Optional<Message> optionalMessage = polls.keySet().stream().filter(message ->
                        message.getId().equals(e.getMessageId())
                ).findFirst();

        if(optionalMessage.isEmpty())
            return;

        Message msg = optionalMessage.get();

        if(e.getReaction().getEmoji().equals(Emoji.fromUnicode("❌"))) {
            int[] newResults = polls.get(msg);
            newResults[1] += 1;

            msg.retrieveReactionUsers(Emoji.fromUnicode("✅")).queue(users -> {
                if (users.contains(e.getUser())) {
                    msg.removeReaction(Emoji.fromUnicode("✅"), e.getUser()).queue();
                    newResults[0] -= 1;
                }
                polls.replace(msg, newResults);
                System.out.println(newResults[0] + " ------- "+ newResults[1]);
                System.out.println(msg.getId());
            });
        }
        else if(e.getReaction().getEmoji().equals(Emoji.fromUnicode("✅"))) {
            int[] newResults = polls.get(msg);
            newResults[0] += 1;
            msg.retrieveReactionUsers(Emoji.fromUnicode("❌")).queue(users -> {
                if (users.contains(e.getUser())) {
                    msg.removeReaction(Emoji.fromUnicode("❌"), e.getUser()).queue();
                    newResults[1] -= 1;
                }
                polls.replace(msg, newResults);
                System.out.println(newResults[0] + " ------- "+ newResults[1]);
            });
        }
    }

    public void addPoll(Message message) {
        polls.put(message, new int[2]);
    }

    public int[] getResults(Message message) {
        System.out.println(polls.get(message)[0] + " ------- "+ polls.get(message)[1]);
        System.out.println(message.getId());
        return polls.get(message);
    }
}
