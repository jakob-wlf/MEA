package de.firecreeper82.exceptions;

import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

public class ExceptionHandler {

    public static void handleException(Message message, Exception e) {
        EmbedBuilder eb = getExceptionEmbedBuilder(e);

        message.replyEmbeds(eb.build()).queue();
    }

    public static void handleSlashCommandException(SlashCommandInteractionEvent event, Exception e) {
        EmbedBuilder eb = getExceptionEmbedBuilder(e);

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    @NotNull
    private static EmbedBuilder getExceptionEmbedBuilder(Exception e) {
        String title = String.join(" ", e.getClass().getSimpleName().split("(?=\\p{Lu})"));

        if(e instanceof CustomException)
            title = title.replace("Exception", "");
        else
            e.printStackTrace();

        return Util.createEmbed(
                title,
                Color.red,
                e.getMessage(),
                null,
                Instant.now(),
                null,
                null
        );
    }
}
