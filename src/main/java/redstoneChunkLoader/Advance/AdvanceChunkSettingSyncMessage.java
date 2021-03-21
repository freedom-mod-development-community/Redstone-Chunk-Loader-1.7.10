package redstoneChunkLoader.Advance;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import redstoneChunkLoader.network.PacketHandler;
import redstoneChunkLoader.network.TileEntityMessage;

import java.util.ArrayList;

public class AdvanceChunkSettingSyncMessage extends TileEntityMessage implements IMessageHandler<AdvanceChunkSettingSyncMessage, IMessage> {
    ArrayList<ChunkCoordIntPair> chunks = new ArrayList<ChunkCoordIntPair>();

    public AdvanceChunkSettingSyncMessage() {
    }

    public AdvanceChunkSettingSyncMessage(AdvanceChunkLoadTile tile, ArrayList<ChunkCoordIntPair> chunks) {
        super(tile);
        this.chunks = chunks;
    }

    @Override
    protected void read(ByteBuf buf) {
        NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);
        ArrayList<ChunkCoordIntPair> chunks = new ArrayList<ChunkCoordIntPair>();
        byte[] bytes = tagCompound.getByteArray("chunks");
        for (int i = 0; i < bytes.length; i += 2) {
            byte x = bytes[i];
            byte z = bytes[i + 1];
            chunks.add(new ChunkCoordIntPair(x, z));
        }
        this.chunks = chunks;
    }

    @Override
    protected void write(ByteBuf buf) {
        byte bytes[] = new byte[chunks.size() * 2];
        for (int i = 0; i < chunks.size(); i++) {
            ChunkCoordIntPair pair = chunks.get(i);
            bytes[i * 2] = (byte) pair.chunkXPos;
            bytes[i * 2 + 1] = (byte) pair.chunkZPos;
        }
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setByteArray("chunks", bytes);
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    @Override
    public IMessage onMessage(AdvanceChunkSettingSyncMessage message, MessageContext ctx) {
        TileEntity tile = message.getTileEntity(ctx);
        if (tile instanceof AdvanceChunkLoadTile) {
            ((AdvanceChunkLoadTile) tile).resetAndReForceChunk(message.chunks);
        }
        if(ctx.side == Side.SERVER){
            PacketHandler.sendPacketAll(message);
        }
        return null;
    }
}
