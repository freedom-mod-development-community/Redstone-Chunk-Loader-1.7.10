package redstoneChunkLoader.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import redstoneChunkLoader.Advance.AdvanceChunkSettingSyncMessage;

public class PacketHandler {
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("KEI");

    public static void init() {
        registerMessage(new AdvanceChunkSettingSyncMessage(), AdvanceChunkSettingSyncMessage.class, 0, Side.SERVER);
        registerMessage(new AdvanceChunkSettingSyncMessage(), AdvanceChunkSettingSyncMessage.class, 0, Side.CLIENT);
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestMessageType, int discriminator, Side sendTo) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, discriminator, sendTo);
    }

    public static void sendPacketServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendPacketAll(IMessage message) {
        INSTANCE.sendToAll(message);
    }
}
