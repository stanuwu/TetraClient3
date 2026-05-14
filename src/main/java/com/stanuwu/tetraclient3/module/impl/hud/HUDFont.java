package com.stanuwu.tetraclient3.module.impl.hud;

import com.stanuwu.tetraclient3.render.ImGuiManager;
import imgui.ImFont;

public enum HUDFont {
    ANTA {
        @Override
        public ImFont getFont() {
            return ImGuiManager.getInstance().getAnta();
        }
    },
    ROBOTO {
        @Override
        public ImFont getFont() {
            return ImGuiManager.getInstance().getRoboto();
        }
    },
    BARCODE {
        @Override
        public ImFont getFont() {
            return ImGuiManager.getInstance().getBarcode();
        }
    },
    CHERRY_BOMB {
        @Override
        public ImFont getFont() {
            return ImGuiManager.getInstance().getCherrybomb();
        }
    };

    public abstract ImFont getFont();
}
