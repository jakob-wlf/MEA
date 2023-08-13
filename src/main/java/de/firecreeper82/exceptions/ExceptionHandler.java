package de.firecreeper82.exceptions;

import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.time.Instant;

public class ExceptionHandler {

    public static void handleException(Message message, Exception e) {
        String title = String.join(" ", e.getClass().getSimpleName().split("(?=\\p{Lu})"));

        if(e instanceof CustomException)
            title = title.replace("Exception", "");
        else
            e.printStackTrace();

        EmbedBuilder eb = Util.createEmbed(
                title,
                Color.red,
                e.getMessage(),
                null,
                Instant.now(),
                null,
                null
        );

        message.replyEmbeds(eb.build()).queue();
    }
}
