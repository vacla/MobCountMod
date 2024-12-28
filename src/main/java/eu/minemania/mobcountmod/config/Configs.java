package eu.minemania.mobcountmod.config;

import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigHandler;

import java.io.File;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import eu.minemania.mobcountmod.Reference;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

public class Configs implements IConfigHandler
{
    /**
     * Config file for mod.
     */
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    /**
     * Default Generic configuration.
     */
    public static class Generic
    {
        public static final ConfigColor COLOR_BACK_DEFAULT = new ConfigColor("colorBackDefault", "#30FFF0E0", "mcm.description.config.color_back_default");
        public static final ConfigColor COLOR_FORE_DEFAULT = new ConfigColor("colorForeDefault", "#00E0E0E0", "mcm.description.config.color_fore_default");
        public static final ConfigInteger COUNT_HOSTILE = new ConfigInteger("countHostile", 16, "mcm.description.config.count_hostile");
        public static final ConfigInteger COUNT_PASSIVE = new ConfigInteger("countPassive", 16, "mcm.description.config.count_passive");
        public static final ConfigBoolean CUSTOM_BG_COLOR = new ConfigBoolean("customBgColor", false, "mcm.description.config.custom_bg_color");
        public static final ConfigBoolean DISPLAY_ALL = new ConfigBoolean("displayAll", false, "mcm.description.config.display_all");
        public static final ConfigBoolean ENABLED = new ConfigBoolean("enabled", true, "mcm.description.config.enabled");
        public static final ConfigOptionList HUD_ALIGNMENT = new ConfigOptionList("hudAlignment", HudAlignment.TOP_LEFT, "mcm.description.config.hudalignment");
        public static final ConfigStringList MESSAGE_LIST = new ConfigStringList("messageList", ImmutableList.of(), "mcm.description.config.message_list");
        public static final ConfigBoolean NOTIFYFACTION = new ConfigBoolean("notifyFaction", false, "mcm.description.config.notifyfaction");
        public static final ConfigInteger RADIUS_HOSTILE = new ConfigInteger("radiusHostile", 16, "mcm.description.config.radius_hostile");
        public static final ConfigInteger RADIUS_PASSIVE = new ConfigInteger("radiusPassive", 16, "mcm.description.config.radius_passive");
        public static final ConfigString SOUNDFILE = new ConfigString("soundFile", "block.note_block.bass", "mcm.description.config.soundfile");
        public static final ConfigBoolean XP5 = new ConfigBoolean("xp5", false, "mcm.description.config.xp5");
        public static final ConfigBoolean DISPLAY_AMOUNT_KILLED = new ConfigBoolean("displayAmountKilled", false, "mcm.description.config.display_amount_killed");
        public static final ConfigStringList CUSTOM_COUNT = new ConfigStringList("customCount", ImmutableList.of(), "mcm.description.config.custom_count");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                COLOR_BACK_DEFAULT,
                COLOR_FORE_DEFAULT,
                COUNT_HOSTILE,
                COUNT_PASSIVE,
                CUSTOM_BG_COLOR,
                CUSTOM_COUNT,
                DISPLAY_ALL,
                DISPLAY_AMOUNT_KILLED,
                ENABLED,
                HUD_ALIGNMENT,
                MESSAGE_LIST,
                NOTIFYFACTION,
                RADIUS_HOSTILE,
                RADIUS_PASSIVE,
                SOUNDFILE,
                XP5
        );
    }

    /**
     * Loads configurations from configuration file.
     */
    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();
                JsonObject objInfoLineOrdersHostile = JsonUtils.getNestedObject(root, "InfoLineOrdersHostile", false);
                JsonObject objInfoLineOrdersPassive = JsonUtils.getNestedObject(root, "InfoLineOrdersPassive", false);

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);
                ConfigUtils.readHotkeyToggleOptions(root, "InfoHotkeysHostile", "InfoTypeTogglesHostile", ImmutableList.copyOf(InfoToggleHostile.values()));
                ConfigUtils.readHotkeyToggleOptions(root, "InfoHotkeysPassive", "InfoTypeTogglesPassive", ImmutableList.copyOf(InfoTogglePassive.values()));

                if (objInfoLineOrdersHostile != null)
                {
                    for (InfoToggleHostile toggle : InfoToggleHostile.values())
                    {
                        if (JsonUtils.hasInteger(objInfoLineOrdersHostile, toggle.getName()))
                        {
                            toggle.setIntegerValue(JsonUtils.getInteger(objInfoLineOrdersHostile, toggle.getName()));
                        }
                    }
                }

                if (objInfoLineOrdersPassive != null)
                {
                    for (InfoTogglePassive toggle : InfoTogglePassive.values())
                    {
                        if (JsonUtils.hasInteger(objInfoLineOrdersPassive, toggle.getName()))
                        {
                            toggle.setIntegerValue(JsonUtils.getInteger(objInfoLineOrdersPassive, toggle.getName()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Saves configurations to configuration file.
     */
    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();
            JsonObject objInfoLineOrdersHostile = JsonUtils.getNestedObject(root, "InfoLineOrdersHostile", true);
            JsonObject objInfoLineOrdersPassive = JsonUtils.getNestedObject(root, "InfoLineOrdersPassive", true);

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);
            ConfigUtils.writeHotkeyToggleOptions(root, "InfoHotkeysHostile", "InfoTypeTogglesHostile", ImmutableList.copyOf(InfoToggleHostile.values()));
            ConfigUtils.writeHotkeyToggleOptions(root, "InfoHotkeysPassive", "InfoTypeTogglesPassive", ImmutableList.copyOf(InfoTogglePassive.values()));

            for (InfoToggleHostile toggle : InfoToggleHostile.values())
            {
                objInfoLineOrdersHostile.add(toggle.getName(), new JsonPrimitive(toggle.getIntegerValue()));
            }

            for (InfoTogglePassive toggle : InfoTogglePassive.values())
            {
                objInfoLineOrdersPassive.add(toggle.getName(), new JsonPrimitive(toggle.getIntegerValue()));
            }

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}