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

        EmbedBuilder eb = Util.createEmbed(
                "Kicked " + kickMember.getEffectiveName(),
                Color.GREEN,
                "Successfully kicked the member " + kickMember.getAsMention() + " from the server!",
                "Kicked by " + member.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        final String reason = String.join(" ", Arrays.stream(args, 1, args.length).toList());

        eb.addField("Reason:", reason, true);
        message.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));

        eb = Util.createEmbed(
                "You were kicked from " + message.getGuild().getName(),
                null,
                "You were kicked from the server by " + member.getEffectiveName(),
                "Kicked",
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason", reason, true);

        notifyUser(eb, kickMember.getUser());
    }
}
