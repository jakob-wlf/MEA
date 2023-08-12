package de.firecreeper82;

import de.firecreeper82.commands.CommandManager;
import de.firecreeper82.commands.impl.KickCmd;
import de.firecreeper82.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.util.Arrays;

public class Main {

    public static String TOKEN;
    public static final String PREFIX = "!";

    public static JDA jda;
    public static CommandManager commandManager;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/token.txt"));
        TOKEN = reader.readLine();

        jda = JDABuilder.createDefault(TOKEN)
                .setActivity(Activity.watching("you"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageListener())
                .build();

        commandManager = new CommandManager();
        commandManager.addCommand(new KickCmd(
                new String[]{"kick"},
                "Kick a user from the server",
                Arrays.asList("User", "Reason"),
                null
        ));
    }
}