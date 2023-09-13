package de.firecreeper82;

import de.firecreeper82.commands.CommandManager;
import de.firecreeper82.commands.impl.*;
import de.firecreeper82.listeners.JoinListener;
import de.firecreeper82.listeners.MessageListener;
import de.firecreeper82.listeners.SlashListener;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
public class Main {

    public static String TOKEN;
    public static final String PREFIX = "!";

    private static String guildId;
    private static String adminRoleId;
    private static String moderationRoleId;
    private static boolean notifyUserAtModerationAction;
    private static boolean logCommandUsage;
    private static boolean deleteCommandFeedback;
    private static long commandFeedbackDeletionDelayInSeconds;
    private static String loggingChannelID;
    private static String levelingChannelID;
    private static String levelUpImage;
    private static String levelImage;
    private static JSONArray bannedLinks;
    private static JSONArray autoRoleIDs;
    private static long xpPerMessage;

    public static JDA jda;
    public static CommandManager commandManager;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/token.txt"));
        TOKEN = reader.readLine();

        jda = JDABuilder.createDefault(TOKEN)
                .setActivity(Activity.watching("you"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new MessageListener())
                .addEventListeners(new SlashListener())
                .addEventListeners(new JoinListener())
                .build();

        commandManager = new CommandManager();
        commandManager.addCommand(new KickCmd(
                new String[]{"kick"},
                "Kick a user from the server",
                Arrays.asList("User", "Reason"),
                Permission.MODERATION
        ));
        commandManager.addCommand(new ClearCmd(
                new String[]{"clear", "purge"},
                "Clear the chat",
                List.of("Messagecount"),
                Permission.MODERATION
        ));
        commandManager.addCommand(new BanCmd(
                new String[]{"ban"},
                "Ban a user from the server",
                List.of("User", "Reason"),
                Permission.ADMIN
        ));
        commandManager.addCommand(new MuteCmd(
                new String[]{"mute", "m", "timeout"},
                "Mute a user",
                List.of("User", "Mute Duration (1m/1h/1d/1w/infinite)", "Reason"),
                Permission.ADMIN
        ));
        commandManager.addCommand(new HelpCmd(
                new String[]{"help"},
                "List all commands",
                List.of(),
                Permission.MEMBER
        ));
        commandManager.addCommand(new UnMuteCmd(
                new String[]{"unmute"},
                "Unmute a member",
                List.of("User"),
                Permission.MEMBER
        ));
        commandManager.addCommand(new XpCommand(
                new String[] {"xp", "level"},
                "Check for your level",
                List.of(),
                Permission.MEMBER
        ));

        Main.jda.updateCommands().addCommands(
                Commands.slash("ban", "Ban a user from the server")
                        .addOption(OptionType.USER, "user", "The user to ban", true)
                        .addOption(OptionType.STRING, "reason", "The reason for the ban", true),
                Commands.slash("xp", "Check for your level"),
                Commands.slash("clear", "Clear the chat")
                        .addOption(OptionType.INTEGER, "count", "The number of messages to clear", true),
                Commands.slash("help", "List all commands"),
                Commands.slash("kick", "Kick a user from the server")
                        .addOption(OptionType.USER, "user", "The user to kick", true)
                        .addOption(OptionType.STRING, "reason", "The reason for the kick", true),
                Commands.slash("mute", "Mute a member")
                        .addOption(OptionType.USER, "user", "The user to mute", true)
                        .addOption(OptionType.STRING, "time", "The time for the mute (1m/1h/1d/1w/infinite)", true)
                        .addOption(OptionType.STRING, "reason", "The reason for the mute", true),
                Commands.slash("unmute", "Unmute a member")
                        .addOption(OptionType.USER, "user", "The user to unmute", true)
        ).queue();

        readConfig();
    }

    @SuppressWarnings("unchecked")
    public static void writeXpToJsonFile(JSONArray writeObjects) {
        JSONObject mainObject = new JSONObject();
        mainObject.put("Xp", writeObjects);

        try (FileWriter file = new FileWriter("src/main/resources/xp.json")) {
            file.write(mainObject.toJSONString());
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray readXp() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("src/main/resources/xp.json")) {

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (JSONArray) jsonObject.get("Xp");

        } catch (IOException | ParseException e) {
            throw new RuntimeException();
        }
    }

    public static HashMap<String, String> getRoleIds() {
        readConfig();
        HashMap<String, String> values = new HashMap<>();
        values.put("admin", adminRoleId);
        values.put("moderation", moderationRoleId);
        return values;
    }

    public static void readConfig(){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("src/main/resources/config.json")) {

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            adminRoleId = (String) jsonObject.get("AdminPermissionRoleID");
            moderationRoleId = (String) jsonObject.get("ModerationPermissionRoleID");

            guildId = (String) jsonObject.get("GuildID");
            notifyUserAtModerationAction = (Boolean) jsonObject.get("NotifyUserAtModerationAction");
            deleteCommandFeedback = (Boolean) jsonObject.get("DeleteCommandFeedback");
            logCommandUsage = (Boolean) jsonObject.get("LogCommandUsage");
            commandFeedbackDeletionDelayInSeconds = (long) jsonObject.get("CommandFeedbackDeletionDelayInSeconds");
            loggingChannelID = (String) jsonObject.get("LoggingChannelID");
            levelingChannelID = (String) jsonObject.get("LevelingChannelID");
            levelUpImage = (String) jsonObject.get("LevelUpImage");
            levelImage = (String) jsonObject.get("LevelImage");
            bannedLinks = (JSONArray) jsonObject.get("BannedLinks");
            autoRoleIDs = (JSONArray) jsonObject.get("AutoRoleIDs");
            xpPerMessage = (long) jsonObject.get("XpPerMessage");

        } catch (IOException | ParseException e) {
            throw new RuntimeException();
        }
    }

    public static boolean isNotifyUserAtModerationAction() {
        readConfig();
        return notifyUserAtModerationAction;
    }

    public static JSONArray getAutoRoleIDs() {
        readConfig();
        return autoRoleIDs;
    }

    public static long getXpPerMessage() {
        readConfig();
        return xpPerMessage;
    }

    public static String getLevelImage() {
        readConfig();
        return levelImage;
    }

    public static String getLevelingChannelID() {
        readConfig();
        return levelingChannelID;
    }

    public static String getLevelUpImage() {
        readConfig();
        return levelUpImage;
    }

    public static boolean isDeleteCommandFeedback() {
        readConfig();
        return deleteCommandFeedback;
    }

    public static boolean isLogCommandUsage() {
        readConfig();
        return logCommandUsage;
    }

    public static long getCommandFeedbackDeletionDelayInSeconds() {
        return commandFeedbackDeletionDelayInSeconds;
    }

    public static String getGuildId() {
        readConfig();
        return guildId;
    }

    public static String getLoggingChannelID() {
        readConfig();
        return loggingChannelID;
    }

    public static JSONArray getBannedLinks() {
        readConfig();
        return bannedLinks;
    }
}