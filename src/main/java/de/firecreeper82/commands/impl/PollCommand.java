package de.firecreeper82.commands.impl;

import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.*;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

import static de.firecreeper82.util.Util.checkForValidTime;
import static de.firecreeper82.util.Util.createEmbed;

public class PollCommand extends Command {

    public PollCommand(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException, MemberIsAlreadyMutedException, MemberIsNotMutedException, SomethingWentWrongException {
        long time = checkForValidTime(args[1], getSyntax());

        String[] descriptionArray = Arrays.copyOfRange(args, 2, args.length);
        String description = "```" + String.join(" ", descriptionArray) + "```";
        description = description.replace("\\n", "\n");
        description += "\n\n**Poll ends** " + TimeFormat.RELATIVE.format(System.currentTimeMillis() + time);

        EmbedBuilder eb = Util.createEmbed(args[0], new Color(252, 33, 117), description, "Poll ends ", Instant.ofEpochMilli(System.currentTimeMillis() + time), null, null);
        message.replyEmbeds(eb.build()).queue(msg -> {
            msg.addReaction(Emoji.fromUnicode("✅")).queue();
            msg.addReaction(Emoji.fromUnicode("❌")).queue();
        });
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, InvalidArgumentsException, RoleNoFoundException, MemberIsAlreadyMutedException, WrongArgumentsException, MemberIsNotMutedException, SomethingWentWrongException {

    }
}
