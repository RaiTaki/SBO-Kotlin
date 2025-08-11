package net.sbo.mod.utils

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.sbo.mod.general.WaypointManager
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.Chat.chat
import org.lwjgl.glfw.GLFW

object SboKeyBinds {

    private data class KeyPressState(var isHeldDown: Boolean = false, var lastActivation: Long = 0)
    private val keyStates = mutableMapOf<KeyBinding, KeyPressState>()

    fun init() {
        register()
        registerKeyBindListener()
    }

    val guessWarpKey: KeyBinding = KeyBinding(
        "key.sbo-kotlin.guess_warp",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "key.category.sbo-kotlin.keybinds"
    )

    val inqWarpKey: KeyBinding = KeyBinding(
        "key.sbo-kotlin.inq_warp",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "key.category.sbo-kotlin.keybinds"
    )

    val generalWarpKey: KeyBinding = KeyBinding(
        "key.sbo-kotlin.general_warp",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "key.category.sbo-kotlin.keybinds"
    )

    val sendCoordsKey: KeyBinding = KeyBinding(
        "key.sbo-kotlin.send_coords",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "key.category.sbo-kotlin.keybinds"
    )

    fun register() {
        KeyBindingHelper.registerKeyBinding(guessWarpKey)
        KeyBindingHelper.registerKeyBinding(inqWarpKey)
        KeyBindingHelper.registerKeyBinding(generalWarpKey)
        KeyBindingHelper.registerKeyBinding(sendCoordsKey)
    }

    private fun handlePressAction(keyBinding: KeyBinding, action: () -> Unit) {
        handlePressAction(keyBinding, 0L, action)
    }

    private fun handlePressAction(keyBinding: KeyBinding, cooldownMillis: Long, action: () -> Unit) {
        val state = keyStates.getOrPut(keyBinding) { KeyPressState() }

        if (keyBinding.wasPressed()) {
            val currentTime = System.currentTimeMillis()
            if (!state.isHeldDown && currentTime - state.lastActivation > cooldownMillis) {
                action()
                state.lastActivation = currentTime
                state.isHeldDown = true
            }
        } else {
            state.isHeldDown = false
        }
    }

    fun registerKeyBindListener() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            handlePressAction(guessWarpKey) {
                WaypointManager.warpToGuess()
            }

            handlePressAction(inqWarpKey) {
                WaypointManager.warpToInq()
            }

            handlePressAction(generalWarpKey) {
                WaypointManager.warpBoth()
            }

            handlePressAction(sendCoordsKey, 500L) {
                val playerPos = Player.getLastPosition()
                Chat.say("x: ${playerPos.x.toInt()}, y: ${playerPos.y.toInt() - 1}, z: ${playerPos.z.toInt()}")
            }
        })
    }
}