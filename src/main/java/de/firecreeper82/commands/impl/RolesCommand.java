package de.firecreeper82.commands.impl;

import de.firecreeper82.Main;
import de.firecreeper82.commands.Command;
import de.firecreeper82.exceptions.exceptions.*;
import de.firecreeper82.permissions.Permission;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RolesCommand extends Command {

    public RolesCommand(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException, MemberIsAlreadyMutedException, MemberIsNotMutedException, SomethingWentWrongException, IOException, ParseException {
        if(!(message.getChannel() instanceof TextChannel))
            return;

        createRolesEmbed((TextChannel) message.getChannel());
        message.delete().queue();
    }

    private void createRolesEmbed(TextChannel channel) throws IOException, ParseException{

        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/main/resources/config.json");
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

        JSONArray rolesArray = (JSONArray) jsonObject.get("SelfRoles");

        StringBuilder builder = new StringBuilder();
        HashMap<Emoji, Role> roles = new HashMap<>();

        for(Object o : rolesArray) {
            if(!(o instanceof JSONObject roleObject))
                continue;

            String name = (String) roleObject.get("name");
            String id = (String) roleObject.get("id");
            String emoji = (String) roleObject.get("emoji");

            Emoji reaction = Emoji.fromUnicode(emoji);
            builder.append(name.substring(0, 1).toUpperCase()).append(name.substring(1).toLowerCase()).append(":  ").append(reaction.getAsReactionCode()).append("\n");
            Role role = Objects.requireNonNull(Main.jda.getGuildById(Main.getGuildId())).getRoleById(id);

            roles.put(reaction, role);
        }


        EmbedBuilder eb = Util.createEmbed(
                "Self Roles",
                new Color(173, 54, 217),
                "**React to get the corresponding role**\n\n" +
                        builder,
                "Self Roles",
                null,
                null,
                null
        );

        channel.sendMessageEmbeds(eb.build()).queue(msg -> {

        });
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, InvalidArgumentsException, RoleNoFoundException, MemberIsAlreadyMutedException, WrongArgumentsException, MemberIsNotMutedException, SomethingWentWrongException, IOException, ParseException {
        if(!(event.getChannel() instanceof TextChannel))
            return;

        createRolesEmbed((TextChannel) event.getChannel());

        event.reply("Successfully created the roles embed").setEphemeral(true).queue();
    }
}
