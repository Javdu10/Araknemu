package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Adapter for transform mutable characteristics
 */
final public class MutableCharacteristicsTransformer implements Transformer<MutableCharacteristics> {
    final private CharacteristicsTransformer inner = new CharacteristicsTransformer();

    @Override
    public String serialize(MutableCharacteristics value) {
        return inner.serialize(value);
    }

    @Override
    public MutableCharacteristics unserialize(String serialize) {
        return inner.unserialize(serialize);
    }
}