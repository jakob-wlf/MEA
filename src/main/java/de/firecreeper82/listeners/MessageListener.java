package de.firecreeper82.listeners;

import de.firecreeper82.Main;
import de.firecreeper82.logging.Logger;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getChannel() instanceof PrivateChannel || !e.getGuild().getId().equals(Main.getGuildId()))
            return;

        if(e.getMessage().getContentRaw().startsWith(Main.PREFIX))
            Main.commandManager.onCommand(e.getMessage());

        for(Object o : Main.getBannedLinks()) {
            if(!(o instanceof String link))
                continue;
            if(e.getMessage().getContentRaw().contains(link)) {
                e.getMessage().delete().queue();

                Member member = e.getMember();
                if(member == null)
                    return;

                try {
                    User user = e.getMember().getUser();
                    user.openPrivateChannel().queue(privateChannel -> {
                        EmbedBuilder eb = Util.createEmbed(
                            "Warning",
                                Color.yellow,
                                "The link you posted in " + e.getChannel().getAsMention() + " is banned in the discord server. Please refrain from posting these links further!",
                                "Posted Banned Link",
                                Instant.now(),
                                null,
                                null
                        );
                        eb.addField("Message", "```" + e.getMessage().getContentRaw() + "```", false);
                        privateChannel.sendMessageEmbeds(eb.build()).queue();
                    });
                }
                catch (Exception ignored) {}

                EmbedBuilder eb = Util.createEmbed(
                        "Warning",
                        Color.yellow,
                        member.getAsMention() + " got a warning for posting a banned link in " + e.getMessage().getChannel().getAsMention(),
                        "Banned Link posted",
                        Instant.now(),
                        null,
                        null
                );
                eb.addField("Message", "```" + e.getMessage().getContentRaw() + "```", false);

                Logger.logUserWarningBecauseOfMessage(eb, e.getMember(), e.getMessage());
                return;
            }
        }
    }
}
