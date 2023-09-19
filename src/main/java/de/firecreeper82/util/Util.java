package de.firecreeper82.util;

import de.firecreeper82.exceptions.exceptions.WrongArgumentsException;
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
                g.retrieveMemberById(mentionid).queue();
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

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (Exception e) {
            return false;
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

    public static long checkForValidTime(String time, String syntax, boolean onlyUntilDay) throws WrongArgumentsException {
        long multiplier = getMultiplier(time, syntax, onlyUntilDay);

        if(!Util.isInt(time.substring(0, time.length() -1)))
            throw new WrongArgumentsException("The provided arguments do not match the syntax ``" + syntax + "``");

        int rawTime = Integer.parseInt(time.substring(0, time.length() -1));
        if(rawTime <= 0)
            throw new WrongArgumentsException("The provided arguments do not match the syntax ``" + syntax + "``");
        return rawTime * multiplier;
    }

    private static long getMultiplier(String time, String syntax, boolean onlyUntilDay) throws WrongArgumentsException {
        long multiplier;
        switch(time.substring(time.length() -1)) {
            case "m" -> multiplier = 60 * 1000L;
            case "h" -> multiplier = 60 * 60 * 1000L;
            case "d" -> multiplier = 24 * 60 * 60 * 1000L;
            case "w" -> {
                if (!onlyUntilDay)
                     multiplier = 7 * 24 * 60 * 60 * 1000L;
                else
                    throw new WrongArgumentsException("The provided arguments do not match the syntax ``" + syntax + "``");
            }
            default -> throw new WrongArgumentsException("The provided arguments do not match the syntax ``" + syntax + "``");
        }
        return multiplier;
    }

}
