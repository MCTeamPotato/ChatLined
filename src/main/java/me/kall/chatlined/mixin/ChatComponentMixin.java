package me.kall.chatlined.mixin;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Shadow public abstract void addMessage(Component chatComponent, @Nullable MessageSignature headerSignature, @Nullable GuiMessageTag tag);

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void chatLined$splitOnNewline(@NotNull Component chatComponent, @Nullable MessageSignature headerSignature, @Nullable GuiMessageTag tag, CallbackInfo ci) {
        String text = chatComponent.getString().replace("\\n", "\n");
        if (!text.contains("\n")) return;

        ci.cancel();

        Style originalStyle = chatComponent.getStyle();
        for (String line : text.split("\n", -1)) this.addMessage(Component.literal(line).withStyle(originalStyle), headerSignature, tag);
    }
}
