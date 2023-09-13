package de.firecreeper82.commands.impl;

import de.firecreeper82.Main;
import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.MemberNotFoundException;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class KickCmd extends Command {

    public KickCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException {
        Member kickMember = Util.getMemberFromString(args[0], message);

        if (kickMember == null)
            throw new MemberNotFoundException("The member you are trying to kick could not be found.");

        kickMember.kick().queue();

        final String reason = String.join(" ", Arrays.stream(args, 1, args.length).toList());
        sendConfirmEmbed(message, member, kickMember, reason);

        EmbedBuilder eb = getNotifyEmbed(member, reason);

        eb.addField("Reason", reason, true);

        notifyUser(eb, kickMember.getUser());
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException {
        Member kickMember = event.getOption("user", OptionMapping::getAsMember);
        final String reason = event.getOption("reason", OptionMapping::getAsString);

        if(event.getMember() == null)
            return;

        if (kickMember == null)
            throw new MemberNotFoundException("The member you are trying to kick could not be found.");

        kickMember.kick().queue();

        EmbedBuilder eb = createConfirmEmbed(event.getMember(), kickMember, reason);
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        if(Main.isLogCommandUsage()) {
            Logger.logCommandUsage(eb, this, event.getMember(), event.getChannel());
        }

        eb = getNotifyEmbed(event.getMember(), reason);

        notifyUser(eb, kickMember.getUser());
    }

    @NotNull
    private static EmbedBuilder getNotifyEmbed(Member member, String reason) {
        EmbedBuilder eb;
        eb = Util.createEmbed(
                "You were kicked from " + Objects.requireNonNull(member.getGuild()).getName(),
                null,
                "You were kicked from the server by " + member.getUser().getEffectiveName(),
                "Kicked",
                Instant.now(),
                null,
                null
        );

        if(reason != null)
            eb.addField("Reason", reason, true);

        return eb;
    }


    @SafeVarargs
    public final <T> void sendConfirmEmbed(Message msg, Member cmdUser, T... additionalArgs) {
        Member kickMember = (Member) additionalArgs[0];
        String reason = (String) additionalArgs[1];

        EmbedBuilder eb = createConfirmEmbed(cmdUser, kickMember, reason);
        msg.getChannel().sendMessageEmbeds(eb.build()).queue(message -> {
            if(Main.isDeleteCommandFeedback())
                message.delete().queueAfter(Main.getCommandFeedbackDeletionDelayInSeconds(), TimeUnit.SECONDS);
        });

        if(Main.isLogCommandUsage()) {
            Logger.logCommandUsage(eb, this, cmdUser, msg.getChannel());
        }
    }

    @NotNull
    private static EmbedBuilder createConfirmEmbed(Member cmdUser, Member kickMember, String reason) {
        EmbedBuilder eb = Util.createEmbed(
                "Kicked " + kickMember.getEffectiveName(),
                Color.GREEN,
                "Successfully kicked the member " + kickMember.getAsMention() + " from the server!",
                "Kicked by " + cmdUser.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason:", reason, true);
        return eb;
    }
}
