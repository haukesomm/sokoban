package de.haukesomm.sokoban.core

import de.haukesomm.sokoban.core.level.*
import de.haukesomm.sokoban.core.moving.MoveCoordinator
import de.haukesomm.sokoban.core.state.GameState
import de.haukesomm.sokoban.core.state.transform

class GameStateService(
    private val levelRepository: LevelRepository,
    tileFactory: TileFactory
) {
    fun interface StateChangeListener {
        fun onGameStateChange(state: GameState)
    }


    private val levelToGameStateConverter = LevelToGameStateConverter(tileFactory)

    private val moveCoordinator = MoveCoordinator.withDefaultRules()

    private val stateChangeListeners = mutableSetOf<StateChangeListener>()


    private lateinit var state: GameState


    fun addGameStateChangedListener(listener: StateChangeListener) {
        stateChangeListeners += listener
    }

    private fun notifyGameStateChangedListeners() {
        stateChangeListeners.forEach { it.onGameStateChange(state) }
    }


    fun getAvailableLevels(): List<LevelDescription> =
        levelRepository.getAvailableLevels()

    fun loadLevel(levelId: String) {
        val level = levelRepository.getLevelOrNull(levelId)
            ?: throw IllegalStateException("Level with id '$levelId' does not exist!")

        state = levelToGameStateConverter.convert(level)
        notifyGameStateChangedListeners()
    }

    fun reloadLevel() = loadLevel(state.levelId)


    fun getPlayer(): Entity? = state.getPlayer()


    fun moveEntityIfPossible(entity: Entity, direction: Direction) {
        if (!this::state.isInitialized) {
            throw IllegalStateException("Cannot move Entity: No game state loaded!")
        }

        moveCoordinator.moveEntityIfPossible(state, entity, direction)?.let { newState ->
            state = newState.transform {
                levelCleared = checkLevelCleared(newState)
            }
        }

        notifyGameStateChangedListeners()
    }

    private fun checkLevelCleared(state: GameState): Boolean =
        state.tiles.none { tile ->
            val box = state.entityAt(tile.position)?.takeIf(Entity::isBox)
            tile.isTarget && box == null
        }
}