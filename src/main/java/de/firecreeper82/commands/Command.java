package de.firecreeper82.commands;

import de.firecreeper82.Main;
import de.firecreeper82.exceptions.exceptions.*;
import de.firecreeper82.permissions.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        if(!requiredArgs.isEmpty())
            return Main.PREFIX + aliases[0] + " [" + String.join("] [", requiredArgs) + "]";
        else
            return Main.PREFIX + aliases[0];
    }

    public abstract void onCommand(String[] args, Message message, Member member) throws MemberNotFoundException, WrongArgumentsException, InvalidArgumentsException, InterruptedException, RoleNoFoundException, MemberIsAlreadyMutedException, MemberIsNotMutedException, SomethingWentWrongException, IOException, ParseException;

    public abstract void onSlashCommand(SlashCommandInteractionEvent event) throws MemberNotFoundException, InvalidArgumentsException, RoleNoFoundException, MemberIsAlreadyMutedException, WrongArgumentsException, MemberIsNotMutedException, SomethingWentWrongException, IOException, ParseException;

    public <T> void sendConfirmEmbed(Message message, Member member, T... additionalArgs) {

    }

    public void notifyUser(EmbedBuilder eb, User user) {
        if (Main.isNotifyUserAtModerationAction()) {
            user.openPrivateChannel().queue(channel -> {
                try {
                    channel.sendMessageEmbeds(eb.build()).queue();
                } catch (Exception ignored) {}
            });
        }
    }
}
