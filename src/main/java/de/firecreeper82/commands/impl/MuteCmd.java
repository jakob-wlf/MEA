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
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MuteCmd extends Command {
    public MuteCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException, MemberIsAlreadyMutedException {
        Member muteMember = Util.getMemberFromString(args[0], message);

        if (muteMember == null)
            throw new MemberNotFoundException("The member you are trying to mute could not be found.");

        long time = checkForValidTime(args[1]);
        if(time != 0)
            muteMember.timeoutFor(time, TimeUnit.MILLISECONDS).queue();
        else
            muteMember.timeoutFor(2147483647, TimeUnit.DAYS).queue();

        final String reason = String.join(" ", Arrays.stream(args, 2, args.length).toList());
        sendConfirmEmbed(message, member, time, muteMember, reason);

        EmbedBuilder eb = Util.createEmbed(
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

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, RoleNoFoundException, MemberIsAlreadyMutedException, WrongArgumentsException {
        Member muteMember = event.getOption("user", OptionMapping::getAsMember);

        if(event.getMember() == null)
            return;

        if (muteMember == null)
            throw new MemberNotFoundException("The member you are trying to mute could not be found.");

        long time = checkForValidTime(Objects.requireNonNull(event.getOption("time", OptionMapping::getAsString)));
        if(time != 0)
            muteMember.timeoutFor(time, TimeUnit.MILLISECONDS).queue();
        else
            muteMember.timeoutFor(2147483647, TimeUnit.DAYS).queue();

        String embedDescription = "Successfully muted the member " + muteMember.getAsMention() + "!\n";
        if(time != 0)
            embedDescription += "He will be unmuted " + TimeFormat.RELATIVE.format(System.currentTimeMillis() + time);

        final String reason = event.getOption("reason", OptionMapping::getAsString);
        event.replyEmbeds(createConfirmEmbed(event.getMember(), muteMember, embedDescription, reason).build()).setEphemeral(true).queue();

        EmbedBuilder eb = Util.createEmbed(
                "You were muted in " + event.getGuild().getName(),
                null,
                "You were muted in the server by " + event.getMember().getEffectiveName(),
                "Muted",
                Instant.now(),
                null,
                null
        );

        if(reason != null)
            eb.addField("Reason", reason, true);

        notifyUser(eb, muteMember.getUser());
    }


    @SafeVarargs
    @Override
    public final <T> void sendConfirmEmbed(Message msg, Member cmdUser, T... additionalArgs) {
        long time = (long) additionalArgs[0];
        Member muteMember = (Member) additionalArgs[1];
        String reason = (String) additionalArgs[2];

        String embedDescription = "Successfully muted the member " + muteMember.getAsMention() + "!\n";
        if(time != 0)
            embedDescription += "He will be unmuted " + TimeFormat.RELATIVE.format(System.currentTimeMillis() + time);

        EmbedBuilder eb = createConfirmEmbed(cmdUser, muteMember, embedDescription, reason);
        msg.getChannel().sendMessageEmbeds(eb.build()).queue(message -> {
            if(Main.isDeleteCommandFeedback())
                message.delete().queueAfter(Main.getCommandFeedbackDeletionDelayInSeconds(), TimeUnit.SECONDS);
        });

        if(Main.isLogCommandUsage()) {
            Logger.logCommandUsage(eb, this, cmdUser, msg.getChannel());
        }
    }

    @NotNull
    private static EmbedBuilder createConfirmEmbed(Member cmdUser, Member muteMember, String embedDescription, String reason) {
        EmbedBuilder eb = Util.createEmbed(
                "Muted " + muteMember.getEffectiveName(),
                Color.GREEN,
                embedDescription,
                "Muted by " + cmdUser.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        eb.addField("Reason:", reason, true);
        return eb;
    }

    private long checkForValidTime(String time) throws WrongArgumentsException {
        if(time.equalsIgnoreCase("infinite"))
            return 0;

        long multiplier;
        switch(time.substring(time.length() -1)) {
            case "m" -> multiplier = 60 * 1000L;
            case "h" -> multiplier = 60 * 60 * 1000L;
            case "d" -> multiplier = 24 * 60 * 60 * 1000L;
            case "w" -> multiplier = 7 * 24 * 60 * 60 * 1000L;
            default -> throw new WrongArgumentsException("The provided arguments do not match the syntax ``" + getSyntax() + "``");
        }

        if(!Util.isInt(time.substring(0, time.length() -1)))
            throw new WrongArgumentsException("The provided arguments do not match the syntax ``" + getSyntax() + "``");

        int rawTime = Integer.parseInt(time.substring(0, time.length() -1));
        return rawTime * multiplier;
    }
}
