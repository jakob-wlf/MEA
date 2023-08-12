package de.firecreeper82.commands.impl;

import de.firecreeper82.commands.Command;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class KickCmd extends Command {

    public KickCmd(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        super(aliases, description, requiredArgs, requiredPerm);
    }

    @Override
    public void onCommand(String[] args, Message message) {

    }


}
