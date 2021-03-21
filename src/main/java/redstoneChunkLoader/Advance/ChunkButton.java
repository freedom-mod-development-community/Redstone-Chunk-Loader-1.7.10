package redstoneChunkLoader.Advance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import redstoneChunkLoader.ModReadStoneChunkLoader;

public class ChunkButton extends GuiButton {


    public int xOffset, zOffset;
    public EnumChunkSettingMode mode = EnumChunkSettingMode.None;

    public ChunkButton(int id, int xPos, int yPos, int xOffset, int yOffset) {
        super(id, xPos, yPos, "");
        this.xOffset = xOffset;
        this.zOffset = yOffset;
        this.width = 20;
        this.height = 20;
    }

    public ChunkButton set(EnumChunkSettingMode mode) {
        this.mode = mode;
        return this;
    }

    protected static final ResourceLocation chunkButtonTex = new ResourceLocation(ModReadStoneChunkLoader.DOMAIN + ":textures/chunkButtonTex.png");

    public int getHoverState(boolean p_146114_1_) {
        byte b0 = 1;

        if (!this.enabled) {
            b0 = 0;
        } else if (p_146114_1_) {
            b0 = 2;
        }

        return b0;
    }

    public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
        if (this.visible) {
            p_146112_1_.getTextureManager().bindTexture(chunkButtonTex);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
            int x;
            if (!enabled) {
                x = 0;
            } else {
                x = (mode.id)*20;
            }

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, x, 0, this.width, this.height);
            this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
        }
    }
}
