package redstoneChunkLoader;


import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import redstoneChunkLoader.Advance.AdvanceChunkLoadBlock;
import redstoneChunkLoader.Advance.AdvanceChunkLoadTile;
import redstoneChunkLoader.network.PacketHandler;
import redstoneChunkLoader.normal.ChunkLoadBlock;
import redstoneChunkLoader.normal.ChunkLoadTile;

import java.util.List;

@Mod(modid = ModReadStoneChunkLoader.MOD_ID, name = "RedstoneChunkLoader", version = "[1.7.10]-0.0")
public class ModReadStoneChunkLoader {
    public static final String MOD_ID = "RedstoneChunkLoader";
    public static final String DOMAIN = "redstonechunkloader";
    @Mod.Instance("RedstoneChunkLoader")
    public static ModReadStoneChunkLoader instance;
    public static Block creativeTabIcon;

    @SidedProxy(clientSide = "redstoneChunkLoader.KEIClientProxy", serverSide = "redstoneChunkLoader.KEICommonProxy")
    public static KEIProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        creativeTabIcon = new ChunkLoadBlock();
        GameRegistry.registerBlock(creativeTabIcon, "chunkLoadBlock");
        GameRegistry.registerBlock(new AdvanceChunkLoadBlock(), "AdvanceChunkLoadBlock");
        GameRegistry.registerTileEntity(ChunkLoadTile.class, "ChunkLoadTile");
        GameRegistry.registerTileEntity(AdvanceChunkLoadTile.class, "AdvanceChunkLoadTile");

        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ForgeChunkManager.LoadingCallback() {
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
                for (ForgeChunkManager.Ticket ticket : tickets) {
                    NBTTagCompound tags = ticket.getModData();
                    if (!tags.hasNoTags()) {
                        if (world != null) {
                            TileEntity tileEntity = world.getTileEntity(tags.getInteger("xCoord"), tags.getInteger("yCoord"), tags.getInteger("zCoord"));
                            if (tileEntity instanceof IChunkLoadHandler) {
                                ((IChunkLoadHandler) tileEntity).chunkLoaderInit(ticket);
                            }
                        }
                    }
                }
            }
        });
        PacketHandler.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }
}