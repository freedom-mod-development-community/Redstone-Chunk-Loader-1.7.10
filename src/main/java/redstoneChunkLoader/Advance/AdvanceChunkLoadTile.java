package redstoneChunkLoader.Advance;

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

public class AdvanceChunkLoadTile extends TileEntity implements IChunkLoadHandler {
    public AdvanceChunkLoadTile() {
    }

    public boolean chunkLoadON = false;

    private ChunkCoordIntPair _thisPosChunk;

    public ChunkCoordIntPair getThisPosChunk() {
        if (_thisPosChunk == null) {
            int xC = MathHelper.floor_double(this.xCoord) >> 4;
            int zC = MathHelper.floor_double(this.zCoord) >> 4;
            _thisPosChunk = new ChunkCoordIntPair(xC, zC);
        }
        return _thisPosChunk;
    }


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

    private ArrayList<ChunkCoordIntPair> _forcedChunks = new ArrayList<ChunkCoordIntPair>();

    public ArrayList<ChunkCoordIntPair> getForcedChunks() {
        return _forcedChunks;
    }

    public boolean isUpdate=false;
    public void resetAndReForceChunk(ArrayList<ChunkCoordIntPair> chunks) {
        this.unforceChunk();
        this._forcedChunks = chunks;
        this.forceChunk();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        isUpdate = true;
    }


    private ForgeChunkManager.Ticket chunkTicket = null;

    @Override
    public void chunkLoaderInit(ForgeChunkManager.Ticket ticket) {
        if (!this.worldObj.isRemote) {
            if (ticket != null) {
                if (this.chunkTicket == null) {
                    this.chunkTicket = ticket;
                }
                forceChunk();
            }
        }
    }


    /**
     * チャンクロード設定
     */
    public void forceChunk() {
        if (chunkLoadON) {
            for (ChunkCoordIntPair chunk : _forcedChunks) {
                ForgeChunkManager.forceChunk(this.chunkTicket, chunk);
            }
        }
    }

    /**
     * チャンクロード解除
     */
    public void unforceChunk() {
        for (ChunkCoordIntPair chunk : _forcedChunks) {
            ForgeChunkManager.unforceChunk(this.chunkTicket, chunk);
        }
    }

    /**
     * チャンクロードON/OFF切り替え
     *
     * @param onoff trueでON
     */
    public void switchPower(boolean onoff) {
        if (chunkLoadON != onoff) {
            chunkLoadON = onoff;
            int meta = chunkLoadON ? 1 : 0;
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, meta, 3);
            if (!this.worldObj.isRemote) {
                if (chunkLoadON) {
                    forceChunk();
                } else {
                    unforceChunk();
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_) {
        super.readFromNBT(p_145839_1_);
        //チャンクリスト
        _forcedChunks.clear();
        byte[] bytes = p_145839_1_.getByteArray("chunks");
        for (int i = 0; i < bytes.length; i += 2) {
            byte x = bytes[i];
            byte z = bytes[i + 1];
            _forcedChunks.add(new ChunkCoordIntPair(x, z));
        }

        this.chunkLoadON = p_145839_1_.getBoolean("chunkLoadON");
        switchPower(chunkLoadON);
    }

    @Override
    public void writeToNBT(NBTTagCompound p_145841_1_) {
        super.writeToNBT(p_145841_1_);
        byte bytes[] = new byte[_forcedChunks.size() * 2];
        for (int i = 0; i < _forcedChunks.size(); i++) {
            ChunkCoordIntPair pair = _forcedChunks.get(i);
            bytes[i * 2] = (byte) pair.chunkXPos;
            bytes[i * 2 + 1] = (byte) pair.chunkZPos;
        }
        p_145841_1_.setByteArray("chunks", bytes);
        p_145841_1_.setBoolean("chunkLoadON", chunkLoadON);
    }

    /**
     * 同期用のパケット送信
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, compound);
    }

    /**
     * 同期用のパケット受信
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }
}
