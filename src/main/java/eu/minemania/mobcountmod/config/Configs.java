package eu.minemania.mobcountmod.config;

import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigHandler;

import java.nio.file.Files;
import java.nio.file.Path;

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
        public static final ConfigColor COLOR_BACK_DEFAULT = new ConfigColor("colorBackDefault", "#30FFF0E0", "mobcountmod.config.color_back_default.description");
        public static final ConfigColor COLOR_FORE_DEFAULT = new ConfigColor("colorForeDefault", "#00E0E0E0", "mobcountmod.config.color_fore_default.description");
        public static final ConfigInteger COUNT_HOSTILE = new ConfigInteger("countHostile", 16, "mobcountmod.config.count_hostile.description");
        public static final ConfigInteger COUNT_PASSIVE = new ConfigInteger("countPassive", 16, "mobcountmod.config.count_passive.description");
        public static final ConfigBoolean CUSTOM_BG_COLOR = new ConfigBoolean("customBgColor", false, "mobcountmod.config.custom_bg_color.description");
        public static final ConfigBoolean DISPLAY_ALL = new ConfigBoolean("displayAll", false, "mobcountmod.config.display_all.description");
        public static final ConfigBoolean ENABLED = new ConfigBoolean("enabled", true, "mobcountmod.config.enabled.description");
        public static final ConfigOptionList HUD_ALIGNMENT = new ConfigOptionList("hudAlignment", HudAlignment.TOP_LEFT, "mobcountmod.config.hudalignment.description");
        public static final ConfigStringList MESSAGE_LIST = new ConfigStringList("messageList", ImmutableList.of(), "mobcountmod.config.message_list.description");
        public static final ConfigBoolean NOTIFYFACTION = new ConfigBoolean("notifyFaction", false, "mobcountmod.config.notifyfaction.description");
        public static final ConfigInteger RADIUS_HOSTILE = new ConfigInteger("radiusHostile", 16, "mobcountmod.config.radius_hostile.description");
        public static final ConfigInteger RADIUS_PASSIVE = new ConfigInteger("radiusPassive", 16, "mobcountmod.config.radius_passive.description");
        public static final ConfigString SOUNDFILE = new ConfigString("soundFile", "block.note_block.bass", "mobcountmod.config.soundfile.description");
        public static final ConfigBoolean XP5 = new ConfigBoolean("xp5", false, "mobcountmod.config.xp5.description");
        public static final ConfigBoolean DISPLAY_AMOUNT_KILLED = new ConfigBoolean("displayAmountKilled", false, "mobcountmod.config.display_amount_killed.description");
        public static final ConfigStringList CUSTOM_COUNT = new ConfigStringList("customCount", ImmutableList.of(), "mobcountmod.config.custom_count.description");

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
        Path configFile = FileUtils.getConfigDirectoryAsPath().resolve(CONFIG_FILE_NAME);

        if (Files.exists(configFile) && Files.isReadable(configFile))
        {
            JsonElement element = JsonUtils.parseJsonFileAsPath(configFile);

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
        Path dir = FileUtils.getConfigDirectoryAsPath();

        if (!Files.exists(dir))
        {
            FileUtils.createDirectoriesIfMissing(dir);
        }

        if (Files.isDirectory(dir))
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

            JsonUtils.writeJsonToFileAsPath(root, dir.resolve(CONFIG_FILE_NAME));
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