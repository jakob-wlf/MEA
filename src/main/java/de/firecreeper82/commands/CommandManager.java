package de.firecreeper82.commands;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class CommandManager {

    private final ArrayList<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
    }

    public void onCommand(Message msg) {
        String commandString = msg.getContentRaw().toLowerCase().substring(1).split(" ")[0];

        Optional<Command> optionalCommand = commands.stream().filter(cmd -> Arrays.asList(cmd.aliases).contains(commandString)).findFirst();
        if(optionalCommand.isEmpty()) {
            commandNotFound(msg, commandString);
            return;
        }


        Command command = optionalCommand.get();
        command.onCommand(msg.getContentRaw().toLowerCase().substring(commandString.length() + 1).split(" "), msg);



    }
    private void commandNotFound(Message message, String cmd) {

    }

    public void addCommand(Command command) {
        commands.add(command);
    }

}
