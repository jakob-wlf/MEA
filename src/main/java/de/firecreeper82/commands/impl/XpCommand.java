package de.firecreeper82.commands.impl;

import de.firecreeper82.Main;
import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.*;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class XpCommand extends Command {
    public XpCommand(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);

        Main.jda.updateCommands().addCommands(
                Commands.slash(aliases[0], description)
        ).queue();
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException, MemberIsAlreadyMutedException, MemberIsNotMutedException, SomethingWentWrongException {
        EmbedBuilder eb = getLevelEmbed(member);
        if(eb == null)
            throw new SomethingWentWrongException("Something went wrong");
        message.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, InvalidArgumentsException, RoleNoFoundException, MemberIsAlreadyMutedException, WrongArgumentsException, MemberIsNotMutedException, SomethingWentWrongException {
        EmbedBuilder eb = getLevelEmbed(event.getMember());
        if(eb == null)
            throw new SomethingWentWrongException("Something went wrong");
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    public EmbedBuilder getLevelEmbed(Member member) {
        JSONArray jsonArray = Main.readXp();
        for(Object o : jsonArray) {
            if(!(o instanceof JSONObject jsonObject))
                continue;

            if(jsonObject.get("id").equals(member.getId())) {
                long currentXp = (long) jsonObject.get("xp");
                long level = (long) Math.floor(Math.pow(currentXp, (2f / 5f)));

                EmbedBuilder eb = new EmbedBuilder();
                eb.setThumbnail(member.getAvatarUrl());
                eb.setAuthor("Level " + (int) level + "!!");
                eb.setColor(new Color(145, 255, 237));
                eb.setDescription("You have **" + currentXp + "/" + (int) Math.pow(level, 2.5) + "** xp for level " + (level + 1) + ".");
                eb.setTimestamp(Instant.now());
                eb.setFooter("Level " + level);
                return eb;
            }
        }
        return null;
    }
}
