package com.stanuwu.tetraclient3.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@UtilityClass
public class InputUtil {
    public boolean isKeyHeld(int key) {
        long window = Minecraft.getInstance().getWindow().handle();
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
}
