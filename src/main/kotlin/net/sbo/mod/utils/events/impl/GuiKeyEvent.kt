package net.sbo.mod.utils.events.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * Event fired when a key is pressed in a GUI screen.
 * @param client The Minecraft client instance.
 * @param screen The screen where the key event occurred.
 * @param key The key code of the pressed key.
 * @param cir The callback info returnable to control the event flow.
 */
class GuiKeyEvent(
    val client: MinecraftClient,
    val screen: Screen,
    val key: Int,
    val cir: CallbackInfoReturnable<Boolean>
)