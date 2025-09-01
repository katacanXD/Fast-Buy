package ru.katacan.fastbuy;


import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import ru.katacan.fastbuy.command.Command;

import java.time.LocalDate;

public class Buy implements ModInitializer {
    @Override
    public void onInitialize() {
//        String date = LocalDate.now().toString();
//        if (date.equals("2025-05-17")) {
            Command.init();
//        }
    }
}
