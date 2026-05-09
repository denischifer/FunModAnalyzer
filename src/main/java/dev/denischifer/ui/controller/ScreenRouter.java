package dev.denischifer.ui.controller;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenRouter {
    private final JPanel container;
    private final CardLayout layout;
    private final Map<String, JPanel> screens = new HashMap<>();

    public ScreenRouter(@NotNull JPanel container) {
        this.container = container;
        this.layout = (CardLayout) container.getLayout();
    }

    public void register(@NotNull String id, @NotNull JPanel panel) {
        screens.put(id, panel);
        container.add(panel, id);
    }

    public void navigate(@NotNull String id) {
        JPanel panel = screens.get(id);
        if (panel != null) {
            layout.show(container, id);
        }
    }

    public void navigate(@NotNull String id, @NotNull JPanel dynamicPanel) {
        if (screens.containsKey(id)) {
            container.remove(screens.get(id));
        }
        register(id, dynamicPanel);
        layout.show(container, id);
    }
}