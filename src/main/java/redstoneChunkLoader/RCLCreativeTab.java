package redstoneChunkLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class RCLCreativeTab extends CreativeTabs {
    public RCLCreativeTab() {
        super("Redstone Chunk Loader");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Item getTabIconItem(){
        return Item.getItemFromBlock(ModReadStoneChunkLoader.creativeTabIcon);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getTranslatedTabLabel(){
        return "AegisSystemMod";
    }
}
