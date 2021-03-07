package redstoneChunkLoader;

import net.minecraft.client.Minecraft;

public class RCLClientProxy extends RCLProxy {
    Minecraft mc;

    @Override
    public void init() {
        mc = Minecraft.getMinecraft();
    }
}
