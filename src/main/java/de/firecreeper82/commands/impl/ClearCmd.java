package de.firecreeper82.commands.impl;

import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.InvalidArgumentsException;
import de.firecreeper82.exceptions.exceptions.WrongArgumentsException;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCmd extends Command {
    public ClearCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws InvalidArgumentsException, InterruptedException {
        if(!Util.isInt(args[0]))
            throw new InvalidArgumentsException("Your arguments do not match the syntax ``" + getSyntax() + "``.");

        int messageCount = Integer.parseInt(args[0]);
        if (messageCount < 1 || messageCount > 99)
            throw new InvalidArgumentsException("The Message count must be between 1 and 99.");

        messageCount++;
        List<Message> messages = message.getChannel().getHistory().retrievePast(messageCount).complete();
        message.getChannel().purgeMessages(messages);
        messageCount--;

        EmbedBuilder eb = Util.createEmbed(
                "Messages cleared",
                Color.GREEN,
                "Successfully cleared " + messageCount + " messages!",
                "Cleared by " + member.getEffectiveName(),
                Instant.now(),
                null,
                null
        );

        message.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
}
