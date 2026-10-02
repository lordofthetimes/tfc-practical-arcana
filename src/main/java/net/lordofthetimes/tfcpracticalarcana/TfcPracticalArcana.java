package net.lordofthetimes.tfcpracticalarcana;

import net.lordofthetimes.tfcpracticalarcana.registry.ModEffects;
import net.lordofthetimes.tfcpracticalarcana.registry.ModFoodTraits;
import net.lordofthetimes.tfcpracticalarcana.registry.ModSpellRegistry;
import net.lordofthetimes.tfcpracticalarcana.spells.ArcaneSupportSpell;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TfcPracticalArcana.MODID)
public class TfcPracticalArcana {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tfc_practical_arcana";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TfcPracticalArcana(IEventBus modEventBus, ModContainer modContainer) {

        ModFoodTraits.TRAITS.register(modEventBus);

        ModEffects.register(modEventBus);

        ModSpellRegistry.SPELLS.register(modEventBus);

    }
}
