package fr.quatrevieux.araknemu.data.world.entity.character;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.util.SortedMap;

/**
 * Entity for player race
 */
final public class PlayerRace {
    final private Race race;
    final private String name;
    final private SortedMap<Integer, Characteristics> baseStats;
    final private int startDiscernment;
    final private int startPods;
    final private int startLife;
    final private int perLevelLife;
    final private BoostStatsData boostStats;
    final private Position startPosition;
    final private Position astrubPosition;
    final private int[] spells;

    public PlayerRace(Race race, String name, SortedMap<Integer, Characteristics> baseStats, int startDiscernment, int startPods, int startLife, int perLevelLife, BoostStatsData boostStats, Position startPosition, Position astrubPosition, int[] spells) {
        this.race = race;
        this.name = name;
        this.baseStats = baseStats;
        this.startDiscernment = startDiscernment;
        this.startPods = startPods;
        this.startLife = startLife;
        this.perLevelLife = perLevelLife;
        this.boostStats = boostStats;
        this.startPosition = startPosition;
        this.astrubPosition = astrubPosition;
        this.spells = spells;
    }

    public PlayerRace(Race race) {
        this(race, null, null, 0, 0, 0, 0, null, null, null, null);
    }

    public Race race() {
        return race;
    }

    /**
     * Get the race name (not used on process : for human)
     */
    public String name() {
        return name;
    }

    /**
     * Get the base stats of the race
     * This include AP, MP
     * The stats are indexed by the minimum level for applying (ex: 7 AP on level 100)
     */
    public SortedMap<Integer, Characteristics> baseStats() {
        return baseStats;
    }

    /**
     * The base discernment for the race
     */
    public int startDiscernment() {
        return startDiscernment;
    }

    /**
     * The base pods for the race
     */
    public int startPods() {
        return startPods;
    }

    /**
     * The base life for the race
     */
    public int startLife() {
        return startLife;
    }

    /**
     * Number of life point win per level
     */
    public int perLevelLife() {
        return perLevelLife;
    }

    /**
     * Boost stats rules
     */
    public BoostStatsData boostStats() {
        return boostStats;
    }

    /**
     * The start position (incarman statue)
     */
    public Position startPosition() {
        return startPosition;
    }

    /**
     * The astrub statue position
     */
    public Position astrubPosition() {
        return astrubPosition;
    }

    /**
     * List of race spells ids
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.SpellTemplate#id()
     */
    public int[] spells() {
        return spells;
    }
}
