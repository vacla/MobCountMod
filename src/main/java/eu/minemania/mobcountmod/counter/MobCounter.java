package eu.minemania.mobcountmod.counter;

import eu.minemania.mobcountmod.config.Configs;
import eu.minemania.mobcountmod.config.InfoToggleHostile;
import eu.minemania.mobcountmod.config.InfoTogglePassive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.io.*;

public class MobCounter
{
    private int radiusH;
    private Box boundingBoxH;
    private int radiusP;
    private Box boundingBoxP;

    public MobCounter()
    {
        Configs.Generic.XP5.setBooleanValue(false);

        this.radiusH = Configs.Generic.RADIUS_HOSTILE.getIntegerValue();
        this.radiusP = Configs.Generic.RADIUS_PASSIVE.getIntegerValue();

        this.boundingBoxH = new Box(0, 0, 0, 0, 0, 0);
        this.boundingBoxP = new Box(0, 0, 0, 0, 0, 0);
    }

    public Box getPassiveBB()
    {
        return this.boundingBoxP;
    }

    public Box getHostileBB()
    {
        return this.boundingBoxH;
    }

    public void updateBBP()
    {
        PlayerEntity player = MinecraftClient.getInstance().player;

        int x = (int) player.getX();
        int y = (int) player.getY();
        int z = (int) player.getZ();

        this.boundingBoxP = new Box(x - this.radiusP, y - this.radiusP, z - this.radiusP, x + this.radiusP, y + this.radiusP, z + this.radiusP);
    }

    public void updateBBH()
    {
        if (!Configs.Generic.XP5.getBooleanValue())
        {
            PlayerEntity player = MinecraftClient.getInstance().player;

            int x = (int) player.getX();
            int y = (int) player.getY();
            int z = (int) player.getZ();

            this.boundingBoxH = new Box(x - this.radiusH, y - this.radiusH, z - this.radiusH, x + this.radiusH, y + this.radiusH, z + this.radiusH);
        }
    }

    public int getRadiusP()
    {
        return this.radiusP;
    }

    public void increaseRadiusP()
    {
        radiusP++;
    }

    public void decreaseRadiusP()
    {
        if (this.radiusP > 0)
        {
            this.radiusP--;
        }
    }

    public int getRadiusH()
    {
        return this.radiusH;
    }

    public void increaseRadiusH()
    {
        this.radiusH++;
    }

    public void decreaseRadiusH()
    {
        if (this.radiusH > 0)
        {
            this.radiusH--;
        }
    }

    public void setRadius(int radius, boolean passive)
    {
        if (passive)
        {
            this.radiusP = radius;
        }
        else
        {
            this.radiusH = radius;
        }
    }

    public void setXP5(boolean isOn)
    {
        if (isOn)
        {
            this.setXP5bounding();
        }
    }

    private void setXP5bounding()
    {
        this.boundingBoxH = new Box(5229, 5, -4700, 5250, 34, -4692);
    }

    public synchronized void save(File file) throws IOException
    {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file))))
        {
            for (InfoToggleHostile info : InfoToggleHostile.values())
            {
                if (info.getBooleanValue())
                {
                    EntityType<?> entityType = getHostileEntityType(info);
                    writeKilledData(writer, entityType);
                }
            }

            for (InfoTogglePassive info : InfoTogglePassive.values())
            {
                if (info.getBooleanValue())
                {
                    EntityType<?> entityType = getPassiveEntityType(info);
                    writeKilledData(writer, entityType);
                }
            }
        }
    }

    private void writeKilledData(PrintWriter writer, EntityType<?> type)
    {
        int total = DataManager.getEntityCount(type);
        if (total <= 0)
        {
            return;
        }
        writer.format("%s|%d\n", type.getName().getString(), total);
    }

    public EntityType<?> getPassiveEntityType(InfoTogglePassive type)
    {
        if (type == InfoTogglePassive.ALLAY)
        {
            return EntityType.ALLAY;
        }
        else if (type == InfoTogglePassive.ARMADILLO)
        {
            return EntityType.ARMADILLO;
        }
        else if (type == InfoTogglePassive.AXOLOTL)
        {
            return EntityType.AXOLOTL;
        }
        else if (type == InfoTogglePassive.BAT)
        {
            return EntityType.BAT;
        }
        else if (type == InfoTogglePassive.BEE)
        {
            return EntityType.BEE;
        }
        else if (type == InfoTogglePassive.CAMEL)
        {
            return EntityType.CAMEL;
        }
        else if (type == InfoTogglePassive.CAT)
        {
            return EntityType.CAT;
        }
        else if (type == InfoTogglePassive.CHICKEN)
        {
            return EntityType.CHICKEN;
        }
        else if (type == InfoTogglePassive.COW)
        {
            return EntityType.COW;
        }
        else if (type == InfoTogglePassive.DOLPHIN)
        {
            return EntityType.DOLPHIN;
        }
        else if (type == InfoTogglePassive.DONKEY)
        {
            return EntityType.DONKEY;
        }
        else if (type == InfoTogglePassive.FISH)
        {
            return EntityType.TROPICAL_FISH;
        }
        else if (type == InfoTogglePassive.FOX)
        {
            return EntityType.FOX;
        }
        else if (type == InfoTogglePassive.FROG)
        {
            return EntityType.FROG;
        }
        else if (type == InfoTogglePassive.GLOWSQUID)
        {
            return EntityType.GLOW_SQUID;
        }
        else if (type == InfoTogglePassive.GOAT)
        {
            return EntityType.GOAT;
        }
        else if (type == InfoTogglePassive.HORSE)
        {
            return EntityType.HORSE;
        }
        else if (type == InfoTogglePassive.IRONGOLEM)
        {
            return EntityType.IRON_GOLEM;
        }
        else if (type == InfoTogglePassive.ITEM)
        {
            return EntityType.ITEM;
        }
        else if (type == InfoTogglePassive.LLAMA)
        {
            return EntityType.LLAMA;
        }
        else if (type == InfoTogglePassive.MOOSHROOM)
        {
            return EntityType.MOOSHROOM;
        }
        else if (type == InfoTogglePassive.MULE)
        {
            return EntityType.MULE;
        }
        else if (type == InfoTogglePassive.OCELOT)
        {
            return EntityType.OCELOT;
        }
        else if (type == InfoTogglePassive.PANDA)
        {
            return EntityType.PANDA;
        }
        else if (type == InfoTogglePassive.PARROT)
        {
            return EntityType.PARROT;
        }
        else if (type == InfoTogglePassive.PIG)
        {
            return EntityType.PIG;
        }
        else if (type == InfoTogglePassive.PLAYER)
        {
            return EntityType.PLAYER;
        }
        else if (type == InfoTogglePassive.POLARBEAR)
        {
            return EntityType.POLAR_BEAR;
        }
        else if (type == InfoTogglePassive.RABBIT)
        {
            return EntityType.RABBIT;
        }
        else if (type == InfoTogglePassive.SHEEP)
        {
            return EntityType.SHEEP;
        }
        else if (type == InfoTogglePassive.SKELETON_HORSE)
        {
            return EntityType.SKELETON_HORSE;
        }
        else if (type == InfoTogglePassive.SNIFFER)
        {
            return EntityType.SNIFFER;
        }
        else if (type == InfoTogglePassive.SNOW_GOLEM)
        {
            return EntityType.SNOW_GOLEM;
        }
        else if (type == InfoTogglePassive.SQUID)
        {
            return EntityType.SQUID;
        }
        else if (type == InfoTogglePassive.STRIDER)
        {
            return EntityType.STRIDER;
        }
        else if (type == InfoTogglePassive.TADPOLE)
        {
            return EntityType.TADPOLE;
        }
        else if (type == InfoTogglePassive.TRADER_LLAMA)
        {
            return EntityType.TRADER_LLAMA;
        }
        else if (type == InfoTogglePassive.TURTLE)
        {
            return EntityType.TURTLE;
        }
        else if (type == InfoTogglePassive.VILLAGER)
        {
            return EntityType.VILLAGER;
        }
        else if (type == InfoTogglePassive.WANDERING_TRADER)
        {
            return EntityType.WANDERING_TRADER;
        }
        else if (type == InfoTogglePassive.WOLF)
        {
            return EntityType.WOLF;
        }
        else if (type == InfoTogglePassive.ZOMBIE_HORSE)
        {
            return EntityType.ZOMBIE_HORSE;
        }

        return null;
    }

    public EntityType<?> getHostileEntityType(InfoToggleHostile type)
    {
        if (type == InfoToggleHostile.BLAZE)
        {
            return EntityType.BLAZE;
        }
        else if (type == InfoToggleHostile.BOGGED)
        {
            return EntityType.BOGGED;
        }
        else if (type == InfoToggleHostile.BREEZE)
        {
            return EntityType.BREEZE;
        }
        else if (type == InfoToggleHostile.CAVE_SPIDER)
        {
            return EntityType.CAVE_SPIDER;
        }
        else if (type == InfoToggleHostile.CREAKING)
        {
            return EntityType.CREAKING;
        }
        else if (type == InfoToggleHostile.CREAKING_TRANSIENT)
        {
            return EntityType.CREAKING_TRANSIENT;
        }
        else if (type == InfoToggleHostile.CREEPER)
        {
            return EntityType.CREEPER;
        }
        else if (type == InfoToggleHostile.DROWNED)
        {
            return EntityType.DROWNED;
        }
        else if (type == InfoToggleHostile.ELDER_GUARDIAN)
        {
            return EntityType.ELDER_GUARDIAN;
        }
        else if (type == InfoToggleHostile.ENDERMAN)
        {
            return EntityType.ENDERMAN;
        }
        else if (type == InfoToggleHostile.ENDERMITE)
        {
            return EntityType.ENDERMITE;
        }
        else if (type == InfoToggleHostile.EVOKER)
        {
            return EntityType.EVOKER;
        }
        else if (type == InfoToggleHostile.GHAST)
        {
            return EntityType.GHAST;
        }
        else if (type == InfoToggleHostile.GUARDIAN)
        {
            return EntityType.GUARDIAN;
        }
        else if (type == InfoToggleHostile.HOGLIN)
        {
            return EntityType.HOGLIN;
        }
        else if (type == InfoToggleHostile.HUSK)
        {
            return EntityType.HUSK;
        }
        else if (type == InfoToggleHostile.ILLUSIONER)
        {
            return EntityType.ILLUSIONER;
        }
        else if (type == InfoToggleHostile.MAGMA_CUBE)
        {
            return EntityType.MAGMA_CUBE;
        }
        else if (type == InfoToggleHostile.PHANTOM)
        {
            return EntityType.PHANTOM;
        }
        else if (type == InfoToggleHostile.PIGLIN)
        {
            return EntityType.PIGLIN;
        }
        else if (type == InfoToggleHostile.PILLAGER)
        {
            return EntityType.PILLAGER;
        }
        else if (type == InfoToggleHostile.PIGLIN_BRUTE)
        {
            return EntityType.PIGLIN_BRUTE;
        }
        else if (type == InfoToggleHostile.RAVAGER)
        {
            return EntityType.RAVAGER;
        }
        else if (type == InfoToggleHostile.SHULKER)
        {
            return EntityType.SHULKER;
        }
        else if (type == InfoToggleHostile.SILVERFISH)
        {
            return EntityType.SILVERFISH;
        }
        else if (type == InfoToggleHostile.SKELETON)
        {
            return EntityType.SKELETON;
        }
        else if (type == InfoToggleHostile.SLIME)
        {
            return EntityType.SLIME;
        }
        else if (type == InfoToggleHostile.SPIDER)
        {
            return EntityType.SPIDER;
        }
        else if (type == InfoToggleHostile.STRAY)
        {
            return EntityType.STRAY;
        }
        else if (type == InfoToggleHostile.VEX)
        {
            return EntityType.VEX;
        }
        else if (type == InfoToggleHostile.VINDICATOR)
        {
            return EntityType.VINDICATOR;
        }
        else if (type == InfoToggleHostile.WARDEN)
        {
            return EntityType.WARDEN;
        }
        else if (type == InfoToggleHostile.WITCH)
        {
            return EntityType.WITCH;
        }
        else if (type == InfoToggleHostile.WITHER)
        {
            return EntityType.WITHER;
        }
        else if (type == InfoToggleHostile.WITHER_SKELETON)
        {
            return EntityType.WITHER_SKELETON;
        }
        else if (type == InfoToggleHostile.ZOGLIN)
        {
            return EntityType.ZOGLIN;
        }
        else if (type == InfoToggleHostile.ZOMBIE)
        {
            return EntityType.ZOMBIE;
        }
        else if (type == InfoToggleHostile.ZOMBIE_VILLAGER)
        {
            return EntityType.ZOMBIE_VILLAGER;
        }
        else if (type == InfoToggleHostile.ZOMBIFIED_PIGLIN)
        {
            return EntityType.ZOMBIFIED_PIGLIN;
        }

        return null;
    }
}