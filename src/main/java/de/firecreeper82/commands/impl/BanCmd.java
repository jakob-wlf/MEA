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
import java.util.concurrent.TimeUnit;

public class BanCmd extends Command {

    public BanCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException {
        Member banMember = Util.getMemberFromString(args[0], message);

        if (banMember == null)
            throw new MemberNotFoundException("The member you are trying to ban could not be found.");

        String reason = String.join(" ", Arrays.stream(args, 1, args.length).toList());
        sendConfirmEmbed(message, member, banMember, reason);

        EmbedBuilder eb = getNotifyEmbed(member, reason);

        notifyUser(eb, banMember.getUser());

        banMember.ban(0, TimeUnit.SECONDS).queue();

    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException {
        Member banMember = event.getOption("user", OptionMapping::getAsMember);
        String reason = event.getOption("reason", OptionMapping::getAsString);

        if(event.getMember() == null)
            return;

        if (banMember == null)
            throw new MemberNotFoundException("The member you are trying to ban could not be found.");

        EmbedBuilder eb = createConfirmEmbed(event.getMember(), banMember, reason);
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        eb = getNotifyEmbed(event.getMember(), reason);

        notifyUser(eb, banMember.getUser());

        banMember.ban(0, TimeUnit.SECONDS).queue();
    }

    @NotNull
    private static EmbedBuilder getNotifyEmbed(Member member, String reason) {
        EmbedBuilder eb = Util.createEmbed(
                "You were banned from " + member.getGuild().getName(),
                Color.GREEN,
                "You were banned from the server by " + member.getEffectiveName(),
                "Banned",
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason", reason, true);
        return eb;
    }

    @SafeVarargs
    public final <T> void sendConfirmEmbed(Message msg, Member cmdUser, T... additionalArgs) {
        Member banMember = (Member) additionalArgs[0];
        String reason = (String) additionalArgs[1];

        EmbedBuilder eb = createConfirmEmbed(cmdUser, banMember, reason);
        msg.getChannel().sendMessageEmbeds(eb.build()).queue(message -> {
            if(Main.isDeleteCommandFeedback())
                message.delete().queueAfter(Main.getCommandFeedbackDeletionDelayInSeconds(), TimeUnit.SECONDS);
        });

        if(Main.isLogCommandUsage()) {
            Logger.logCommandUsage(eb, this, cmdUser, msg.getChannel());
        }
    }

    @NotNull
    private static EmbedBuilder createConfirmEmbed(Member cmdUser, Member banMember, String reason) {
        EmbedBuilder eb = Util.createEmbed(
                "Banned " + banMember.getEffectiveName(),
                null,
                "Successfully banned the member " + banMember.getAsMention() + " from the server!",
                "Banned by " + cmdUser.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason:", reason, true);
        return eb;
    }
}
