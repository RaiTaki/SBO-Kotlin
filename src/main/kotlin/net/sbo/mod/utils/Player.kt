package net.sbo.mod.utils

import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.SboVec
import java.util.UUID

object Player {
    fun getLastPosition(): SboVec {
        val player = mc.player ?: return SboVec(0.0, 0.0, 0.0)
        return SboVec(player.x, player.y, player.z)
    }

    fun getUUIDString(): String {
        return mc.player?.uuidAsString ?: ""
    }

    fun getUUID(): UUID {
        return mc.player?.uuid ?: UUID.fromString("00000000-0000-0000-0000-000000000000")
    }
}