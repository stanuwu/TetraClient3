package com.stanuwu.tetraclient3.module.impl.combat;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;

public class ReachModule extends AbstractModule {
    public ReachModule() {
        super(ModuleCategory.COMBAT, "Reach");
    }

    public final CheckboxValue blockReach = reg(new CheckboxValue("Block Reach", false));
    public final FloatSliderValue blockReachAmount = reg(new FloatSliderValue("Block Reach Amount", 3f, 1f, 6f, blockReach::getValue));
    public final CheckboxValue entityReach = reg(new CheckboxValue("Entity Reach", false));
    public final FloatSliderValue entityReachAmount = reg(new FloatSliderValue("Entity Reach Amount", 3f, 1f, 6f, entityReach::getValue));
}
