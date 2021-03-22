package redstoneChunkLoader.Advance;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import org.lwjgl.opengl.GL11;
import redstoneChunkLoader.ContainerKEI;
import redstoneChunkLoader.ModReadStoneChunkLoader;
import redstoneChunkLoader.network.PacketHandler;

import java.util.ArrayList;

public class ChunkLoadSettingGui extends GuiContainer {
    AdvanceChunkLoadTile tile;
    protected static final ResourceLocation chunkButtonTex = new ResourceLocation(ModReadStoneChunkLoader.DOMAIN + ":textures/chunkButtonTex.png");

    public ChunkLoadSettingGui(AdvanceChunkLoadTile tile) {
        super(new ContainerKEI());
        this.tile = tile;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        ChunkCoordIntPair center = tile.getThisPosChunk();
        ArrayList<ChunkCoordIntPair> chunks = tile.getForcedChunks();
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                ChunkButton button = new ChunkButton(0, width / 2 - 10 + x * 20, height / 2 - 10 + z * 20, x, z);
                if (x == 0 && z == 0) {
                    button.enabled = false;
                    button.mode = EnumChunkSettingMode.None;
                } else {
                    ChunkCoordIntPair ccp = new ChunkCoordIntPair(center.chunkXPos + x, center.chunkZPos + z);
                    if (chunks.contains(ccp)) {
                        button.set(EnumChunkSettingMode.Forced);
                    } else if (tempAddingChunkList.contains(ccp)) {
                        button.set(EnumChunkSettingMode.Adding);
                    } else if (tempRemoveChunkList.contains(ccp)) {
                        button.set(EnumChunkSettingMode.Removing);
                    } else {
                        button.set(EnumChunkSettingMode.None);
                    }
                }
                this.buttonList.add(button);
            }
        }

        GuiButton button = new GuiButton(1, 320, height / 2 + 40, 80, 20, "ReForceChunk");
        this.buttonList.add(button);
    }

    ArrayList<ChunkCoordIntPair> tempAddingChunkList = new ArrayList<ChunkCoordIntPair>();
    ArrayList<ChunkCoordIntPair> tempRemoveChunkList = new ArrayList<ChunkCoordIntPair>();
    ArrayList<ChunkCoordIntPair> tempChunkList = new ArrayList<ChunkCoordIntPair>();

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        super.actionPerformed(p_146284_1_);
        if (p_146284_1_.id == 0) {
            if (p_146284_1_ instanceof ChunkButton) {
                ChunkButton cb = (ChunkButton) p_146284_1_;
                ChunkCoordIntPair center = tile.getThisPosChunk();
                ChunkCoordIntPair pair = new ChunkCoordIntPair(center.chunkXPos + cb.xOffset, center.chunkZPos + cb.zOffset);
                switch (cb.mode) {
                    case None:
                        tempAddingChunkList.add(pair);
                        tempChunkList.add(pair);
                        cb.mode = EnumChunkSettingMode.Adding;
                        break;
                    case Removing:
                        tempRemoveChunkList.remove(pair);
                        tempChunkList.remove(pair);
                        cb.mode = EnumChunkSettingMode.Forced;
                        break;
                    case Adding:
                        tempAddingChunkList.remove(pair);
                        tempChunkList.remove(pair);
                        cb.mode = EnumChunkSettingMode.None;
                        break;
                    case Forced:
                        tempRemoveChunkList.add(pair);
                        tempChunkList.add(pair);
                        cb.mode = EnumChunkSettingMode.Removing;
                }
            }
        } else if (p_146284_1_.id == 1) {
            PacketHandler.sendPacketServer(new AdvanceChunkSettingSyncMessage(tile, tempChunkList));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        if (tile.isUpdate) {
            this.initGui();
            tile.isUpdate = false;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        for (int x = -4; x < 6; x++) {
            int oX = -15 + x * 20 + 90;
            int oZ = -12;
            GL11.glTranslated(oX, oZ, 0);
            GL11.glScaled(0.8, 0.8, 1.0);
            GL11.glRotated(-90, 0.0, 0.0, 1.0);
            String str = String.valueOf(x * 16 + tile.getThisPosChunk().chunkXPos * 16);
            this.fontRendererObj.drawString(str, 0, 0, -1);
            GL11.glRotated(90, 0.0, 0.0, 1.0);
            GL11.glScaled(1.25, 1.25, 1.0);
            GL11.glTranslated(-oX, -oZ, 0);
        }

        for (int z = -4; z < 6; z++) {
            int oX = -10;
            int oZ = -20 + z * 20 + 90;
            GL11.glTranslated(oX, oZ, 0);
            GL11.glScaled(0.8, 0.8, 1.0);
            String str = String.valueOf(z * 16 + tile.getThisPosChunk().chunkZPos * 16);
            this.fontRendererObj.drawString(str, -fontRendererObj.getStringWidth(str), 0, -1);
            GL11.glScaled(1.25, 1.25, 1.0);
            GL11.glTranslated(-oX, -oZ, 0);
        }


        this.mc.getTextureManager().bindTexture(chunkButtonTex);
        this.drawTexturedModalRect(200, height / 2 - 130, 0, 0, 20, 20);
        this.drawTexturedModalRect(200, height / 2 - 110, 20, 0, 20, 20);
        this.drawTexturedModalRect(200, height / 2 - 90, 40, 0, 20, 20);
        this.drawTexturedModalRect(200, height / 2 - 70, 60, 0, 20, 20);
        this.drawTexturedModalRect(200, height / 2 - 50, 80, 0, 20, 20);

        this.fontRendererObj.drawString("center", 230, height / 2 - 125, -1);
        this.fontRendererObj.drawString("None", 230, height / 2 - 105, -1);
        this.fontRendererObj.drawString("Remove", 230, height / 2 - 85, -1);
        this.fontRendererObj.drawString("Add", 230, height / 2 - 65, -1);
        this.fontRendererObj.drawString("Reserved", 230, height / 2 - 45, -1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(4.0f);
        if (tile.chunkLoadON) {
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        } else {
            GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
        }
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2d(-4, -9);
        GL11.glVertex2d(-4, 175);
        GL11.glVertex2d(180, 175);
        GL11.glVertex2d(180, -9);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
