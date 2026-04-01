package com.alexfh.external_text_edit.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.network.chat.Component;

public class ExternalTextEditModMenuImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent ->
                ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable("text.autoconfig.external-text-edit.title")).build();
    }
}