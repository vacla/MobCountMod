package eu.minemania.mobcountmod.counter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import eu.minemania.mobcountmod.MobCountMod;
import eu.minemania.mobcountmod.Reference;
import eu.minemania.mobcountmod.command.MobCountCommand;
import eu.minemania.mobcountmod.config.Configs;
import eu.minemania.mobcountmod.gui.GuiConfigs.ConfigGuiTab;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class DataManager
{
    private static final DataManager INSTANCE = new DataManager();
    private static boolean canSave;

    private final MobCounter counter = new MobCounter();

    private int counterVisible = 0; // 0 - not visible, 1 - compact, 2 - expanded
    private int hostileVisible = 0;
    private int playSoundCount = 0; // counts up so sound plays once per sec
    private int sendMsgCount = 0; // counts up so message sends every 5 minutes
    private HashMap<EntityType<?>, Integer> entities = new HashMap<>();

    public static DataManager getInstance()
    {
        return INSTANCE;
    }

    public static MobCounter getCounter()
    {
        return getInstance().counter;
    }

    public static int visibleCounter()
    {
        return getInstance().counterVisible;
    }

    public static void upVisibleCounter()
    {
        getInstance().counterVisible++;
    }

    public static void resetVisibleCounter()
    {
        getInstance().counterVisible = 0;
    }

    public static int visibleHostile()
    {
        return getInstance().hostileVisible;
    }

    public static void upVisibleHostile()
    {
        getInstance().hostileVisible++;
    }

    public static void resetVisibleHostile()
    {
        getInstance().hostileVisible = 0;
    }

    public static void setPlaySoundCount(int count)
    {
        getInstance().playSoundCount = count;
    }

    private static ConfigGuiTab configGuiTab = ConfigGuiTab.GENERIC;

    public static ConfigGuiTab getConfigGuiTab()
    {
        return configGuiTab;
    }

    public static void setConfigGuiTab(ConfigGuiTab tab)
    {
        configGuiTab = tab;
    }

    /**
     * Plays the sound "note.bass" if there are 150+ hostile mobs in the hostile mob count radius
     */
    public void hostileLimit(int hostileCount)
    {
        if (this.playSoundCount != 0)
        {
            this.playSoundCount++;
        }
        if (this.sendMsgCount != 0)
        {
            this.sendMsgCount++;
        }
        if (hostileCount > 149)
        {
            if (this.playSoundCount == 0)
            {
                SoundEvent soundEvent = Registries.SOUND_EVENT.get(Identifier.of(Configs.Generic.SOUNDFILE.getStringValue()));
                if (soundEvent != null)
                {
                    SoundInstance sound = PositionedSoundInstance.master(soundEvent, 1.0F);
                    MinecraftClient.getInstance().getSoundManager().play(sound);
                }
                this.playSoundCount++;
            }
            if (this.playSoundCount > 100)
            {
                this.playSoundCount = -1;
            }
            if (this.sendMsgCount == 0)
            {
                if (Configs.Generic.NOTIFYFACTION.getBooleanValue())
                {
                    MinecraftClient.getInstance().player.networkHandler.sendChatCommand("ch qm f Automated Message: " + hostileCount + " mobz. Kill pl0x.");
                }
                if (Configs.Generic.MESSAGE_LIST.getStrings() != null && Configs.Generic.MESSAGE_LIST.getStrings().size() > 0)
                {
                    for (String player : Configs.Generic.MESSAGE_LIST.getStrings())
                    {
                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand("m " + player + " Automated Message: " + hostileCount + " mobz. Kill pl0x.");
                    }
                }
                this.sendMsgCount++;
            }
            if (this.sendMsgCount > 10000)
            {
                this.sendMsgCount = -1;
            }
        }
    }

    public static void load()
    {
        Path file = getCurrentStorageFile();

        JsonElement element = JsonUtils.parseJsonFileAsPath(file);

        if (element != null && element.isJsonObject())
        {

            JsonObject root = element.getAsJsonObject();

            if (JsonUtils.hasString(root, "config_gui_tab"))
            {
                try
                {
                    configGuiTab = ConfigGuiTab.valueOf(root.get("config_gui_tab").getAsString());
                }
                catch (Exception ignored)
                {
                }

                if (configGuiTab == null)
                {
                    configGuiTab = ConfigGuiTab.GENERIC;
                }
            }
        }

        canSave = true;
    }

    public static void save()
    {
        save(false);
    }

    public static void save(boolean forceSave)
    {
        if (!canSave && !forceSave)
        {
            return;
        }

        JsonObject root = new JsonObject();

        root.add("config_gui_tab", new JsonPrimitive(configGuiTab.name()));

        Path file = getCurrentStorageFile();
        JsonUtils.writeJsonToFileAsPath(root, file);

        canSave = false;
    }

    private static Path getCurrentStorageFile()
    {
        Path dir = getCurrentConfigDirectory();

        if (!Files.exists(dir))
        {
            FileUtils.createDirectoriesIfMissing(dir);
        }

        if (!Files.isDirectory(dir))
        {
            MobCountMod.logger.warn("Failed to create the config directory '{}'", dir.toAbsolutePath());
        }

        return dir.resolve(getStorageFileName());
    }

    private static String getStorageFileName()
    {
        String name = StringUtils.getWorldOrServerName();

        if (name == null)
        {
            return Reference.MOD_ID + "_default.json";
        }

        return Reference.MOD_ID + "_" + name + ".json";
    }

    public static Path getCurrentConfigDirectory()
    {
        return FileUtils.getConfigDirectoryAsPath().resolve(Reference.MOD_ID);
    }

    public static <T extends Entity> Integer getEntityCount(EntityType<T> entity)
    {
        return INSTANCE.entities.getOrDefault(entity, 0);
    }

    public static <T extends Entity> void addEntityCount(EntityType<T> entity)
    {
        INSTANCE.entities.compute(entity, (key, count) -> count == null ? 1 : count + 1);
    }

    public static void resetEntityCount()
    {
        INSTANCE.entities.clear();
    }

    public static File getMobCounterModBaseDirectory()
    {
        Path pathDir = FileUtils.getMinecraftDirectoryAsPath().resolve("mobcountermod");
        File dir = FileUtils.getCanonicalFileIfPossible(pathDir.toFile());

        if (!dir.exists() && !dir.mkdirs())
        {
            MobCountMod.logger.warn("Failed to create the mobcountermod directory '{}'", dir.getAbsolutePath());
        }

        return dir;
    }

    public static void saveKilledFile(ServerCommandSource serverCommandSource)
    {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        Calendar calendar = Calendar.getInstance();
        String fileName = String.format("%4d-%02d-%02d-%02d.%02d.%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

        File file = new File(getMobCounterModBaseDirectory(), fileName + ".txt");
        try
        {
            getCounter().save(file);
            StringUtils.sendOpenFileChatMessage(playerEntity, "%s", file);
            MobCountCommand.localOutputT(serverCommandSource, "mobcountmod.message.killed.saved", fileName);
        }
        catch (IOException e)
        {
            MobCountMod.logger.error("error saving killed mob count to " + file, e);
            MobCountCommand.localErrorT(serverCommandSource, "mobcountmod.message.killed.not_saved", fileName);
        }
    }
}