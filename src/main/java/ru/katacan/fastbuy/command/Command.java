package ru.katacan.fastbuy.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import ru.katacan.fastbuy.FastBuy;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Command {
    public static int boostType;
    public static void init() {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("fastbuy")
                    .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(0))
                            .then(ClientCommandManager.argument("boostType", IntegerArgumentType.integer(0))
                                    .executes(c -> {
                                        boostType = c.getArgument("boostType", int.class);
                                        FastBuy.buy(c.getArgument("count", int.class));
                                        return 1;
                                    }))
                    ))
            );
    }
}