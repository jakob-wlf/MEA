package de.firecreeper82.permissions;

import de.firecreeper82.Main;

public enum Permission {

    ADMIN(Main.readConfig().get("admin")),
    MODERATION(Main.readConfig().get("admin"), Main.readConfig().get("moderation")),
    MEMBER();

    private final String[] ids;

    Permission(String... ids) {
        this.ids = ids;
    }

    public String[] getIds() {
        return ids;
    }
}
