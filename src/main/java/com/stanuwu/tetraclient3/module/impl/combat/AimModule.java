package com.stanuwu.tetraclient3.module.impl.combat;

import com.stanuwu.tetraclient3.config.CheckboxValue;
import com.stanuwu.tetraclient3.config.ColorConfigValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.config.FloatSliderValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.PreRenderEvent;
import com.stanuwu.tetraclient3.events.impl.PreTickEvent;
import com.stanuwu.tetraclient3.events.impl.RenderHudEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.render.OverlayUtil;
import com.stanuwu.tetraclient3.util.ColorUtil;
import com.stanuwu.tetraclient3.util.MathUtil;
import imgui.ImGui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AimModule extends AbstractModule {
    public AimModule() {
        super(ModuleCategory.COMBAT, "Aim");
    }

    private enum AimMode {
        ANGLES,
        INPUT
    }

    private enum AimPriority {
        FOV,
        CLOSE,
        FAR,
        LOW_HEALTH,
        FULL_HEALTH
    }

    private enum AimTarget {
        CLOSEST,
        HEAD,
        CENTER,
        FEET
    }

    public final CheckboxValue enabled = reg(new CheckboxValue("Enabled", false));
    private final EnumValue<AimMode> mode = reg(new EnumValue<>("Mode", AimMode.ANGLES, AimMode.class));
    public final CheckboxValue checkReach = reg(new CheckboxValue("Check Reach", true));
    public final CheckboxValue checkVisible = reg(new CheckboxValue("Check Visible", true));
    public final CheckboxValue checkFov = reg(new CheckboxValue("Check FOV", false));
    public final CheckboxValue showFov = reg(new CheckboxValue("Show FOV", true, checkFov::getValue));
    public final ColorConfigValue showFovColor = reg(new ColorConfigValue("FOV Color", Color.RED, () -> checkFov.getValue() && showFov.getValue()));
    public final FloatSliderValue fovAmount = reg(new FloatSliderValue("FOV", 150f, 1f, 500f, checkFov::getValue));
    public final CheckboxValue showTarget = reg(new CheckboxValue("Show Target", true));
    public final ColorConfigValue showTargetColor = reg(new ColorConfigValue("Target Color", Color.RED, showTarget::getValue));
    public final CheckboxValue showSnapline = reg(new CheckboxValue("Show Snapline", true));
    public final ColorConfigValue showSnaplineColor = reg(new ColorConfigValue("Snapline Color", Color.RED, showSnapline::getValue));
    public final FloatSliderValue smoothingAmount = reg(new FloatSliderValue("Smoothing", 15f, 0f, 50f));
    private final EnumValue<AimTarget> aimTarget = reg(new EnumValue<>("Target", AimTarget.CLOSEST, AimTarget.class));
    private final EnumValue<AimPriority> priority = reg(new EnumValue<>("Priority", AimPriority.FOV, AimPriority.class));

    private int targetId = 0;
    private float targetX = 0;
    private float targetY = 0;
    private float targetZ = 0;

    private Vec3 resolveAimPosition(AbstractClientPlayer p) {
        switch (aimTarget.getValue()) {
            case CLOSEST -> {
                Vec3[] positions = {p.getEyePosition(), p.getBoundingBox().getCenter(), p.position()};
                Vec3 pos = null;
                float distance = Float.MAX_VALUE;
                float x = ImGui.getIO().getDisplaySizeX() / 2f;
                float y = ImGui.getIO().getDisplaySizeY() / 2f;
                for (Vec3 position : positions) {
                    Vec2 onscreen = OverlayUtil.worldToScreen(position);
                    float dist = Mth.sqrt(new Vec2(x, y).distanceToSqr(onscreen));
                    if (dist < distance) {
                        distance = dist;
                        pos = position;
                    }
                }
                return pos;
            }
            case HEAD -> {
                return p.getEyePosition();
            }
            case CENTER -> {
                return p.getBoundingBox().getCenter();
            }
            case FEET -> {
                return p.position();
            }
        }
        return Vec3.ZERO;
    }

    @EventSubscriber(event = RenderHudEvent.class)
    private void renderFov(RenderHudEvent event) {
        if (!enabled.getValue()) return;

        // calculate size
        float x = ImGui.getIO().getDisplaySizeX() / 2f;
        float y = ImGui.getIO().getDisplaySizeY() / 2f;

        // draw fov
        if (checkFov.getValue() && showFov.getValue()) {
            event.getData().drawList().addCircle(x, y, fovAmount.getValue(), ColorUtil.colorToImGuiColor(showFovColor.getValue()));
        }

        // target screen position
        Vec2 ts = OverlayUtil.worldToScreen(targetX, targetY, targetZ);

        // draw target
        if (showTarget.getValue() && !(targetX == 0 || targetY == 0 || targetZ == 0)) {
            event.getData().drawList().addCircleFilled(ts.x, ts.y, 3 * event.getData().scale(), ColorUtil.colorToImGuiColor(showTargetColor.getValue()));
        }

        // draw snapline
        if (showSnapline.getValue() && !(targetX == 0 || targetY == 0 || targetZ == 0)) {
            event.getData().drawList().addLine(ts.x, ts.y, x, y, ColorUtil.colorToImGuiColor(showSnaplineColor.getValue()));
        }
    }

    @EventSubscriber(event = PreRenderEvent.class)
    private void doPreRender(PreRenderEvent event) {
        if (!enabled.getValue()) return;
        LocalPlayer player = event.getData().player;
        if (player == null) return;
        if (targetX == 0 || targetY == 0 || targetZ == 0) return;

        // ignore if target is already in crosshair
        if (aimTarget.getValue().equals(AimTarget.CLOSEST) && event.getData().crosshairPickEntity != null && targetId == event.getData().crosshairPickEntity.getId())
            return;

        // aim at target
        switch (mode.getValue()) {
            case ANGLES -> {
                // calculate aim angles
                Vec3 eye = player.getEyePosition();
                Vec3 targetPos = new Vec3(targetX, targetY, targetZ).subtract(eye);
                double targetYaw = Math.toDegrees(Math.atan2(targetPos.z, targetPos.x)) - 90.0;
                double distXZ = Math.sqrt(targetPos.x * targetPos.x + targetPos.z * targetPos.z);
                double targetPitch = -Math.toDegrees(Math.atan2(targetPos.y, distXZ));

                float smooth = ((smoothingAmount.getMax()) - smoothingAmount.getValue() + 1) / 100f;
                float currentYaw = player.getYRot();
                float currentPitch = player.getXRot();
                float smoothedYaw = (float) (currentYaw + Mth.wrapDegrees(targetYaw - currentYaw) * smooth);
                float smoothedPitch = (float) Mth.lerp(smooth, currentPitch, targetPitch);
                smoothedPitch = (float) Math.clamp(smoothedPitch, -90.0, 90.0);
                player.setYRot(smoothedYaw);
                player.setXRot(smoothedPitch);
            }
            case INPUT -> {
                // calculate delta
                Vec2 screenPos = OverlayUtil.worldToScreen(targetX, targetY, targetZ);
                float x = ImGui.getIO().getDisplaySizeX() / 2f;
                float y = ImGui.getIO().getDisplaySizeY() / 2f;

                float deltaX = (x - screenPos.x) / (smoothingAmount.getValue() * 5f);
                float deltaY = (y - screenPos.y) / (smoothingAmount.getValue() * 5f);

                event.getData().mouseHandler.accumulatedDX -= deltaX;
                event.getData().mouseHandler.accumulatedDY -= deltaY;
                event.getData().mouseHandler.handleAccumulatedMovement();
            }
        }
    }

    @EventSubscriber(event = PreTickEvent.class)
    private void doPreTick(PreTickEvent event) {
        if (!enabled.getValue()) return;
        LocalPlayer player = event.getData().player;
        ClientLevel level = event.getData().level;
        if (player == null || level == null) return;

        // get all players
        List<AbstractClientPlayer> targets = new ArrayList<>(event.getData().level.players());

        // remove self
        targets.removeIf(t -> t instanceof LocalPlayer);

        // remove out of reach targets
        if (checkReach.getValue()) {
            double reach = player.entityInteractionRange();
            targets.removeIf(t -> t.distanceTo(player) > reach);
        }

        // remove targets not in fov
        if (checkFov.getValue()) {
            targets.removeIf(t -> !MathUtil.rectInFov(fovAmount.getValue(), MathUtil.aabbToRect(t.getBoundingBox())));
        }

        // remove if behind wall
        if (checkVisible.getValue()) {
            targets.removeIf(t -> {
                Vec3 eye = player.getEyePosition();

                ClipContext ctx = new ClipContext(
                        eye,
                        resolveAimPosition(t),
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        player
                );

                return level.clip(ctx).getType() != HitResult.Type.MISS;
            });
        }

        // determine target based on sorting
        AbstractClientPlayer target = null;
        switch (priority.getValue()) {
            case FOV -> {
                double closest = Double.MAX_VALUE;
                for (AbstractClientPlayer abstractClientPlayer : targets) {
                    double distance = MathUtil.rectFovDistance(MathUtil.aabbToRect(abstractClientPlayer.getBoundingBox()));
                    if (distance < closest) {
                        closest = distance;
                        target = abstractClientPlayer;
                    }
                }
            }
            case CLOSE -> {
                double closest = Double.MAX_VALUE;
                for (AbstractClientPlayer abstractClientPlayer : targets) {
                    double distance = abstractClientPlayer.distanceTo(player);
                    if (distance < closest) {
                        closest = distance;
                        target = abstractClientPlayer;
                    }
                }
            }
            case FAR -> {
                double farthest = Double.MIN_VALUE;
                for (AbstractClientPlayer abstractClientPlayer : targets) {
                    double distance = abstractClientPlayer.distanceTo(player);
                    if (distance > farthest) {
                        farthest = distance;
                        target = abstractClientPlayer;
                    }
                }
            }
            case LOW_HEALTH -> {
                float deadest = Float.MAX_VALUE;
                for (AbstractClientPlayer abstractClientPlayer : targets) {
                    float health = abstractClientPlayer.getHealth();
                    if (health < deadest) {
                        deadest = health;
                        target = abstractClientPlayer;
                    }
                }
            }
            case FULL_HEALTH -> {
                float healthiest = Float.MIN_VALUE;
                for (AbstractClientPlayer abstractClientPlayer : targets) {
                    float health = abstractClientPlayer.getHealth();
                    if (health > healthiest) {
                        healthiest = health;
                        target = abstractClientPlayer;
                    }
                }
            }
        }

        // set target for rendering
        if (target == null) {
            targetX = 0;
            targetY = 0;
            targetZ = 0;
            targetId = 0;
        } else {
            Vec3 ts = resolveAimPosition(target);
            targetX = (float) ts.x;
            targetY = (float) ts.y;
            targetZ = (float) ts.z;
            targetId = target.getId();
        }
    }
}
