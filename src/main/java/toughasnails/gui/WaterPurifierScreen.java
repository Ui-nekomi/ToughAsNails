/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package toughasnails.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import toughasnails.container.WaterPurifierContainer;
import toughasnails.core.ToughAsNails;

@OnlyIn(Dist.CLIENT)
public class WaterPurifierScreen extends AbstractContainerScreen<WaterPurifierContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(ToughAsNails.MOD_ID, "textures/gui/container/water_purifier.png");

    public WaterPurifierScreen(WaterPurifierContainer screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
    }

    public void init()
    {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int leftPos = this.leftPos;
        int topPos = this.topPos;
        this.blit(stack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.isFiltering())
        {
            int filterProgress = this.menu.getFilterProgress();

            // NOTE: This is moved over right by 1 compared to the furnace
            // stack, x, y, u, v, width, height
            this.blit(stack, leftPos + 56 + 1, topPos + 36 + 13 - filterProgress, 176, 13 - filterProgress, 14, filterProgress + 1);
        }

        int purifyProgress = this.menu.getPurifyProgress();
        this.blit(stack, leftPos + 79, topPos + 34, 176, 14, purifyProgress + 1, 16);
    }
}