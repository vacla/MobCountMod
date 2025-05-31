package eu.minemania.mobcountmod.event;

import com.google.common.collect.ImmutableList;
import eu.minemania.mobcountmod.Reference;
import eu.minemania.mobcountmod.config.Configs;
import eu.minemania.mobcountmod.config.Hotkeys;
import eu.minemania.mobcountmod.config.InfoToggleHostile;
import eu.minemania.mobcountmod.config.InfoTogglePassive;
import eu.minemania.mobcountmod.counter.DataManager;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.malilib.util.KeyCodes;

public class InputHandler implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler
{

    private static final InputHandler INSTANCE = new InputHandler();

    private InputHandler()
    {
    }

    public static InputHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
        for (IHotkey hotkey : Hotkeys.HOTKEY_LIST)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }

        for (InfoTogglePassive togglePassive : InfoTogglePassive.values())
        {
            manager.addKeybindToMap(togglePassive.getKeybind());
        }

        for (InfoToggleHostile toggleHostile : InfoToggleHostile.values())
        {
            manager.addKeybindToMap(toggleHostile.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
        manager.addHotkeysForCategory(Reference.MOD_NAME, "mobcountmod.hotkeys.category.generic_hotkeys", Hotkeys.HOTKEY_LIST);
        manager.addHotkeysForCategory(Reference.MOD_NAME, "mobcountmod.hotkeys.category.info_toggle_hotkeys.passive", ImmutableList.copyOf(InfoTogglePassive.values()));
        manager.addHotkeysForCategory(Reference.MOD_NAME, "mobcountmod.hotkeys.category.info_toggle_hotkeys.hostile", ImmutableList.copyOf(InfoToggleHostile.values()));
    }

    @Override
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        if (eventKeyState)
        {
            if (Configs.Generic.ENABLED.getBooleanValue())
            {
                if (Hotkeys.PASSIVE.getKeybind().isKeybindHeld())
                {
                    if (keyCode == KeyCodes.KEY_UP)
                    {
                        DataManager.getCounter().increaseRadiusP();
                    }
                    else if (keyCode == KeyCodes.KEY_DOWN)
                    {
                        DataManager.getCounter().decreaseRadiusP();
                    }
                    else
                    {
                        DataManager.upVisibleCounter();
                        if (DataManager.visibleCounter() > 1)
                        {
                            DataManager.resetVisibleCounter();
                        }
                    }
                    return true;
                }
                if (Hotkeys.HOSTILE.getKeybind().isKeybindHeld())
                {
                    if (keyCode == KeyCodes.KEY_UP)
                    {
                        DataManager.getCounter().increaseRadiusH();
                    }
                    else if (keyCode == KeyCodes.KEY_DOWN)
                    {
                        DataManager.getCounter().decreaseRadiusH();
                    }
                    else
                    {
                        DataManager.upVisibleHostile();
                        if (DataManager.visibleHostile() > 1)
                        {
                            DataManager.resetVisibleHostile();
                        }
                    }
                    return true;
                }
                if (Hotkeys.TOGGLE_BOTH.getKeybind().isKeybindHeld())
                {
                    DataManager.upVisibleCounter();
                    DataManager.upVisibleHostile();
                    if (DataManager.visibleCounter() > 1)
                    {
                        DataManager.resetVisibleCounter();
                    }
                    if (DataManager.visibleHostile() > 1)
                    {
                        DataManager.resetVisibleHostile();
                    }
                }
                if (Hotkeys.EMPTY_MOBCOUNTS.getKeybind().isKeybindHeld())
                {
                    DataManager.resetEntityCount();
                }
            }
        }
        return false;
    }
}