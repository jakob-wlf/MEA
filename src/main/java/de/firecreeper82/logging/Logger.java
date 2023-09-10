package de.firecreeper82.logging;

import de.firecreeper82.Main;
import de.firecreeper82.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Logger {

    public static void logCommandUsage(EmbedBuilder eb, Command cmd, Member member, Channel channel) {
        Guild guild = Main.jda.getGuildById(Main.getGuildId());

        if(guild == null)
            return;

        TextChannel textChannel = guild.getTextChannelById(Main.getLoggingChannelID());
        if(textChannel == null)
            return;

        textChannel.sendMessage("Command ``" + cmd.getAliases()[0] + "`` executed by " + member.getAsMention() + " in channel " + channel.getAsMention()).addEmbeds(eb.build()).queue();
    }

    public static void logUserWarningBecauseOfMessage(EmbedBuilder eb, Member member, Message msg) {
        Guild guild = Main.jda.getGuildById(Main.getGuildId());

        if(guild == null)
            return;

        TextChannel textChannel = guild.getTextChannelById(Main.getLoggingChannelID());
        if(textChannel == null)
            return;

        textChannel.sendMessage(member.getAsMention() + " got a warning for something they posted in " + msg.getChannel().getAsMention()).addEmbeds(eb.build()).queue();

    }

}
