package de.firecreeper82.commands;

import de.firecreeper82.Main;
import de.firecreeper82.exceptions.exceptions.InvalidArgumentsException;
import de.firecreeper82.exceptions.exceptions.MemberNotFoundException;
import de.firecreeper82.exceptions.exceptions.WrongArgumentsException;
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

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredArgs() {
        return requiredArgs;
    }

    public String getSyntax() {
        return syntax;
    }

    public Permission getRequiredPerm() {
        return requiredPerm;
    }

    private String createSyntax() {
        return Main.PREFIX + aliases[0] + " [" + String.join("] [", requiredArgs) + "]";
    }

    public abstract void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException;
}
