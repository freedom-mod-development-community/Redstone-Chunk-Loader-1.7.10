package redstoneChunkLoader;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class KEIClientProxy extends KEIProxy{
    @Override
    public World getWorld(MessageContext ctx) {
        return Minecraft.getMinecraft().theWorld;
    }
}
