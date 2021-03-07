package redstoneChunkLoader;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;

@Mod(modid = ModReadStoneChunkLoader.MOD_ID, name = "RedstoneChunkLoader", version = "[1.7.10]-0.0")
public class ModReadStoneChunkLoader {
    public static final String MOD_ID = "RedstoneChunkLoader";
    public static final String DOMAIN = "redstoneChunkLoader";
    @Mod.Instance("RedstoneChunkLoader")
    public static ModReadStoneChunkLoader instance;

    @SidedProxy(clientSide = "redstoneChunkLoader.RCLClientProxy")
    public static RCLProxy proxy;


    @SubscribeEvent
    public void EntityJoinWorldEvent(EntityJoinWorldEvent event) {
//        if(event.world.isRemote && Vars.doroScore==null) {
//            Scoreboard scoreboard = event.world.getScoreboard();
//            ScoreObjective scoreObjective = scoreboard.getObjective(Vars.doroScoreName);
//            if(scoreObjective == null){
//                scoreboard.addScoreObjective(Vars.doroScoreName, IScoreObjectiveCriteria.field_96641_b);
//                scoreObjective = scoreboard.getObjective(Vars.doroScoreName);
//            }
//            Vars.doroScore = scoreboard.func_96529_a(Minecraft.getMinecraft().thePlayer.getCommandSenderName(), scoreObjective);
//        }
    }

    @SubscribeEvent
    public void WorldUnloadEvent(WorldEvent.Unload event) {
    }

    public static Block creativeTabIcon;
    public static CreativeTabs creativeTab = new RCLCreativeTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        creativeTabIcon = new ChunkLoadBlock();
        GameRegistry.registerBlock(creativeTabIcon, "chunkLoadBlock");
        GameRegistry.registerTileEntity(ChunkLoadTile.class, "ChunkLoadTile");

        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ForgeChunkManager.LoadingCallback() {
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
                boolean flag = false;
                for (ForgeChunkManager.Ticket ticket : tickets) {
                    NBTTagCompound tags = ticket.getModData();
                    if (!tags.hasNoTags()) {
                        if (world != null) {
                            TileEntity tileEntity = world.getTileEntity(tags.getInteger("xCoord"), tags.getInteger("yCoord"), tags.getInteger("zCoord"));
                            if (tileEntity instanceof IChunkLoadHandler){
                                flag = true;
                                ((IChunkLoadHandler)tileEntity).chunkLoaderInit(ticket);
                            }
                        }
                    }
                }
            }
//            if (flag) {
//                Logger.info("ticketsLoaded")
//            }
        });
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(new TickEventManager());
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            NetworkRegistry.INSTANCE.registerGuiHandler(ModReadStoneChunkLoader.instance, new GuiHandler());
        }
    }
}