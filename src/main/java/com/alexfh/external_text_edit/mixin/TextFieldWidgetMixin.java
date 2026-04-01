package com.alexfh.external_text_edit.mixin;

import com.alexfh.external_text_edit.ExternalTextEdit;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public class TextFieldWidgetMixin
{
    @Inject(at = @At("HEAD"), method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", cancellable = true)
    private void keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir)
    {
        boolean isControlE = input.input() == com.mojang.blaze3d.platform.InputConstants.KEY_E &&
                             input.modifiers() == GLFW.GLFW_MOD_CONTROL;
        if (!isControlE)
        {
            return;
        }
        ExternalTextEdit.editFieldWithExternalEditor((EditBox) (Object) this);
        cir.setReturnValue(true);
    }
}