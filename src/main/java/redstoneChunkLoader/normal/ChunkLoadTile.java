package redstoneChunkLoader.normal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import redstoneChunkLoader.IChunkLoadHandler;
import redstoneChunkLoader.ModReadStoneChunkLoader;

import java.util.ArrayList;

public class ChunkLoadTile extends TileEntity implements IChunkLoadHandler {
    public ChunkLoadTile() {
    }

    public int forceChunkRange = 1;
    public boolean chunkLoadON = false;

    public int clickCoolTime = 0;

    private boolean isInited = false;

    public void initEntity() {
        if (!this.worldObj.isRemote && this.chunkTicket == null) {
            ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(ModReadStoneChunkLoader.instance, this.worldObj, ForgeChunkManager.Type.NORMAL);
            if (ticket != null) {
                NBTTagCompound tag = ticket.getModData();
                tag.setInteger("yCoord", this.yCoord);
                tag.setInteger("xCoord", this.xCoord);
                tag.setInteger("zCoord", this.zCoord);
                this.chunkLoaderInit(ticket);
            }
        }

        super.validate();
    }


    @Override
    public void updateEntity() {
        if (!isInited) {
            initEntity();
            isInited = true;
        }
        if (clickCoolTime > 0) {
            clickCoolTime--;
        }
    }

    private final ArrayList<ChunkCoordIntPair> forcedChunks = new ArrayList<ChunkCoordIntPair>();

    private ForgeChunkManager.Ticket chunkTicket = null;

    @Override
    public void chunkLoaderInit(ForgeChunkManager.Ticket ticket) {
        if (!this.worldObj.isRemote) {
            if (ticket != null) {
                if (this.chunkTicket == null) {
                    this.chunkTicket = ticket;
                }
                forceChunk(forceChunkRange);
            }
        }
    }

    public String getChunkLoadRangeMessage() {
        int rang1 = forceChunkRange * 2 + 1;
        return forceChunkRange + " (" + rang1 + "*" + rang1 + ")";
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param range ????????????????????????????????????
     */
    public void setForceChunkRangeAndReForce(int range) {
        this.forceChunkRange = range;
        unforceChunk();
        forceChunk(range);
    }

    /**
     * ???????????????????????????
     *
     * @param range ????????????????????????
     *              0:???????????????????????????????????????
     *              1:????????????????????????????????????????????????????????????(3*3????????????)
     *              2:(5*5????????????)
     */
    public void forceChunk(int range) {
        forcedChunks.clear();
        if (chunkLoadON) {
            int xC = MathHelper.floor_double(this.xCoord) >> 4;
            int zC = MathHelper.floor_double(this.zCoord) >> 4;
            for (int x = xC - range; x <= xC + range; x++) {
                for (int z = zC - range; z <= zC + range; z++) {
                    forcedChunks.add(new ChunkCoordIntPair(x, z));
                }
            }
            for (ChunkCoordIntPair chunk : forcedChunks) {
                ForgeChunkManager.forceChunk(this.chunkTicket, chunk);
            }
        }
    }

    /**
     * ???????????????????????????
     */
    public void unforceChunk() {
        for (ChunkCoordIntPair chunk : forcedChunks) {
            ForgeChunkManager.unforceChunk(this.chunkTicket, chunk);
        }
    }

    /**
     * ?????????????????????ON/OFF????????????
     *
     * @param onoff true???ON
     */
    public void switchPower(boolean onoff) {
        if (chunkLoadON != onoff) {
            chunkLoadON = onoff;
            int meta = chunkLoadON? 1 : 0;
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, meta, 3);
            if (!this.worldObj.isRemote) {
                if (chunkLoadON) {
                    forceChunk(forceChunkRange);
                } else {
                    unforceChunk();
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_) {
        super.readFromNBT(p_145839_1_);
        this.forceChunkRange = p_145839_1_.getInteger("forceChunkRange");
        this.chunkLoadON = p_145839_1_.getBoolean("chunkLoadON");
        switchPower(chunkLoadON);
    }

    @Override
    public void writeToNBT(NBTTagCompound p_145841_1_) {
        super.writeToNBT(p_145841_1_);
        p_145841_1_.setInteger("forceChunkRange", forceChunkRange);
        p_145841_1_.setBoolean("chunkLoadON", chunkLoadON);
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, compound);
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }
}
