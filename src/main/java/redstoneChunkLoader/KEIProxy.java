package redstoneChunkLoader;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.World;

public abstract class KEIProxy {
    public abstract World getWorld(MessageContext ctx);
}
