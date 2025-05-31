package eu.minemania.mobcountmod.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import eu.minemania.mobcountmod.Reference;
import eu.minemania.mobcountmod.config.Configs;
import eu.minemania.mobcountmod.config.Hotkeys;
import eu.minemania.mobcountmod.config.InfoToggleHostile;
import eu.minemania.mobcountmod.config.InfoTogglePassive;
import eu.minemania.mobcountmod.counter.DataManager;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiConfigs extends GuiConfigsBase
{
    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, "mobcountmod.gui.title.configs");
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        x += this.createButton(x, y, -1, ConfigGuiTab.GENERIC);
        x += this.createButton(x, y, -1, ConfigGuiTab.INFO_TOGGLES);
        x += this.createButton(x, y, -1, ConfigGuiTab.INFO_LINE_ORDER_HOSTILE);
        x += this.createButton(x, y, -1, ConfigGuiTab.INFO_LINE_ORDER_PASSIVE);
        x += this.createButton(x, y, -1, ConfigGuiTab.HOTKEYS);
        x += this.createButton(x, y, -1, ConfigGuiTab.INFO_HOTKEYS_HOSTILE);
        x += this.createButton(x, y, -1, ConfigGuiTab.INFO_HOTKEYS_PASSIVE);
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(DataManager.getConfigGuiTab() != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth()
    {
        ConfigGuiTab tab = DataManager.getConfigGuiTab();

        if (tab == ConfigGuiTab.GENERIC)
        {
            return 140;
        }
        else if (tab == ConfigGuiTab.INFO_LINE_ORDER_PASSIVE ||
                tab == ConfigGuiTab.INFO_LINE_ORDER_HOSTILE ||
                tab == ConfigGuiTab.INFO_TOGGLES)
        {
            return 100;
        }

        return super.getConfigWidth();
    }

    @Override
    protected boolean useKeybindSearch()
    {
        return DataManager.getConfigGuiTab() == ConfigGuiTab.HOTKEYS ||
                DataManager.getConfigGuiTab() == ConfigGuiTab.INFO_HOTKEYS_PASSIVE ||
                DataManager.getConfigGuiTab() == ConfigGuiTab.INFO_HOTKEYS_HOSTILE;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = DataManager.getConfigGuiTab();

        if (tab == ConfigGuiTab.GENERIC)
        {
            configs = Configs.Generic.OPTIONS;
        }
        else if (tab == ConfigGuiTab.HOTKEYS)
        {
            configs = Hotkeys.HOTKEY_LIST;
        }
        else if (tab == ConfigGuiTab.INFO_TOGGLES)
        {
            List<IConfigValue> stuff = new ArrayList<>();
            stuff.addAll(ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(InfoTogglePassive.values())));
            stuff.addAll(ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(InfoToggleHostile.values())));
            configs = stuff;
        }
        else if (tab == ConfigGuiTab.INFO_LINE_ORDER_PASSIVE)
        {
            configs = ConfigUtils.createConfigWrapperForType(ConfigType.INTEGER, ImmutableList.copyOf(InfoTogglePassive.values()));
        }
        else if (tab == ConfigGuiTab.INFO_LINE_ORDER_HOSTILE)
        {
            configs = ConfigUtils.createConfigWrapperForType(ConfigType.INTEGER, ImmutableList.copyOf(InfoToggleHostile.values()));
        }
        else if (tab == ConfigGuiTab.INFO_HOTKEYS_PASSIVE)
        {
            configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(InfoTogglePassive.values()));
        }
        else if (tab == ConfigGuiTab.INFO_HOTKEYS_HOSTILE)
        {
            configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(InfoToggleHostile.values()));
        }
        else
        {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    @Override
    protected void onSettingsChanged()
    {
        super.onSettingsChanged();
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final GuiConfigs parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, GuiConfigs parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            DataManager.setConfigGuiTab(this.tab);

            this.parent.reCreateListWidget();
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab
    {
        GENERIC("mobcountmod.gui.button.config_gui.generic"),
        HOTKEYS("mobcountmod.gui.button.config_gui.hotkeys"),
        INFO_TOGGLES("mobcountmod.gui.button.config_gui.info_toggles"),
        INFO_LINE_ORDER_HOSTILE("mobcountmod.gui.button.config_gui.info_line_order.hostile"),
        INFO_LINE_ORDER_PASSIVE("mobcountmod.gui.button.config_gui.info_line_order.passive"),
        INFO_HOTKEYS_HOSTILE("mobcountmod.gui.button.config_gui.info_hotkeys.hostile"),
        INFO_HOTKEYS_PASSIVE("mobcountmod.gui.button.config_gui.info_hotkeys.passive");

        private final String translationKey;

        private ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}