package net.sbo.mod.utils.events.impl.game

import net.minecraft.client.MinecraftClient

/**
 * Called when the game is closed.
 * @param client The Minecraft client instance.
 */
class GameCloseEvent (val client: MinecraftClient)