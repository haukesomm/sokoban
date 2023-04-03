package de.haukesomm.sokoban.core.level

import de.haukesomm.sokoban.core.Entity
import de.haukesomm.sokoban.core.GameState
import de.haukesomm.sokoban.core.Position
import de.haukesomm.sokoban.core.Tile

class LevelToGameStateConverter(private val levelCharacterMap: LevelCharacterMap) {

    fun convert(level: Level): GameState {
        val entities = mutableSetOf<Entity>()
        val tiles = Array(level.height) { y ->
            Array(level.width) { x ->
                val position = Position(x, y)
                val levelStringIndex = position.toIndex(level.width)
                val character = level.layoutString.toCharArray()[levelStringIndex]

                val tile = Tile(levelCharacterMap.getTileType(character))

                levelCharacterMap.getEntityType(character)?.let { type ->
                    entities += Entity(type = type, position = position)
                }

                tile
            }
        }
        return GameState(tiles, entities)
    }
}