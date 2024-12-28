package eu.minemania.mobcountmod.render;

import java.util.*;

import eu.minemania.mobcountmod.MobCountMod;
import eu.minemania.mobcountmod.config.Configs;
import eu.minemania.mobcountmod.config.InfoToggleHostile;
import eu.minemania.mobcountmod.config.InfoTogglePassive;
import eu.minemania.mobcountmod.counter.DataManager;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class MobCountRenderer
{
    private static final MobCountRenderer INSTANCE = new MobCountRenderer();
    private int totalPassive = 0;
    private int totalHostile = 0;
    private int totalKilledPassive = 0;
    private int totalKilledHostile = 0;
    private long infoUpdateTime;
    private final List<StringHolder> lineWrappersHostile = new ArrayList<>();
    private final List<StringHolder> lineWrappersPassive = new ArrayList<>();
    private final List<String> linesHostile = new ArrayList<>();
    private final List<String> linesPassive = new ArrayList<>();
    private Map<String, Integer> maxEntities = new HashMap<>();

    public static MobCountRenderer getInstance()
    {
        return INSTANCE;
    }

    public static void renderOverlays(DrawContext drawContext)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        DataManager.getInstance().hostileLimit(getInstance().totalHostile);
        if (mc.currentScreen == null && MinecraftClient.isHudEnabled())
        {
            getInstance().renderHUD(drawContext);
        }
    }

    public void renderHUD(DrawContext drawContext)
    {
        long currentTime = System.currentTimeMillis();
        HudAlignment hudAlignment = (HudAlignment) Configs.Generic.HUD_ALIGNMENT.getOptionListValue();
        int colorBg = Configs.Generic.COLOR_BACK_DEFAULT.getColor().intValue;
        int colorFg = Configs.Generic.COLOR_FORE_DEFAULT.getColor().intValue;
        boolean customBg = Configs.Generic.CUSTOM_BG_COLOR.getBooleanValue();
        int yOff = 15;

        if (currentTime - this.infoUpdateTime >= 50)
        {
            this.updateLines();
            this.infoUpdateTime = currentTime;
        }

        if (hudAlignment == HudAlignment.BOTTOM_LEFT || hudAlignment == HudAlignment.BOTTOM_RIGHT)
        {
            yOff = 75;
        }

        if (DataManager.visibleCounter() == 1)
        {
            DataManager.getCounter().updateBBP();
            int xOff = 5;

            if (hudAlignment == HudAlignment.CENTER)
            {
                xOff = -125;
            }
            RenderUtils.renderText(xOff, yOff, 1, colorFg, colorBg, hudAlignment, customBg, true, this.linesPassive, drawContext);
        }

        if (DataManager.visibleHostile() == 1)
        {
            int xOff = 200;

            if (hudAlignment == HudAlignment.CENTER)
            {
                xOff = 125;
            }
            DataManager.getCounter().updateBBH();
            RenderUtils.renderText(xOff, yOff, 1, colorFg, colorBg, hudAlignment, customBg, true, this.linesHostile, drawContext);
        }
    }

    private void updateLines()
    {
        this.lineWrappersHostile.clear();
        this.lineWrappersPassive.clear();
        this.totalHostile = 0;
        this.totalPassive = 0;
        this.totalKilledHostile = 0;
        this.totalKilledPassive = 0;

        List<LinePosPassive> positionsPassive = new ArrayList<>();
        List<LinePosHostile> positionsHostile = new ArrayList<>();


        for (InfoTogglePassive toggle : InfoTogglePassive.values())
        {
            if (toggle.getBooleanValue())
            {
                positionsPassive.add(new LinePosPassive(toggle.getIntegerValue(), toggle));
            }
        }

        for (InfoToggleHostile toggle : InfoToggleHostile.values())
        {
            if (toggle.getBooleanValue())
            {
                positionsHostile.add(new LinePosHostile(toggle.getIntegerValue(), toggle));
            }
        }

        Collections.sort(positionsPassive);
        Collections.sort(positionsHostile);
        maxEntities.clear();
        setTooMuchEntities();

        for (LinePosPassive pos : positionsPassive)
        {
            try
            {
                this.addLinePassive(pos.type);
            }
            catch (Exception e)
            {
                this.addLinePassive(pos.type.getName() + ": exception");
                MobCountMod.logger.error(e.getMessage());
            }
        }

        for (LinePosHostile pos : positionsHostile)
        {
            try
            {
                this.addLineHostile(pos.type);
            }
            catch (Exception e)
            {
                this.addLineHostile(pos.type.getName() + ": exception");
                MobCountMod.logger.error(e.getMessage());
            }
        }

        this.linesHostile.clear();
        this.linesPassive.clear();

        for (StringHolder holder : this.lineWrappersHostile)
        {
            this.linesHostile.add(holder.str);
        }

        for (StringHolder holder : this.lineWrappersPassive)
        {
            this.linesPassive.add(holder.str);
        }
    }

    private String getColor(int amount, boolean passive)
    {
        String color = GuiBase.TXT_WHITE;
        if (passive && amount > 149)
        {
            color = GuiBase.TXT_DARK_RED;
        }
        else if (!passive)
        {
            if (amount > 149)
            { // if 150+ mobs, display in red.
                color = GuiBase.TXT_DARK_RED;
            }
            else
            {
                DataManager.setPlaySoundCount(100);
            }
        }
        return color;
    }

    private void addLineHostile(String text)
    {
        if (!text.isEmpty())
        {
            this.lineWrappersHostile.add(new StringHolder(text));
        }
    }

    private void addLinePassive(String text)
    {
        if (!text.isEmpty())
        {
            this.lineWrappersPassive.add(new StringHolder(text));
        }
    }

    private <T extends Entity> String lineTextP(EntityType<T> entity)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        int size = mc.world.getEntitiesByType(entity, DataManager.getCounter().getPassiveBB(), EntityPredicates.EXCEPT_SPECTATOR).size() - (!StringUtils.translate(entity.getTranslationKey()).equals("Player") ? 0 : (mc.player.isSpectator() ? 0 : 1));
        totalPassive += size;
        int passiveKilled = DataManager.getEntityCount(entity);
        totalKilledPassive += passiveKilled;
        return size == 0 && !Configs.Generic.DISPLAY_ALL.getBooleanValue() ? "" : String.format("%s: %s%d%s%s", StringUtils.translate(entity.getTranslationKey()), size > getTooMuchEntities(entity, Configs.Generic.COUNT_PASSIVE.getIntegerValue()) ? GuiBase.TXT_RED : GuiBase.TXT_GREEN, size, GuiBase.TXT_RST, Configs.Generic.DISPLAY_AMOUNT_KILLED.getBooleanValue() ? " " + StringUtils.translate("mcm.message.mobcounter.killed", passiveKilled) : "");
    }

    private <T extends Entity> String lineTextH(EntityType<T> entity)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        int size = mc.world.getEntitiesByType(entity, DataManager.getCounter().getHostileBB(), EntityPredicates.EXCEPT_SPECTATOR).size();
        totalHostile += size;
        int hostileKilled = DataManager.getEntityCount(entity);
        totalKilledHostile += hostileKilled;
        return size == 0 && !Configs.Generic.DISPLAY_ALL.getBooleanValue() ? "" : String.format("%s: %s%d%s%s", StringUtils.translate(entity.getTranslationKey()), size > getTooMuchEntities(entity, Configs.Generic.COUNT_HOSTILE.getIntegerValue()) ? GuiBase.TXT_RED : GuiBase.TXT_GREEN, size, GuiBase.TXT_RST, Configs.Generic.DISPLAY_AMOUNT_KILLED.getBooleanValue() ? " " + StringUtils.translate("mcm.message.mobcounter.killed", hostileKilled) : "");
    }

    private void addLinePassive(InfoTogglePassive type)
    {
        if (type == InfoTogglePassive.RADIUS_COUNTER)
        {
            this.addLinePassive(String.format("%s %s%s%s%s", StringUtils.translate("mcm.message.mobcounter.radius", DataManager.getCounter().getRadiusP()), getColor(totalPassive, true), StringUtils.translate("mcm.message.mobcounter.total", this.totalPassive), GuiBase.TXT_RST, Configs.Generic.DISPLAY_AMOUNT_KILLED.getBooleanValue() ? " " + StringUtils.translate("mcm.message.mobcounter.total_killed", this.totalKilledPassive) : ""));
        }

        EntityType<?> entityType = DataManager.getCounter().getPassiveEntityType(type);

        if (entityType != null)
        {
            this.addLinePassive(lineTextP(entityType));
        }
    }



    private void addLineHostile(InfoToggleHostile type)
    {
        if (type == InfoToggleHostile.RADIUS_COUNTER)
        {
            this.addLineHostile(String.format("%s %s%s%s%s", StringUtils.translate("mcm.message.mobcounter.radius", DataManager.getCounter().getRadiusH()), getColor(totalHostile, false), StringUtils.translate("mcm.message.mobcounter.total", this.totalHostile), GuiBase.TXT_RST, Configs.Generic.DISPLAY_AMOUNT_KILLED.getBooleanValue() ? " " + StringUtils.translate("mcm.message.mobcounter.total_killed", this.totalKilledHostile) : ""));
        }

        EntityType<?> entityType = DataManager.getCounter().getHostileEntityType(type);

        if (entityType != null)
        {
            this.addLineHostile(lineTextH(entityType));
        }
    }

    private void setTooMuchEntities()
    {
        List<String> list = Configs.Generic.CUSTOM_COUNT.getStrings();
        for (String entry : list)
        {
            String[] parts = entry.split("=");
            if (parts.length == 2)
            {
                try
                {
                    Optional<EntityType<?>> optionalEntityType = EntityType.get(parts[0]);
                    int count = Integer.parseInt(parts[1]);
                    optionalEntityType.ifPresent(type -> {
                        Identifier identifier = Registries.ENTITY_TYPE.getId(optionalEntityType.get());
                        this.maxEntities.put(identifier.toString(), count);
                    });
                }
                catch (Exception e)
                {
                    MobCountMod.logger.error(e.getMessage());
                }
            }
        }
    }

    private int getTooMuchEntities(EntityType<?> entityType, int defaultCount)
    {
        Identifier identifier = Registries.ENTITY_TYPE.getId(entityType);
        String key = identifier.toString();
        if (this.maxEntities.containsKey(key))
        {
            return this.maxEntities.get(key);
        }

        return defaultCount;
    }

    private class StringHolder
    {
        public final String str;

        public StringHolder(String str)
        {
            this.str = str;
        }
    }

    private static class LinePosPassive implements Comparable<LinePosPassive>
    {
        private final int position;
        private final InfoTogglePassive type;

        private LinePosPassive(int position, InfoTogglePassive type)
        {
            this.position = position;
            this.type = type;
        }

        @Override
        public int compareTo(LinePosPassive other)
        {
            if (this.position < 0)
            {
                return other.position >= 0 ? 1 : 0;
            }
            else if (other.position < 0 && this.position >= 0)
            {
                return -1;
            }

            return this.position < other.position ? -1 : (this.position > other.position ? 1 : 0);
        }
    }

    private static class LinePosHostile implements Comparable<LinePosHostile>
    {
        private final int position;
        private final InfoToggleHostile type;

        private LinePosHostile(int position, InfoToggleHostile type)
        {
            this.position = position;
            this.type = type;
        }

        @Override
        public int compareTo(LinePosHostile other)
        {
            if (this.position < 0)
            {
                return other.position >= 0 ? 1 : 0;
            }
            else if (other.position < 0 && this.position >= 0)
            {
                return -1;
            }

            return this.position < other.position ? -1 : (this.position > other.position ? 1 : 0);
        }
    }
}