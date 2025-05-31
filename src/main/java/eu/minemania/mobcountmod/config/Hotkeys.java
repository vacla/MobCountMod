package eu.minemania.mobcountmod.config;

import fi.dy.masa.malilib.config.options.ConfigHotkey;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Default hotkeys configuration.
 */
public class Hotkeys
{
    public static final ConfigHotkey PASSIVE = new ConfigHotkey("togglePassive", "P", "mobcountmod.hotkey.passive.description");
    public static final ConfigHotkey HOSTILE = new ConfigHotkey("toggleHostile", "O", "mobcountmod.hotkey.hostile.description");
    public static final ConfigHotkey OPEN_GUI_SETTINGS = new ConfigHotkey("openGuiSettings", "P,C", "mobcountmod.hotkey.open_gui_settings.description");
    public static final ConfigHotkey TOGGLE_BOTH = new ConfigHotkey("toggleBoth", "", "mobcountmod.hotkey.both.description");
    public static final ConfigHotkey EMPTY_MOBCOUNTS = new ConfigHotkey("emptyMobcounts", "", "mobcountmod.hotkey.empty_mobcounts.description");

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            EMPTY_MOBCOUNTS,
            HOSTILE,
            OPEN_GUI_SETTINGS,
            PASSIVE,
            TOGGLE_BOTH
    );
}