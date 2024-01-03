/*******************************************************************************
 * Copyright 2023, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package toughasnails.init;

import glitchcore.event.client.RegisterColorsEvent;
import glitchcore.util.RenderTypeHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import toughasnails.api.item.TANItems;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.core.ToughAsNails;
import toughasnails.item.DyeableWoolItem;
import toughasnails.item.LeafArmorItem;

import java.util.HashMap;
import java.util.Map;

import static toughasnails.api.block.TANBlocks.RAIN_COLLECTOR;
import static toughasnails.api.block.TANBlocks.WATER_PURIFIER;

public class ModClient
{
    static void registerItemProperties()
    {
        ItemProperties.register(TANItems.THERMOMETER, new ResourceLocation(ToughAsNails.MOD_ID, "temperature"), new ClampedItemPropertyFunction() {
            final Map<Integer, Delta> deltas = new HashMap<>();


            @Override
            public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed)
            {
                Entity holder = entity != null ? entity : stack.getEntityRepresentation();

                if (holder == null)
                    return 0.5F;

                if (level == null && holder.level() instanceof ClientLevel)
                    level = (ClientLevel)holder.level();

                if (level == null)
                    return 0.5F;

                Delta delta = deltas.computeIfAbsent(holder.getId(), k -> new Delta());
                delta.update(level, TemperatureHelper.getTemperatureAtPos(level, holder.blockPosition()));
                return delta.getValue();
            }

            private static class Delta
            {
                private long lastUpdateTick;
                private double currentValue;
                private double rota;

                private void update(ClientLevel level, TemperatureLevel temperatureLevel)
                {
                    if (level.getGameTime() == this.lastUpdateTick)
                        return;

                    this.lastUpdateTick = level.getGameTime();
                    double targetValue = temperatureLevel.ordinal() * 0.25;
                    double delta = targetValue - this.currentValue;

                    // Add a small increment to the rota to move towards the target value
                    this.rota += delta * 0.1;

                    // Diminish the rota over time. The clock uses 0.9, but we want slightly less wobbling
                    this.rota *= 0.87;
                    this.currentValue = Mth.clamp(this.currentValue + this.rota, 0.0, 1.0);
                }

                public float getValue()
                {
                    // Round to the nearest 0.05
                    return (float)((double)Math.round(this.currentValue * 20.0) / 20.0);
                }
            }
        });
    }

    public static void registerItemColors(RegisterColorsEvent.Item event)
    {
        event.register((stack, tintIndex) -> {
            return tintIndex > 0 ? -1 : ((DyeableWoolItem)stack.getItem()).getColor(stack);
        }, TANItems.WOOL_HELMET, TANItems.WOOL_CHESTPLATE, TANItems.WOOL_LEGGINGS, TANItems.WOOL_BOOTS);

        event.register((stack, tintIndex) -> {
            return tintIndex > 0 ? -1 : ((LeafArmorItem)stack.getItem()).getColor(stack);
        }, TANItems.LEAF_HELMET, TANItems.LEAF_CHESTPLATE, TANItems.LEAF_LEGGINGS, TANItems.LEAF_BOOTS);
    }

    public static void registerBlockColors(RegisterColorsEvent.Block event)
    {
        event.register((state, world, pos, tintIndex) -> 0x47DAFF, RAIN_COLLECTOR);
        event.register((state, world, pos, tintIndex) -> 0x3F76E4, WATER_PURIFIER);
    }

    public static void setupRenderTypes()
    {
        RenderType transparentRenderType = RenderType.cutoutMipped();
        RenderType cutoutRenderType = RenderType.cutout();
        RenderType translucentRenderType = RenderType.translucent();

        RenderTypeHelper.setRenderType(RAIN_COLLECTOR, cutoutRenderType);
        RenderTypeHelper.setRenderType(WATER_PURIFIER, cutoutRenderType);
    }
}