package eu.minemania.mobcountmod.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import eu.minemania.mobcountmod.MobCountMod;
import eu.minemania.mobcountmod.Reference;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigInteger;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBoolean;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public enum InfoToggleHostile implements IConfigInteger, IHotkeyTogglable
{
    RADIUS_COUNTER("infoRadiusCounter", true, 99, "", "radius counter"),
    BLAZE("infoBlaze", true, 13, "", "blazes"),
    BOGGED("infoBogged", true, 36, "", "boggeds"),
    BREEZE("infoBreeze", true, 35, "", "breezes"),
    CAVE_SPIDER("infoCaveSpider", true, 18, "", "cave spiders"),
    CREEPER("infoCreeper", true, 3, "", "creepers"),
    DROWNED("infoDrowned", true, 19, "", "drowneds"),
    ELDER_GUARDIAN("infoElderGuardian", true, 20, "", "elder guardians"),
    ENDERMAN("infoEnderman", true, 15, "", "endermans"),
    ENDERMITE("infoEndermite", true, 5, "", "endermites"),
    EVOKER("infoEvoker", true, 21, "", "evokers"),
    GHAST("infoGhast", true, 14, "", "ghasts"),
    GUARDIAN("infoGuardian", true, 7, "", "guardians"),
    HOGLIN("infoHoglin", true, 30, "", "hoglins"),
    HUSK("infoHusk", true, 22, "", "husks"),
    ILLUSIONER("infoIllusioner", true, 23, "", "illusioners"),
    MAGMA_CUBE("infoMagmaCube", true, 24, "", "magma cubes"),
    PHANTOM("infoPhantom", true, 9, "", "phantoms"),
    PIGLIN("infoPiglin", true, 31, "", "piglins"),
    PIGLIN_BRUTE("infoPiglinBrute", true, 33, "", "piglin brutes"),
    PILLAGER("infoPillager", true, 8, "", "pillagers"),
    RAVAGER("infoRavager", true, 10, "", "ravagers"),
    SHULKER("infoShulker", true, 16, "", "shulkers"),
    SILVERFISH("infoSilverfish", true, 17, "", "silverfishes"),
    SKELETON("infoSkeleton", true, 11, "", "skeletons"),
    SLIME("infoSlime", true, 6, "", "slimes"),
    SPIDER("infoSpider", true, 2, "", "spiders"),
    STRAY("infoStray", true, 25, "", "strays"),
    VEX("infoVex", true, 12, "", "vexes"),
    VINDICATOR("infoVindicator", true, 26, "", "vindicators"),
    WARDEN("infoWarden", true, 34, "", "wardens"),
    WITCH("infoWitch", true, 4, "", "witches"),
    WITHER("infoWither", true, 34, "", "withers"),
    WITHER_SKELETON("infoWitherSkeleton", true, 27, "", "wither skeletons"),
    ZOGLIN("infoZoglin", true, 32, "", "zoglins"),
    ZOMBIE("infoZombie", true, 1, "", "zombies"),
    ZOMBIE_VILLAGER("infoZombieVillager", true, 29, "", "zombie villagers"),
    ZOMBIFIED_PIGLIN("infoZombifiedPiglin", true, 28, "", "zombified piglins");

    private static final String INFO_KEY = Reference.MOD_ID + ".config.info_toggle";

    private final String name;
    private final String extra;
    private String comment;
    private String prettyName;
    private String translatedName;
    private final IKeybind keybind;
    private final boolean defaultValueBoolean;
    private final int defaultLinePosition;
    private boolean valueBoolean;
    private int linePosition;

    InfoToggleHostile(String name, boolean defaultValue, int linePosition, String defaultHotkey, String extra)
    {
        this(name, defaultValue, linePosition, defaultHotkey, KeybindSettings.DEFAULT, extra);
    }

    InfoToggleHostile(String name, boolean defaultValue, int linePosition, String defaultHotkey, KeybindSettings settings, String extra)
    {
        this.name = name;
        this.extra = extra;
        this.comment = buildTranslateName("description");
        this.prettyName = buildTranslateName(name, "prettyName");
        this.translatedName = buildTranslateName(name, "name");
        this.valueBoolean = defaultValue;
        this.defaultValueBoolean = defaultValue;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
        this.keybind.setCallback(new KeyCallbackToggleBoolean(this));
        this.linePosition = linePosition;
        this.defaultLinePosition = linePosition;
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.HOTKEY;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getPrettyName()
    {
        return StringUtils.getTranslatedOrFallback(this.prettyName, this.prettyName.isEmpty() ? StringUtils.splitCamelCase(this.name) : this.prettyName);
    }

    @Override
    public String getComment()
    {
        return StringUtils.getTranslatedOrFallback(this.comment, this.comment).replaceAll("\\$extra\\$", this.extra);
    }

    @Override
    public String getTranslatedName()
    {
        return StringUtils.getTranslatedOrFallback(this.translatedName, this.name);
    }

    @Override
    public void setPrettyName(String prettyName)
    {
        this.prettyName = prettyName;
    }

    @Override
    public void setTranslatedName(String translatedName)
    {
        this.translatedName = translatedName;
    }

    @Override
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.valueBoolean = element.getAsBoolean();
            }
            else
            {
                MobCountMod.logger.warn("Failed to read config value for '{}' from the JSON config", this.getName());
            }
        }
        catch (Exception e)
        {
            MobCountMod.logger.warn("Failed to read config value for '{}' from the JSON config", this.getName(), e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.valueBoolean);
    }

    @Override
    public boolean isModified()
    {
        return this.valueBoolean != this.defaultValueBoolean;
    }

    @Override
    public void resetToDefault()
    {
        this.valueBoolean = this.defaultValueBoolean;
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValueBoolean);
    }

    @Override
    public void setValueFromString(String value)
    {
        try
        {
            this.valueBoolean = Boolean.parseBoolean(value);
        }
        catch (Exception e)
        {
            MobCountMod.logger.warn("Failed to read config value for '{}' from the JSON config", this.getName(), e);
        }
    }

    @Override
    public boolean isModified(String newValue)
    {
        return !String.valueOf(this.defaultValueBoolean).equals(newValue);
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.valueBoolean);
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.valueBoolean;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValueBoolean;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        this.valueBoolean = value;
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public int getIntegerValue()
    {
        return this.linePosition;
    }

    @Override
    public int getDefaultIntegerValue()
    {
        return this.defaultLinePosition;
    }

    @Override
    public void setIntegerValue(int value)
    {
        this.linePosition = value;
    }

    @Override
    public int getMinIntegerValue()
    {
        return 0;
    }

    @Override
    public int getMaxIntegerValue()
    {
        return InfoToggleHostile.values().length - 1;
    }

    private static String buildTranslateName(String type)
    {
        return INFO_KEY + "." + type;
    }
    private static String buildTranslateName(String name, String type)
    {
        return INFO_KEY + "." + type + "." + name;
    }
}