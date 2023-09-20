package de.firecreeper82.listeners;

import de.firecreeper82.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.UUID;

public class TicketListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if(e.getButton().getId() == null)
            return;
        if(!e.getButton().getId().equals("ticket-mea-bot"))
            return;

        Guild guild = Main.jda.getGuildById(Main.getGuildId());
        if(guild == null)
            return;

        Category category = guild.getCategoryById(Main.getTicketsCategoryId());
        if(category == null)
            return;

        category.createTextChannel(e.getUser().getEffectiveName() + "'s - Ticket").queue(); ;

        e.reply("A Ticket has been created!").setEphemeral(true).queue();
    }
}
