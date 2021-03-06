package redstoneChunkLoader;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import redstoneChunkLoader.Advance.AdvanceChunkLoadTile;
import redstoneChunkLoader.Advance.ChunkLoadSettingGui;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof AdvanceChunkLoadTile) {
                return new ContainerKEI();
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof AdvanceChunkLoadTile) {
                return new ChunkLoadSettingGui((AdvanceChunkLoadTile) tile);
            }
        }
        return null;
    }
}
