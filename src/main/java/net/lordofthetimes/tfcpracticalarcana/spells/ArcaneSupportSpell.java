package net.lordofthetimes.tfcpracticalarcana.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.lordofthetimes.tfcpracticalarcana.TfcPracticalArcana;
import net.lordofthetimes.tfcpracticalarcana.registry.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class ArcaneSupportSpell extends AbstractSpell {

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(TfcPracticalArcana.MODID, "arcane_support");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(120)
            .build();

    public ArcaneSupportSpell(){
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.baseManaCost = 60;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getEffectLength(spellLevel, caster), 1))
        );
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (!Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .35f, false, target -> Utils.shouldHealEntity(entity, (Entity) target))) {
            //If we did not target a creature, target ourself
            playerMagicData.setAdditionalCastData(new TargetEntityCastData(entity));
            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.spell_target_success_self", this.getDisplayName(serverPlayer)).withStyle(ChatFormatting.GREEN)));
            }
        }
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {

        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
            if (targetData.getTarget((ServerLevel) level) instanceof LivingEntity targetEntity) {
                targetEntity.addEffect(new MobEffectInstance(ModEffects.ARCANE_SUPPORT_EFFECT, (int) getEffectLength(spellLevel, caster), 0, false, false, true));
                MagicManager.spawnParticles(level, ParticleHelper.CLEANSE_PARTICLE, targetEntity.getX(), targetEntity.getY() + .25, targetEntity.getZ(), 15, targetEntity.getBbWidth() * 0.5, targetEntity.getBbWidth() * 0.5, targetEntity.getBbWidth() * 0.5, 0, false);
            }
        }

        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    private float getEffectLength(int spellLevel, LivingEntity caster){
        return 20 * 20 + (20 * 15 * getSpellPower(spellLevel, caster));
    }
}
