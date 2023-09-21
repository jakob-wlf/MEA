package de.firecreeper82.listeners;

import de.firecreeper82.Main;
import de.firecreeper82.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

public class TicketListener extends ListenerAdapter {

    private final ArrayList<Member> openTickets = new ArrayList<>();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if(e.getButton().getId() == null || e.getMember() == null)
            return;

        if(e.getButton().getId().equals("ticket-mea-bot")) {

            if(openTickets.contains(e.getMember())) {
                e.reply("You already have an open ticket!").setEphemeral(true).queue();
                return;
            }

            openTickets.add(e.getMember());

            Guild guild = Main.jda.getGuildById(Main.getGuildId());
            if (guild == null)
                return;

            Role modRole = guild.getRoleById(Main.getRoleIds().get("moderation"));
            Role everyoneRole = guild.getRoleById(Main.getRoleIds().get("everyone"));
            if(modRole == null || everyoneRole == null)
                return;

            Category category = guild.getCategoryById(Main.getTicketsCategoryId());
            if (category == null)
                return;

            category.createTextChannel(e.getUser().getEffectiveName() + "'s - Ticket")
                    .addPermissionOverride(everyoneRole, null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .addPermissionOverride(e.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(modRole, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .queue(
                    textChannel -> {
                        EmbedBuilder eb = Util.createEmbed(
                                e.getUser().getEffectiveName() + "'s Ticket",
                                Color.green,
                                "This ticket was opened by " + e.getUser().getEffectiveName() + ".\n\n" +
                                        "To **close** the Ticket press  " + Emoji.fromUnicode("✅").getAsReactionCode() + "\n" +
                                        "To get a **transcript** of the ticket press  " + Emoji.fromUnicode("\uD83D\uDCBE").getAsReactionCode(),
                                "Ticket",
                                Instant.now(),
                                null,
                                null
                        );
                        eb.setFooter("Ticket", e.getUser().getAvatarUrl());

                        textChannel.sendMessageEmbeds(eb.build()).addActionRow(
                                Button.primary("close-ticket-mea-bot", Emoji.fromUnicode("✅")),
                                Button.primary("save-ticket-mea-bot", Emoji.fromUnicode("\uD83D\uDCBE"))
                        ).queue();
                    });

            e.reply("A Ticket has been created!").setEphemeral(true).queue();
        }
        if(e.getButton().getId().equals("close-ticket-mea-bot")) {
            openTickets.remove(e.getMember());
            e.getChannel().delete().queue();
        }
        if(e.getButton().getId().equals("save-ticket-mea-bot")) {
            File file = new File("src/main/resources/" + UUID.randomUUID() + ".txt");

            StringBuilder content = new StringBuilder();
            e.getChannel().getIterableHistory().queue(messages -> {
                for(Message msg : messages) {
                    if(msg.getMember() == null || msg.getMember().getUser().isBot())
                        continue;
                    content.append(msg.getMember().getEffectiveName()).append("( ").append(msg.getMember().getUser().getName()).append(" ): ").append(msg.getContentDisplay()).append("\n");
                }

                try {
                    FileWriter writer = new FileWriter(file);
                    writer.write(content.toString());
                    writer.close();
                } catch (IOException ex) {
                    e.reply("There aren't enough messages yet!").queue();
                }

                e.reply("Here is a transcript:").addFiles(FileUpload.fromData(file)).queue();

                if(!file.delete())
                    System.out.println("Couldn't delete the file after creating a transcript!");;
            });
        }
    }
}
