package io.github.team3engine.game.events;

/**
 * Central list of game-specific events used with IOManager.
 * Keeping these in an enum avoids string typos across the game code.
 */
public enum GameEvents {
    PLAYER_MOVING,
    PLAYER_JUMP,
    PLAYER_CROUCH,
    PLAYER_DEAD,
    PLAYER_HIT_FIRE,
    PLAYER_WIN,
    NPC_RESCUED,

    GAME_PAUSE,
    GAME_UNPAUSE,

    START_GAME,
    START_GAME_TEST,

    SCOREBOARD_NEXT,
    SCOREBOARD_MENU
}
