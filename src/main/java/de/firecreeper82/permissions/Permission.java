package de.firecreeper82.permissions;

import de.firecreeper82.Main;

public enum Permission {

    ADMIN(Main.getRoleIds().get("admin")),
    MODERATION(Main.getRoleIds().get("admin"), Main.getRoleIds().get("moderation")),
    MEMBER();

    private final String[] ids;

    Permission(String... ids) {
        this.ids = ids;
    }

    public String[] getIds() {
        return ids;
    }
}
