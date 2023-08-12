package de.firecreeper82.commands.impl;

import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.MemberNotFoundException;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class KickCmd extends Command {

    public KickCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException {
        Member kickMember = Util.getMemberFromString(args[0], message);

        if(kickMember == null)
            throw new MemberNotFoundException("The member you are trying to kick could not be found.");

        kickMember.kick().queue();

        message.getChannel().sendMessageEmbeds(Util.createEmbed(
                "Kicked " + kickMember.getEffectiveName(),
                Color.GREEN,
                "Successfully kicked the member " + kickMember.getAsMention() + " from the server!",
                "Kicked by " + member.getEffectiveName(),
                Instant.now(),
                null,
                null
        ).build()).queue();
    }
}
