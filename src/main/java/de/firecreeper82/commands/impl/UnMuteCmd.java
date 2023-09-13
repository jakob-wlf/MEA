package de.firecreeper82.commands.impl;

import de.firecreeper82.Main;
import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.*;
import de.firecreeper82.logging.Logger;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class UnMuteCmd extends Command {


    public UnMuteCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, MemberIsNotMutedException{
        Member muteMember = Util.getMemberFromString(args[0], message);

        if (muteMember == null)
            throw new MemberNotFoundException("The member you are trying to unmute could not be found.");

        if(!muteMember.isTimedOut())
            throw new MemberIsNotMutedException("The member you are trying to unmute is not muted.");

        muteMember.removeTimeout().queue();

        sendConfirmEmbed(message, member, muteMember);

        EmbedBuilder eb = Util.createEmbed(
                "You were unmuted in " + message.getGuild().getName(),
                null,
                "You were unmuted in the server by " + member.getEffectiveName(),
                "Unmuted",
                Instant.now(),
                null,
                null
        );

        notifyUser(eb, muteMember.getUser());
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, RoleNoFoundException, MemberIsNotMutedException {
        Member muteMember = event.getOption("user", OptionMapping::getAsMember);

        if(event.getMember() == null)
            return;

        if (muteMember == null)
            throw new MemberNotFoundException("The member you are trying to unmute could not be found.");

        if(!muteMember.isTimedOut())
            throw new MemberIsNotMutedException("The member you are trying to unmute is not muted.");

        muteMember.removeTimeout().queue();

        event.replyEmbeds(createConfirmEmbed(event.getMember(), muteMember).build()).setEphemeral(true).queue();

        EmbedBuilder eb = Util.createEmbed(
                "You were unmuted in " + Objects.requireNonNull(event.getGuild()).getName(),
                null,
                "You were unmuted in the server by " + event.getMember().getEffectiveName(),
                "Unmuted",
                Instant.now(),
                null,
                null
        );

        notifyUser(eb, muteMember.getUser());
    }

    @SafeVarargs
    @Override
    public final <T> void sendConfirmEmbed(Message message, Member member, T... additionalArgs) {
        Member muteMember = (Member) additionalArgs[0];
        EmbedBuilder eb = createConfirmEmbed(member, muteMember);

        message.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> msg.delete().queueAfter(Main.getCommandFeedbackDeletionDelayInSeconds(), TimeUnit.SECONDS));
        if(Main.isLogCommandUsage()) {
            Logger.logCommandUsage(eb, this, member, message.getChannel());
        }
    }

    @NotNull
    private static EmbedBuilder createConfirmEmbed(Member member, Member muteMember) {
        return Util.createEmbed(
                "Unmuted " + muteMember.getEffectiveName(),
                Color.GREEN,
                "Successfully unmuted " + muteMember.getAsMention() + "!",
                "Unmuted by " + member.getEffectiveName(),
                Instant.now(),
                null,
                null
        );
    }
}
