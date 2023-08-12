package de.firecreeper82.listeners;

import de.firecreeper82.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        //TODO implement slash command logic
        if(e.getMessage().getContentRaw().startsWith(Main.PREFIX))
            Main.commandManager.onCommand(e.getMessage());
    }
}
