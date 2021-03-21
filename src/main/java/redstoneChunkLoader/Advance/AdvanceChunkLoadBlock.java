package redstoneChunkLoader.Advance;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import redstoneChunkLoader.ModReadStoneChunkLoader;

import java.util.Random;

public class AdvanceChunkLoadBlock extends BlockContainer {
    public AdvanceChunkLoadBlock() {
        super(Material.rock);
        setBlockName("AdvanceChunkLoaderBlock");
        setBlockTextureName(ModReadStoneChunkLoader.DOMAIN + ":advanceloader-off");
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    protected IIcon blockIconOn;
    protected IIcon blockIconOff;

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof AdvanceChunkLoadTile) {
            player.openGui(ModReadStoneChunkLoader.instance, 0, world, x, y, z);
            return true;
        }
        return false;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_) {
        if (!p_149726_1_.isRemote) {
            TileEntity tile = p_149726_1_.getTileEntity(p_149726_2_, p_149726_3_, p_149726_4_);
            if (tile instanceof AdvanceChunkLoadTile) {
                AdvanceChunkLoadTile chunkTile = (AdvanceChunkLoadTile) tile;
                if (chunkTile.chunkLoadON && !p_149726_1_.isBlockIndirectlyGettingPowered(p_149726_2_, p_149726_3_, p_149726_4_)) {
                    p_149726_1_.scheduleBlockUpdate(p_149726_2_, p_149726_3_, p_149726_4_, this, 4);
                } else if (!chunkTile.chunkLoadON && p_149726_1_.isBlockIndirectlyGettingPowered(p_149726_2_, p_149726_3_, p_149726_4_)) {
                    chunkTile.switchPower(true);
                }
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {
        if (!p_149695_1_.isRemote) {
            TileEntity tile = p_149695_1_.getTileEntity(p_149695_2_, p_149695_3_, p_149695_4_);
            if (tile instanceof AdvanceChunkLoadTile) {
                AdvanceChunkLoadTile chunkTile = (AdvanceChunkLoadTile) tile;
                if (chunkTile.chunkLoadON && !p_149695_1_.isBlockIndirectlyGettingPowered(p_149695_2_, p_149695_3_, p_149695_4_)) {
                    p_149695_1_.scheduleBlockUpdate(p_149695_2_, p_149695_3_, p_149695_4_, this, 4);
                } else if (!chunkTile.chunkLoadON && p_149695_1_.isBlockIndirectlyGettingPowered(p_149695_2_, p_149695_3_, p_149695_4_)) {
                    chunkTile.switchPower(true);
                }
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {
        if (!p_149674_1_.isRemote) {
            TileEntity tile = p_149674_1_.getTileEntity(p_149674_2_, p_149674_3_, p_149674_4_);
            if (tile instanceof AdvanceChunkLoadTile) {
                AdvanceChunkLoadTile chunkTile = (AdvanceChunkLoadTile) tile;
                if (chunkTile.chunkLoadON && !p_149674_1_.isBlockIndirectlyGettingPowered(p_149674_2_, p_149674_3_, p_149674_4_)) {
                    chunkTile.switchPower(false);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        this.blockIconOn = p_149651_1_.registerIcon(ModReadStoneChunkLoader.DOMAIN + ":advanceloader-on");
        this.blockIconOff = p_149651_1_.registerIcon(ModReadStoneChunkLoader.DOMAIN + ":advanceloader-off");
        this.blockIcon = this.blockIconOff;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta > 0) {
            return blockIconOn;
        } else {
            return blockIconOff;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new AdvanceChunkLoadTile();
    }
}
