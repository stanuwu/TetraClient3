package com.stanuwu.tetraclient3.module.impl.misc;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;

public class NoGuiCloseModule extends AbstractModule {
    public NoGuiCloseModule() {
        super(ModuleCategory.MISC, "No Gui Close");
    }

    private final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));

    // TODO: impl after add packet receive
}
