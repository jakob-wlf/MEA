package de.firecreeper82.commands;

import de.firecreeper82.exceptions.ExceptionHandler;
import de.firecreeper82.exceptions.exceptions.CommandNotFoundException;
import de.firecreeper82.exceptions.exceptions.WrongArgumentsException;
import de.firecreeper82.exceptions.exceptions.WrongPermissionsException;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandManager {

    private final ArrayList<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
    }

    public void onCommand(Message msg) {
        try {
            String commandString = msg.getContentRaw().toLowerCase().substring(1).split(" ")[0];

            Command command = commands.stream()
                    .filter(cmd -> Arrays.asList(cmd.aliases).contains(commandString))
                    .findFirst()
                    .orElseThrow(() -> new CommandNotFoundException("The command \"" + commandString + "\" doesn't exist."));

            if (msg.getMember() == null)
                throw new NullPointerException("The user of the command seems to be null.");


            String[] args = msg.getContentRaw().substring(commandString.length() + 1).split(" ");
            if(Objects.equals(args[0], ""))
                args = Arrays.stream(args, 1, args.length).toArray(String[]::new);

            if(args.length < command.getRequiredArgs().size())
                throw new WrongArgumentsException("Your arguments do not match the syntax ``" + command.getSyntax() + "``.");

            List<String> ids = msg.getMember().getRoles().stream().map(Role::getId).toList();
            if (Arrays.stream(command.requiredPerm.getIds()).noneMatch(ids::contains) && command.requiredPerm != Permission.MEMBER)
                throw new WrongPermissionsException("You don't have the permissions to use this command.");

            command.onCommand(args, msg, msg.getMember());
        } catch (Exception e) {
            ExceptionHandler.handleException(msg, e);
        }
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
