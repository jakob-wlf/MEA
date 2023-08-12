package de.firecreeper82;

import de.firecreeper82.commands.CommandManager;
import de.firecreeper82.commands.impl.KickCmd;
import de.firecreeper82.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;

public class Main {

    public static final String TOKEN = "MTEzOTkxNTQxMTk2NjY3Mjk5OA.GmN9FF._bKAQEErk63F309DWdXmEoPp8Rp02RJfqid2sc";
    public static final String PREFIX = "!";

    public static JDA jda;
    public static CommandManager commandManager;

    public static void main(String[] args) {
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