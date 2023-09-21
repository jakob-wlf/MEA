package de.firecreeper82.listeners;

import de.firecreeper82.commands.impl.RolesCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class RolesListener extends ListenerAdapter {

    private RolesCommand rolesCommand;

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if (isInvalidReactionEvent(e.getMessageId(), e.getEmoji(), e.getMember()))
            return;

        if(e.getMember().getRoles().contains(rolesCommand.getRoles().get(e.getEmoji())))
            return;

        e.getGuild().addRoleToMember(e.getMember(), rolesCommand.getRoles().get(e.getEmoji())).queue();
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent e) {
        if (isInvalidReactionEvent(e.getMessageId(), e.getEmoji(), e.getMember()))
            return;

        if(!e.getMember().getRoles().contains(rolesCommand.getRoles().get(e.getEmoji())))
            return;

        e.getGuild().removeRoleFromMember(e.getMember(), rolesCommand.getRoles().get(e.getEmoji())).queue();
    }

    private boolean isInvalidReactionEvent(String messageId, EmojiUnion emoji, Member member) {
        if(rolesCommand == null)
            return true;

        if(rolesCommand.getRoles() == null || rolesCommand.getRolesMessage() == null)
            return true;

        if(!rolesCommand.getRolesMessage().getId().equals(messageId) || !rolesCommand.getRoles().containsKey(emoji))
            return true;

        if(member == null)
            return true;

        return false;
    }

    public void setRolesCommand(RolesCommand rolesCommand) {
        this.rolesCommand = rolesCommand;
    }
}
