package de.firecreeper82.exceptions;

import de.firecreeper82.Main;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Objects;

public class ExceptionHandler {

    public static void handleException(Message message, Exception e) {
        EmbedBuilder eb = getExceptionEmbedBuilder(e);

        if(eb != null)
            message.replyEmbeds(eb.build()).queue();
        else if(Main.isLogCommandUsage()) {
            StringWriter out = new StringWriter();
            PrintWriter pw = new PrintWriter(out);
            e.printStackTrace(pw);

            eb = Util.createEmbed(
                    String.join(" ", e.getClass().getSimpleName().split("(?=\\p{Lu})")),
                    Color.red,
                    "Exception occurred!\n\n```\n" + out + "\n```",
                    "Exception",
                    Instant.now(),
                    null,
                    null
            );

            Objects.requireNonNull(Objects.requireNonNull(Main.jda.getGuildById(Main.getGuildId())).getTextChannelById(Main.getLoggingChannelID())).sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static void handleSlashCommandException(SlashCommandInteractionEvent event, Exception e) {
        EmbedBuilder eb = getExceptionEmbedBuilder(e);

        if(eb != null)
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
        else {
            event.reply("Something went wrong!").setEphemeral(true).queue();
        }
    }

    private static EmbedBuilder getExceptionEmbedBuilder(Exception e) {
        String title = String.join(" ", e.getClass().getSimpleName().split("(?=\\p{Lu})"));

        if(!(e instanceof CustomException))
            return null;

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
