package com.stanuwu.tetraclient3.client;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Defaults {
    @Getter
    private final String TITLE = "TetraClient";
    @Getter
    private final String VERSION = "3.0";
    @Getter
    private final String CREDITS = "by @stanuwu";

    public String getDisplayName() {
        return TITLE + " " + VERSION;
    }
}
