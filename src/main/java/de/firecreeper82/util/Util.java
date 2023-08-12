package de.firecreeper82.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.time.temporal.TemporalAccessor;

public class Util {

    public static Member getMemberFromString(String str, Message msg) {

        Guild g = msg.getGuild();
        String mentionid = str;

        if (str.startsWith("<") && str.endsWith(">")) {
            mentionid = mentionid.substring(2, mentionid.length()-1);

            if (mentionid.matches("^[0-9]*$")) {
                return g.getMemberById(mentionid);
            }else {
                return null;
            }

        }
        else {
            if (str.matches("^[0-9]*$")) {
                return g.getMemberById(str);
            }else {
                return null;
            }
        }
    }

    public static EmbedBuilder createEmbed(
            String title,
            Color color,
            String description,
            String footer,
            TemporalAccessor timestamp,
            String image,
            String thumbnail
    ) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(color);
        eb.setDescription(description);
        eb.setFooter(footer);
        eb.setTimestamp(timestamp);
        eb.setImage(image);
        eb.setThumbnail(thumbnail);
        return eb;
    }

}
