package eu.minemania.mobcountmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;

import eu.minemania.mobcountmod.Reference;
import eu.minemania.mobcountmod.config.Configs;
import eu.minemania.mobcountmod.counter.DataManager;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;

public class MobCountCommand extends MobCountCommandBase
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("mobcounter");
        LiteralArgumentBuilder<ServerCommandSource> counter = literal("mobcounter").executes(MobCountCommand::info)
                .then(literal("help").executes(MobCountCommand::help))
                .then(literal("message").executes(MobCountCommand::message)
                        .then(literal("clear").executes(MobCountCommand::message_clear)))
                .then(argument("[player1[,player2]]", greedyString()).executes(MobCountCommand::message))
                .then(literal("m").executes(MobCountCommand::message)
                        .then(literal("clear").executes(MobCountCommand::message_clear))
                        .then(argument("[player1[,player2]]", greedyString()).executes(MobCountCommand::message)))
                .then(literal("msg").executes(MobCountCommand::message)
                        .then(literal("clear").executes(MobCountCommand::message_clear))
                        .then(argument("[player1[,player2]]", greedyString()).executes(MobCountCommand::message)))
                .then(literal("sound").executes(MobCountCommand::sound)
                        .then(argument("sound file", word()).executes(MobCountCommand::sound)))
                .then(literal("faction").executes(MobCountCommand::faction)
                        .then(argument("on", bool()).executes(MobCountCommand::faction)))
                .then(literal("xp5").executes(MobCountCommand::xp5)
                        .then(argument("on", bool()).executes(MobCountCommand::xp5)))
                .then(literal("killed")
                        .then(literal("save").executes(MobCountCommand::killed_save))
                        .then(literal("clear").executes(MobCountCommand::killed_clear)));
        dispatcher.register(counter);
    }

    private static int info(CommandContext<ServerCommandSource> context)
    {
        localOutput(context.getSource(), Reference.MOD_NAME + " [" + Reference.MOD_VERSION + "]");
        localOutputT(context.getSource(), "mobcountmod.message.command.info");
        return 1;
    }

    private static int message(CommandContext<ServerCommandSource> context)
    {
        String message;
        try
        {
            message = getString(context, "[player1[,player2]]");
            Configs.Generic.MESSAGE_LIST.setStrings(Arrays.asList(message.split(",")));
            String toSend = message.replaceAll(",", " ");
            localOutputT(context.getSource(), "mobcountmod.message.command.message", toSend);
        }
        catch (Exception e)
        {
            if (Configs.Generic.MESSAGE_LIST.getStrings() == null)
            {
                localOutputT(context.getSource(), "mobcountmod.message.command.message.not_notify");
            }
            else
            {
                String toSend = "mobcountmod.message.command.message";
                StringBuilder names = new StringBuilder();
                for (String name : Configs.Generic.MESSAGE_LIST.getStrings())
                {
                    names.append(name).append(" ");
                }
                localOutputT(context.getSource(), toSend, names.toString());
            }
        }
        return 1;
    }

    private static int message_clear(CommandContext<ServerCommandSource> context)
    {
        Configs.Generic.MESSAGE_LIST.setStrings(null);
        localOutputT(context.getSource(), "mobcountmod.message.command.message.not_notify");
        return 1;
    }

    private static int sound(CommandContext<ServerCommandSource> context)
    {
        String soundFile;
        try
        {
            soundFile = getString(context, "sound file");
            Configs.Generic.SOUNDFILE.setValueFromString(soundFile);
            localOutputT(context.getSource(), "mobcountmod.message.command.sound.using", Configs.Generic.SOUNDFILE.getStringValue());

        }
        catch (Exception e)
        {
            localOutputT(context.getSource(), "mobcountmod.message.command.sound.current", Configs.Generic.SOUNDFILE.getStringValue());
        }
        return 1;
    }

    private static int faction(CommandContext<ServerCommandSource> context)
    {
        boolean on;
        try
        {
            on = getBool(context, "on");
            Configs.Generic.NOTIFYFACTION.setBooleanValue(on);
            if (on)
            {
                localOutputT(context.getSource(), "mobcountmod.message.command.faction.notify");
            }
            else
            {
                localOutputT(context.getSource(), "mobcountmod.message.command.faction.not_notify");
            }
        }
        catch (Exception e)
        {
            String strSetting = Configs.Generic.NOTIFYFACTION.getBooleanValue() ? "mobcountmod.message.setting.on" : "mobcountmod.message.setting.off";
            localOutputT(context.getSource(), "mobcountmod.message.command.faction.enabled", StringUtils.translate(strSetting));
        }
        if (Configs.Generic.NOTIFYFACTION.getBooleanValue())
        {
            localOutputT(context.getSource(), "mobcountmod.message.command.faction.notifying");
        }
        else
        {
            localOutputT(context.getSource(), "mobcountmod.message.command.faction.not_notifying");
        }
        return 1;
    }

    private static int xp5(CommandContext<ServerCommandSource> context)
    {
        boolean on;
        try
        {
            on = getBool(context, "on");
            Configs.Generic.XP5.setBooleanValue(on);
            if (on)
            {
                localOutputT(context.getSource(), "mobcountmod.message.command.xp5.shocker");
            }
            else
            {
                localOutputT(context.getSource(), "mobcountmod.message.command.xp5.normal_radius");
            }
        }
        catch (Exception e)
        {
            String strSetting = Configs.Generic.XP5.getBooleanValue() ? "mobcountmod.message.setting.on" : "mobcountmod.message.setting.off";
            localOutputT(context.getSource(), "mobcountmod.message.command.xp5", StringUtils.translate(strSetting));
        }
        return 1;
    }

    private static int help(CommandContext<ServerCommandSource> context)
    {
        localOutputT(context.getSource(), "mobcountmod.message.command.help", Reference.MOD_NAME, Reference.MOD_VERSION);
        int cmdCount = 0;
        CommandDispatcher<ServerCommandSource> dispatcher = Command.commandDispatcher;
        for (CommandNode<ServerCommandSource> command : dispatcher.getRoot().getChildren())
        {
            String cmdName = command.getName();
            if (ClientCommandManager.isClientSideCommand(cmdName))
            {
                Map<CommandNode<ServerCommandSource>, String> usage = dispatcher.getSmartUsage(command, context.getSource());
                for (String u : usage.values())
                {
                    ClientCommandManager.sendFeedback(Text.literal("/" + cmdName + " " + u));
                }
                cmdCount += usage.size();
                if (usage.size() == 0)
                {
                    ClientCommandManager.sendFeedback(Text.literal("/" + cmdName));
                    cmdCount++;
                }
            }
        }
        return cmdCount;
    }

    private static int killed_clear(CommandContext<ServerCommandSource> context)
    {
        DataManager.resetEntityCount();
        localOutputT(context.getSource(), "mobcountmod.message.command.killed.reset");
        return 1;
    }

    private static int killed_save(CommandContext<ServerCommandSource> context)
    {
        DataManager.saveKilledFile(context.getSource());
        return 1;
    }
}