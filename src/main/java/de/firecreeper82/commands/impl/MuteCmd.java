package de.firecreeper82.commands.impl;

import de.firecreeper82.Main;
import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.InvalidArgumentsException;
import de.firecreeper82.exceptions.exceptions.MemberNotFoundException;
import de.firecreeper82.exceptions.exceptions.RoleNoFoundException;
import de.firecreeper82.exceptions.exceptions.WrongArgumentsException;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MuteCmd extends Command {
    public MuteCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException {
        Member muteMember = Util.getMemberFromString(args[0], message);

        if (muteMember == null)
            throw new MemberNotFoundException("The member you are trying to mute could not be found.");

        //TODO implement mute timeframe

        Role muteRole = message.getGuild().getRoleById(Main.getMutedRoleId());
        if (muteRole == null)
            throw new RoleNoFoundException("The muting role could not be found. Check the config to confirm the role id.");

        message.getGuild().addRoleToMember(muteMember, muteRole).queue();

        EmbedBuilder eb = Util.createEmbed(
                "Muted " + muteMember.getEffectiveName(),
                Color.GREEN,
                "Successfully muted the member " + muteMember.getAsMention() + "!",
                "Muted by " + member.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        final String reason = String.join(" ", Arrays.stream(args, 1, args.length).toList());

        eb.addField("Reason:", reason, true);
        message.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));

        eb = Util.createEmbed(
                "You were muted in " + message.getGuild().getName(),
                null,
                "You were muted in the server by " + member.getEffectiveName(),
                "Muted",
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason", reason, true);

        notifyUser(eb, muteMember.getUser());
    }
}
