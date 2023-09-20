package de.firecreeper82.commands.impl;

import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.*;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.List;

public class TicketCommand extends Command {

    public TicketCommand(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException, MemberIsAlreadyMutedException, MemberIsNotMutedException, SomethingWentWrongException {
        if(!(message.getChannel() instanceof TextChannel))
            return;

        ticketEmbed((TextChannel) message.getChannel());
        message.delete().queue();
    }

    private void ticketEmbed(TextChannel textChannel) {
        EmbedBuilder eb = Util.createEmbed("Ticket", Color.GREEN, "Click to open a **support ticket**.\nYou can only have on ticket open at a time.", "Ticket", null, null, null);

        textChannel.sendMessageEmbeds(eb.build()).addActionRow(
                Button.primary("ticket-mea-bot", Emoji.fromUnicode("✉"))
        ).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, InvalidArgumentsException, RoleNoFoundException, MemberIsAlreadyMutedException, WrongArgumentsException, MemberIsNotMutedException, SomethingWentWrongException {
        if(!(event.getChannel() instanceof TextChannel))
            return;

        ticketEmbed((TextChannel) event.getChannel());

        event.reply("Successfully created the ticket embed").setEphemeral(true).queue();
    }
}
