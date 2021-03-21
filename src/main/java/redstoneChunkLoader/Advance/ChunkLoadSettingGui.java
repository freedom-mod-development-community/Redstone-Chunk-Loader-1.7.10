package redstoneChunkLoader.Advance;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.world.ChunkCoordIntPair;
import org.lwjgl.opengl.GL11;
import redstoneChunkLoader.ContainerKEI;
import redstoneChunkLoader.network.PacketHandler;

import java.util.ArrayList;

public class ChunkLoadSettingGui extends GuiContainer {
    AdvanceChunkLoadTile tile;

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

        GuiButton button = new GuiButton(1, 320, height / 2 - 10, 80, 20, "ReForceChunk");
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
        if(tile.isUpdate){
            this.initGui();
            tile.isUpdate = false;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        int cX = this.width / 2;
        int cZ = this.height / 2;
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
    }
}
