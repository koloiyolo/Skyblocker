package me.xmrvizzy.skyblocker.skyblock.dungeon;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.xmrvizzy.skyblocker.SkyblockerMod;
import me.xmrvizzy.skyblocker.chat.ChatFilterResult;
import me.xmrvizzy.skyblocker.chat.ChatPatternListener;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.regex.Matcher;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class NoDowntime extends ChatPatternListener {

    private boolean active;
    private String command;
    private int inParty = 0;
    private SkyblockerMod skyblocker = SkyblockerMod.getInstance();

    public NoDowntime() {
        super("^(?:You are not currently in a party\\." +
                "|You are not a party leader\\." +
                "|.* has left the party\\." +
                "|(.*) joined the party\\." +
                "|(.*): !dt"
                +"|                            Team Score: (.*))$");

        this.active = false;
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("nodt").then(argument("floor", new FloorArgumentType()).executes(context -> {
            if (!Utils.isOnSkyblock() || this.active || MinecraftClient.getInstance().player == null) return 0;
            this.active = true;
            String arg = StringArgumentType.getString(context, "floor");
            command = "/join " + ((arg.charAt(0) == 'f') ? "catacombs " : "master_mode ") + arg.substring(1);
            execute(command);
            return 0;
        }))));
    }

    @Override
    protected ChatFilterResult state() {
        return active ? ChatFilterResult.FILTER : ChatFilterResult.PASS;
    }

    @Override
    protected boolean onMatch(Text message, Matcher matcher) {
        if (matcher.group(1) != null) {
            inParty++;
        } else if (matcher.group(2) != null) {
            active = false;
            command = null;
            skyblocker.messageScheduler.sendMessageAfterCooldown(matcher.group("request") + " requested downtime");
        } else if (matcher.group(3) != null) {
            execute(command);
        } else {
            active = false;
            command = null;
            return true;
        }
        return false;
    }

    private void execute(String command) {
        if (active) {
            inParty = 0;
            skyblocker.messageScheduler.queueMessage("/warp dhub", 5);
            skyblocker.messageScheduler.queueMessage("/p warp", 25);
            skyblocker.messageScheduler.queueMessage("/rp", 45);
            skyblocker.scheduler.schedule(() -> {
                if (inParty == 4) {
                    skyblocker.messageScheduler.sendMessageAfterCooldown(command);
                } else {
                    skyblocker.messageScheduler.sendMessageAfterCooldown("Party not full.");
                    active = false;
                }
            }, 100);
        }
    }
}

class FloorArgumentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();

        if (input.matches("^[fm]\\d+$")) {
            return input;
        } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(input);
        }
    }
}

