package com.alexfh.external_text_edit.mixin;

import com.alexfh.external_text_edit.ExternalTextEdit;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin
{
    @Inject(at = @At("HEAD"), method = "keyPressed(Lnet/minecraft/client/input/KeyInput;)Z", cancellable = true)
    private void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir)
    {
        boolean isControlE = input.getKeycode() == net.minecraft.client.util.InputUtil.GLFW_KEY_E &&
                             input.modifiers() == GLFW.GLFW_MOD_CONTROL;
        if (!isControlE)
        {
            return;
        }
        ExternalTextEdit.editFieldWithExternalEditor((TextFieldWidget) (Object) this);
        cir.setReturnValue(true);
    }
}