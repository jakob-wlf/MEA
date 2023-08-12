package de.firecreeper82.commands;

import de.firecreeper82.Main;
import de.firecreeper82.exceptions.exceptions.MemberNotFoundException;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public abstract class Command {

    protected String[] aliases;
    protected String description;
    protected List<String> requiredArgs;
    protected String syntax;

    protected Permission requiredPerm;

    public Command(String[] aliases, String description, List<String> requiredArgs, Permission requiredPerm) {
        this.aliases = aliases;
        this.description = description;
        this.requiredArgs = requiredArgs;
        this.syntax = createSyntax();
        this.requiredPerm = requiredPerm;
    }

    private String createSyntax() {
        return Main.PREFIX + aliases[0] + " [" + String.join("] [", requiredArgs) + "]";
    }

    public abstract void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException;
}
