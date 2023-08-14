package de.firecreeper82.commands.impl;

import de.firecreeper82.Main;
import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.MemberNotFoundException;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

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

        EmbedBuilder eb = Util.createEmbed(
                "You were banned from " + message.getGuild().getName(),
                Color.GREEN,
                "You were banned from the server by " + member.getEffectiveName(),
                "Banned",
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason", reason, true);

        notifyUser(eb, banMember.getUser());

        banMember.ban(0, TimeUnit.SECONDS).queue();

        eb = Util.createEmbed(
                "Banned " + banMember.getEffectiveName(),
                null,
                "Successfully banned the member " + banMember.getAsMention() + " from the server!",
                "Banned by " + member.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason:", reason, true);
        message.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
            if(Main.isDeleteCommandFeedback())
                msg.delete().queueAfter(Main.getCommandFeedbackDeletionDelayInSeconds(), TimeUnit.SECONDS);
        });
    }
}
