package redstoneChunkLoader;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.World;

public class KEICommonProxy extends KEIProxy{
    @Override
    public World getWorld(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity.worldObj;
    }
}
