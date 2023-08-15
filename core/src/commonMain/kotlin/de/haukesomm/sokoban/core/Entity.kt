package de.haukesomm.sokoban.core

import kotlinx.serialization.Serializable

/**
 * Represents the type an [Entity] can have.
 */
enum class EntityType { Box, Player }

/**
 * Represents an entity in the game that can be moved and placed on a [Tile].
 */
@Serializable
data class Entity(
    val type: EntityType
) {
    val isPlayer: Boolean = type == EntityType.Player
    val isBox: Boolean = type == EntityType.Box
}
