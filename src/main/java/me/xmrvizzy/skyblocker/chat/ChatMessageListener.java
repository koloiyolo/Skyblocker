package me.xmrvizzy.skyblocker.chat;

import me.xmrvizzy.skyblocker.chat.filters.*;
import me.xmrvizzy.skyblocker.skyblock.api.ApiKeyListener;
import me.xmrvizzy.skyblocker.skyblock.barn.HungryHiker;
import me.xmrvizzy.skyblocker.skyblock.barn.TreasureHunter;
import me.xmrvizzy.skyblocker.skyblock.dungeon.NoDowntime;
import me.xmrvizzy.skyblocker.skyblock.dungeon.Reparty;
import me.xmrvizzy.skyblocker.skyblock.dungeon.ThreeWeirdos;
import me.xmrvizzy.skyblocker.skyblock.dungeon.Trivia;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Fetchur;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Puzzler;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

@FunctionalInterface
public interface ChatMessageListener {
    /**
     * An event called when a game message is received. Register your listeners in {@link ChatMessageListener#init()}.
     */
    Event<ChatMessageListener> EVENT = EventFactory.createArrayBacked(ChatMessageListener.class,
            (listeners) -> (message, asString) -> {
                for (ChatMessageListener listener : listeners) {
                    ChatFilterResult result = listener.onMessage(message, asString);
                    if (result != ChatFilterResult.PASS) return result;
                }
                return ChatFilterResult.PASS;
            });

    /**
     * Registers {@link ChatMessageListener}s to {@link ChatMessageListener#EVENT} and registers {@link ChatMessageListener#EVENT} to {@link ClientReceiveMessageEvents#ALLOW_GAME}
     */
    static void init() {
        ChatMessageListener[] listeners = new ChatMessageListener[]{
                // Features
                new ApiKeyListener(),
                new Fetchur(),
                new Puzzler(),
                new Reparty(),
                new NoDowntime(),
                new ThreeWeirdos(),
                new Trivia(),
                new TreasureHunter(),
                new HungryHiker(),
                // Filters
                new AbilityFilter(),
                new AdFilter(),
                new AoteFilter(),
                new ComboFilter(),
                new HealFilter(),
                new ImplosionFilter(),
                new MoltenWaveFilter(),
                new TeleportPadFilter(),
                new AutopetFilter(),
        };
        // Register all listeners to EVENT
        for (ChatMessageListener listener : listeners) {
            EVENT.register(listener);
        }
        // Register EVENT to ClientReceiveMessageEvents.ALLOW_GAME from fabric api
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (!Utils.isOnSkyblock()) {
                return true;
            }
            ChatFilterResult result = EVENT.invoker().onMessage(message, message.getString());
            switch (result) {
                case ACTION_BAR -> {
                    if (overlay) {
                        return true;
                    }
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        player.sendMessage(message, true);
                        return false;
                    }
                }
                case FILTER -> {
                    return false;
                }
            }
            return true;
        });
    }

    ChatFilterResult onMessage(Text message, String asString);
}
