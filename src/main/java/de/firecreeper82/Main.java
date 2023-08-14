package de.firecreeper82;

import de.firecreeper82.commands.CommandManager;
import de.firecreeper82.commands.impl.*;
import de.firecreeper82.listeners.MessageListener;
import de.firecreeper82.listeners.ReadyListener;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static String TOKEN;
    public static final String PREFIX = "!";

    private static String adminRoleId;
    private static String moderationRoleId;
    private static boolean notifyUserAtModerationAction;
    private static boolean logCommandUsage;
    private static boolean deleteCommandFeedback;
    private static String mutedRoleId;
    private static long commandFeedbackDeletionDelayInSeconds;

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
                .addEventListeners(new ReadyListener())
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
                new String[]{"mute", "m"},
                "Ban a user from the server",
                List.of("User", "Mute Duration (1m/1h/1d/1w/infinite)", "Reason"),
                Permission.ADMIN
        ));
        commandManager.addCommand(new HelpCmd(
                new String[]{"help"},
                "List all commands",
                List.of(),
                Permission.MEMBER
        ));


        readConfig();
    }


    @SuppressWarnings("unchecked")
    public static void muteTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for(Object object : readMutedMembers()) {
                    JSONObject jsonObject = (JSONObject) object;

                    long finishedTime = (long) jsonObject.get("FinishedTime");
                    if(System.currentTimeMillis() >= finishedTime) {
                        Guild guild = jda.getGuildById((String) jsonObject.get("GuildID"));
                        if(guild == null)
                            return;

                        String memberId = (String) jsonObject.get("MemberID");
                        guild.retrieveMemberById(memberId).queue(member -> {
                            JSONArray objects = Main.readMutedMembers();

                            JSONArray writeObjects = new JSONArray();
                            for(Object o : objects) {
                                if(!(o instanceof JSONObject jObject))
                                    continue;
                                if(!jObject.get("MemberID").equals(memberId))
                                    writeObjects.add(jObject);

                            }

                            writeMutedMembersToJsonFile(writeObjects);

                            Role role = guild.getRoleById(getMutedRoleId());
                            if(role == null)
                                return;

                            guild.removeRoleFromMember(member, role).queue();
                        });
                    }
                }
            }
        }, 0, 1000);
    }

    @SuppressWarnings("unchecked")
    public static void writeMutedMembersToJsonFile(JSONArray writeObjects) {
        JSONObject mainObject = new JSONObject();
        mainObject.put("MutedMembers", writeObjects);

        try (FileWriter file = new FileWriter("src/main/resources/mutedMembers.json")) {
            file.write(mainObject.toJSONString());
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
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

            notifyUserAtModerationAction = (Boolean) jsonObject.get("NotifyUserAtModerationAction");
            deleteCommandFeedback = (Boolean) jsonObject.get("DeleteCommandFeedback");
            logCommandUsage = (Boolean) jsonObject.get("LogCommandUsage");
            mutedRoleId = (String) jsonObject.get("MutedRoleID");
            commandFeedbackDeletionDelayInSeconds = (long) jsonObject.get("CommandFeedbackDeletionDelayInSeconds");

        } catch (IOException | ParseException e) {
            throw new RuntimeException();
        }
    }


    public static JSONArray readMutedMembers() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("src/main/resources/mutedMembers.json")) {

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (JSONArray) jsonObject.get("MutedMembers");

        } catch (IOException | ParseException e) {
            throw new RuntimeException();
        }
    }

    public static boolean isNotifyUserAtModerationAction() {
        readConfig();
        return notifyUserAtModerationAction;
    }

    public static String getMutedRoleId() {
        readConfig();
        return mutedRoleId;
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
}