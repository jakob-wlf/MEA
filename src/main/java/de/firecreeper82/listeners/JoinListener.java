package de.firecreeper82.listeners;

import de.firecreeper82.Main;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if(!e.getGuild().getId().equals(Main.getGuildId()))
            return;

        for(Object o : Main.getAutoRoleIDs()) {
            if(!(o instanceof String id))
                continue;
            Role role = e.getGuild().getRoleById(id);
            if(role == null)
                continue;

            e.getGuild().addRoleToMember(e.getMember(), role).queue();
        }
    }

}
