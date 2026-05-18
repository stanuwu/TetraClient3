package com.stanuwu.tetraclient3.module.impl.combat;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.config.IntSliderValue;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;

public class AimModule extends AbstractModule {
    public AimModule() {
        super(ModuleCategory.COMBAT, "Aim (WIP)");
    }

    private enum AimMode {
        ANGLES,
        INPUT,
        SILENT
    }

    private enum AimPriority {
        FOV,
        CLOSE,
        FAR,
        LOW_HEALTH,
        FULL_HEALTH
    }

    public final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<AimMode> mode = reg(new EnumValue<>("Mode", AimMode.ANGLES, AimMode.class));
    public final CheckboxValue checkFov = reg(new CheckboxValue("Check FOV", false));
    public final CheckboxValue showFov = reg(new CheckboxValue("Show FOV", true, checkFov::getValue));
    public final FloatSliderValue fovAmount = reg(new FloatSliderValue("FOV", 50f, 1f, 500f, checkFov::getValue));
    public final FloatSliderValue smoothingAmount = reg(new FloatSliderValue("Smoothing", 5f, 0f, 20f));
    public final IntSliderValue targetSwitchDelay = reg(new IntSliderValue("Target Switch Delay", 0, 0, 1000));
    private final EnumValue<AimPriority> priority = reg(new EnumValue<>("Priority", AimPriority.FOV, AimPriority.class));
    

}
