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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class HelpCmd extends Command {
    public HelpCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException {
        EmbedBuilder eb = Util.createEmbed(
                "Help",
                Color.blue,
                "Here is a list of all commands",
                "Requested by " + member.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        for(Command cmd : Main.commandManager.getCommands()) {
            eb.addField(
                    Util.capitalize(cmd.getAliases()[0]),
                    "Aliases: `" + String.join(", ", cmd.getAliases()) + "`\n" +
                          "Syntax: `" + cmd.getSyntax()  + "`\n" +
                          "Description: `" + cmd.getDescription() + "`\n\n" +
                          "**--------------------------------------------------------------------**\n",
                    false
            );
        }

        message.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {

    }
}
