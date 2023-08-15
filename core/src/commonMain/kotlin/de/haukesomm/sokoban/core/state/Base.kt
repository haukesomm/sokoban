package de.haukesomm.sokoban.core.state

import de.haukesomm.sokoban.core.*
import kotlinx.serialization.Serializable

/**
 * Represents the state of a game.
 *
 * The state contains the id of the level, the width and height of the game board, the tiles of the game board,
 * the number of moves and pushes the player has made and whether the level has been cleared or not.
 *
 * A number of convenience methods are provided to access the tiles and entities on the game board.
 *
 * There may be several implementations of this interface. The two fundamental implementations provided by this
 * library are [ImmutableGameState] and [MutableGameState].
 *
 * Other implementations may be provided in order to support different use cases. For example, a [GameState] may
 * be implemented to work with other frameworks mechanics such as fritz2's `@Lenses` annotation.
 */
interface GameState {
    val levelId: String
    val width: Int
    val height: Int
    val tiles: List<Tile>
    val moves: Int
    val pushes: Int
    val levelCleared: Boolean

    /**
     * Returns the [Tile] at the given [position] or `null` if the position is out of bounds.
     */
    fun tileAt(position: Position): Tile? =
        tiles.getOrNull(position.toIndex(width))

    /**
     * Returns the next [Tile] in the given [direction] or `null` if the next position is out of bounds.
     */
    fun tileInDirection(position: Position, direction: Direction): Tile? =
        tileAt(position.nextInDirection(direction))

    /**
     * Returns the [Entity] at the given [position] or `null` if the position is out of bounds.
     */
    fun entityAt(position: Position): Entity? =
        tileAt(position)?.entity

    /**
     * Returns the next [Entity] in the given [direction] or `null` if the next position is out of bounds.
     */
    fun entityInDirection(position: Position, direction: Direction): Entity? =
        entityAt(position.nextInDirection(direction))

    /**
     * Returns the player's [Position] or `null` if the player is not on the game board.
     */
    fun getPlayerPosition(): Position? =
        when (val index = tiles.indexOfFirst { it.entity?.isPlayer == true }) {
            -1 -> null
            else -> Position.fromIndex(index, width)
        }
}