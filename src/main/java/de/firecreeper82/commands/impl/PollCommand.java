package de.firecreeper82.commands.impl;

import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.*;
import de.firecreeper82.listeners.PollListener;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static de.firecreeper82.util.Util.checkForValidTime;

public class PollCommand extends Command {

    private final PollListener pollListener;

    private static final Emoji checkMarkEmoji = Emoji.fromUnicode("✅");
    private static final Emoji xEmoji = Emoji.fromUnicode("❌");

    public PollCommand(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm, PollListener pollListener) {
        super(aliases, description, requiredArgs, requiredPerm);
        this.pollListener = pollListener;
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException, MemberIsAlreadyMutedException, MemberIsNotMutedException, SomethingWentWrongException {
        long time = checkForValidTime(args[1], getSyntax(), false);

        String[] descriptionArray = Arrays.copyOfRange(args, 2, args.length);
        String description = "```" + String.join(" ", descriptionArray) + "```";
        description = description.replace("\\n", "\n");

        poll(args[0], description, time, message.getChannel());

        message.delete().queue();
    }

    private void poll(String title, String description, long time, Channel channel) {
        if(!(channel instanceof TextChannel textChannel))
            return;

        String rawDescription = description;
        description += "\n**Poll ends** " + TimeFormat.RELATIVE.format(System.currentTimeMillis() + time);

        EmbedBuilder eb = Util.createEmbed(title, new Color(252, 33, 117), description, "Poll ends ", Instant.ofEpochMilli(System.currentTimeMillis() + time), null, null);
        textChannel.sendMessageEmbeds(eb.build()).queue(msg -> {

            msg.addReaction(checkMarkEmoji).queue();
            msg.addReaction(xEmoji).queue();
            pollListener.addPoll(msg);

            //New Embed when Poll is finished
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    int[] results = new int[2];

                    msg.retrieveReactionUsers(checkMarkEmoji).queue(usersCheck -> {
                        results[0] = usersCheck.size() - 1;

                        msg.retrieveReactionUsers(xEmoji).queue(usersX -> {
                            results[1] = usersX.size() - 1;

                            String newDescription = rawDescription + "\n**The Poll has ended.**";
                            newDescription += "\n**" + results[0] + "** have voted  " + checkMarkEmoji.getAsReactionCode();
                            newDescription += "\n**" + results[1] + "** have voted  " + xEmoji.getAsReactionCode();

                            eb.clear();
                            eb.setTitle(title + " (Ended)");
                            eb.setDescription(newDescription);
                            eb.setFooter("Poll ended");
                            eb.setTimestamp(Instant.now());
                            eb.setColor(new Color(252, 33, 117));
                            msg.editMessageEmbeds(eb.build()).queue();
                        });
                    });
                }
            }, time);
        });
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, InvalidArgumentsException, RoleNoFoundException, MemberIsAlreadyMutedException, WrongArgumentsException, MemberIsNotMutedException, SomethingWentWrongException {
        OptionMapping title, timeOption, descriptionOption;
        title = event.getOption("title");
        descriptionOption = event.getOption("description");
        timeOption = event.getOption("time");

        if(timeOption == null || descriptionOption == null || title == null)
            return;

        long time = checkForValidTime(timeOption.getAsString(), getSyntax(), false);

        String description = "```" + descriptionOption.getAsString() + "```";
        description = description.replace("\\n", "\n");

        poll(title.getAsString(), description,  time, event.getChannel());
        event.reply("Poll created successfully!").setEphemeral(true).queue();
    }
}
