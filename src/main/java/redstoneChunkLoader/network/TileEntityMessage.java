package redstoneChunkLoader.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import redstoneChunkLoader.ModReadStoneChunkLoader;
import redstoneChunkLoader.Vector.Vec3I;

public abstract class TileEntityMessage implements IMessage {
    protected Vec3I pos;

    public TileEntityMessage() {
    }

    public TileEntityMessage(TileEntity tile) {
        pos = new Vec3I(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new Vec3I(buf.readInt(), buf.readInt(), buf.readInt());
        read(buf);
    }

    protected abstract void read(ByteBuf buf);

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.x);
        buf.writeInt(pos.y);
        buf.writeInt(pos.z);
        write(buf);
    }

    protected abstract void write(ByteBuf buf);


    protected World getWorld(MessageContext ctx) {
        return ModReadStoneChunkLoader.instance.proxy.getWorld(ctx);
    }

    protected TileEntity getTileEntity(MessageContext ctx) {
        World world = getWorld(ctx);
        if (world != null) {
            return world.getTileEntity(pos.x, pos.y, pos.z);
        } else {
            return null;
        }
    }
}
