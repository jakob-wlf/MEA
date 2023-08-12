package de.firecreeper82;

import de.firecreeper82.commands.CommandManager;
import de.firecreeper82.commands.impl.KickCmd;
import de.firecreeper82.listeners.MessageListener;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    public static String TOKEN;
    public static final String PREFIX = "!";

    private static String adminRoleId;
    private static String moderationRoleId;

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
                Permission.MODERATION
        ));

        readConfig();
    }

    public static HashMap<String, String> readConfig(){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("src/main/resources/config.json")) {

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            adminRoleId = (String) jsonObject.get("AdminPermissionRoleID");
            moderationRoleId = (String) jsonObject.get("ModerationPermissionRoleID");

        } catch (IOException | ParseException e) {
            throw new RuntimeException();
        }

        HashMap<String, String> values = new HashMap<>();
        values.put("admin", adminRoleId);
        values.put("moderation", adminRoleId);
        return values;
    }
}