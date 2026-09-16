package net.lordofthetimes.tfcpracticalarcana.registry;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;

import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.lordofthetimes.tfcpracticalarcana.spells.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, TfcPracticalArcana.MODID);

    public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static final Supplier<AbstractSpell> FRESH_WELL = registerSpell(new FreshWellSpell());
    public static final Supplier<AbstractSpell> BRINE_WELL = registerSpell(new BrineWellSpell());

    public static final Supplier<AbstractSpell> REKINDLE = registerSpell(new RekindleSpell());
    public static final Supplier<AbstractSpell> SET_FIRE = registerSpell(new SetFireSpell());
    public static final Supplier<AbstractSpell> HEAT = registerSpell(new HeatSpell());

    public static final Supplier<AbstractSpell> PRESERVE = registerSpell(new PreserveSpell());

    public static final Supplier<AbstractSpell> ARCANE_SUPPORT = registerSpell(new ArcaneSupportSpell());

    public static final Supplier<AbstractSpell> REVITALIZE = registerSpell(new RevitalizeSpell());


    /*TODO: SPELL IDEAS

    Cool item spell: cools down any item with tfc heat property


    Boost sapling/crop growth? potentially too op?

    Spell giving familiarity to animals (or instead increases max cap on adult animals?

    TODO: DONE SPELL IDEAS
    Spell giving an effect that temporarily stops you from causing a collapse
    Heat item spell: heats items with tfc heat property, funny food trait if you use it for food
    Set Fire: sets the block you look at at fire (works with campfires, forges, campfire pots, pit kilns,
    charcoal pits, etc. (other mod like firmalife integrations?) NOTE : all blocks that work with StartFireEvent should work
     */
}
