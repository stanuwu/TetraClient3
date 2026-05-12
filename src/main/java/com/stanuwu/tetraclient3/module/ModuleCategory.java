package com.stanuwu.tetraclient3.module;

import com.stanuwu.tetraclient3.render.FontAwesomeIcons;
import lombok.Getter;

public enum ModuleCategory {
    COMBAT(FontAwesomeIcons.ShieldAlt, "Combat"),
    RENDER(FontAwesomeIcons.Eye, "Render"),
    PLAYER(FontAwesomeIcons.User, "Player"),
    MOVEMENT(FontAwesomeIcons.Walking, "Movement"),
    WORLD(FontAwesomeIcons.Globe, "World"),
    EXPLOIT(FontAwesomeIcons.ExclamationTriangle, "Exploit"),
    HUD(FontAwesomeIcons.Tv, "HUD"),
    MISC(FontAwesomeIcons.Cube, "Misc"),
    CONFIG(FontAwesomeIcons.Wrench, "Config");

    ModuleCategory(String icon, String display) {
        this.icon = icon;
        this.display = display;
    }

    @Getter
    private final String icon;

    @Getter
    private final String display;

    public String getIconDisplay() {
        return icon + "  " + display;
    }
}
