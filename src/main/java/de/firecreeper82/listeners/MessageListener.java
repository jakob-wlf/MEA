package de.firecreeper82.listeners;

import de.firecreeper82.Main;
import de.firecreeper82.logging.Logger;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getChannel() instanceof PrivateChannel || !e.getGuild().getId().equals(Main.getGuildId()) || e.getMember() == null || e.getMember().getUser().isBot())
            return;

        if(e.getMessage().getContentRaw().startsWith(Main.PREFIX)) {
            Main.commandManager.onCommand(e.getMessage());
            return;
        }

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

        JSONArray jsonArray = Main.readXp();
        for(Object o : jsonArray) {
            if(!(o instanceof JSONObject jsonObject))
                continue;

            if(jsonObject.get("id").equals(e.getMember().getId())) {
                long currentXp = (long) jsonObject.get("xp");
                long level = (long) jsonObject.get("level");
                currentXp += Main.getXpPerMessage();

                jsonObject.replace("xp", currentXp);

                double newLevel = Math.floor(Math.pow(currentXp, (2f / 5f)));
                if(level != (long) newLevel) {
                    jsonObject.replace("level", (long) newLevel);

                    TextChannel channel = e.getGuild().getTextChannelById(Main.getLevelingChannelID());
                    if(channel == null)
                        continue;

                    EmbedBuilder eb = getLevelUpEmbed(e, currentXp);
                    channel.sendMessage(e.getMember().getAsMention()).queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                    channel.sendMessageEmbeds(eb.build()).queue();
                }

                Main.writeXpToJsonFile(jsonArray);
                return;
            }
        }

        JSONObject object = new JSONObject();
        object.put("id", e.getMember().getId());
        object.put("xp", Main.getXpPerMessage());
        object.put("level", (long) Math.floor(Math.pow(Main.getXpPerMessage(), (2f / 5f))));

        jsonArray.add(object);
        Main.writeXpToJsonFile(jsonArray);
    }

    @NotNull
    private static EmbedBuilder getLevelUpEmbed(MessageReceivedEvent e, long currentXp) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(Main.getLevelUpImage());
        eb.setAuthor("Level Up!!!");
        eb.setColor(new Color(145, 255, 237));
        eb.setDescription(
                "Congratulations " + e.getMember().getAsMention() + "! \n" +
                "You leveled up to Level " + (int) Math.floor(Math.pow(currentXp, (2f / 5f)))
        );
        eb.setFooter("Level Up");
        eb.setTimestamp(Instant.now());
        return eb;
    }
}
