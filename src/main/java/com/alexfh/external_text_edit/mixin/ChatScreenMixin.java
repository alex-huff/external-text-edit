package com.alexfh.external_text_edit.mixin;

import com.alexfh.external_text_edit.ExternalTextEdit;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin
{
    @Inject(at = @At("HEAD"), method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z")
    private void keyPressedStart(KeyEvent input, CallbackInfoReturnable<Boolean> cir)
    {
        ExternalTextEdit.processingChatScreenKeyPress = true;
    }

    @Inject(at = @At("RETURN"), method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z")
    private void keyPressedEnd(KeyEvent input, CallbackInfoReturnable<Boolean> cir)
    {
        ExternalTextEdit.processingChatScreenKeyPress = false;
    }
}