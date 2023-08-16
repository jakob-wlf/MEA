package de.firecreeper82.listeners;

import de.firecreeper82.Main;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getChannel() instanceof PrivateChannel)
            return;
        Main.commandManager.onSlashCommand(event);
    }

}
