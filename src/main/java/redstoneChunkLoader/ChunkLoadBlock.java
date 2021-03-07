package redstoneChunkLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class ChunkLoadBlock extends BlockContainer {
    protected ChunkLoadBlock() {
        super(Material.rock);
        setBlockName("ChunkLoaderBlock");
        setBlockTextureName(ModReadStoneChunkLoader.DOMAIN + ":loaderoff");
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    protected IIcon blockIconOn;
    protected IIcon blockIconOff;

    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        TileEntity tile = p_149727_1_.getTileEntity(p_149727_2_, p_149727_3_, p_149727_4_);
        if (tile instanceof ChunkLoadTile) {
            if (!p_149727_1_.isRemote) {
                ChunkLoadTile chunkTile = (ChunkLoadTile) tile;
                if (chunkTile.clickCoolTime == 0) {
                    p_149727_5_.addChatMessage(new ChatComponentText("Now Chunk Load Range : " + chunkTile.getChunkLoadRangeMessage()));
                } else {
                    int range = chunkTile.forceChunkRange;
                    range++;
                    if (range > 4) {
                        range = 0;
                    }
                    chunkTile.setForceChunkRangeAndReForce(range);
                    p_149727_5_.addChatMessage(new ChatComponentText("Set to Chunk Load Range : " + chunkTile.getChunkLoadRangeMessage()));
                }

                chunkTile.clickCoolTime = 60;
            }
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
            if (tile instanceof ChunkLoadTile) {
                ChunkLoadTile chunkTile = (ChunkLoadTile) tile;
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
            if (tile instanceof ChunkLoadTile) {
                ChunkLoadTile chunkTile = (ChunkLoadTile) tile;
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
            if (tile instanceof ChunkLoadTile) {
                ChunkLoadTile chunkTile = (ChunkLoadTile) tile;
                if (chunkTile.chunkLoadON && !p_149674_1_.isBlockIndirectlyGettingPowered(p_149674_2_, p_149674_3_, p_149674_4_)) {
                    chunkTile.switchPower(false);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        this.blockIconOn = p_149651_1_.registerIcon(ModReadStoneChunkLoader.DOMAIN + ":loaderon");
        this.blockIconOff = p_149651_1_.registerIcon(ModReadStoneChunkLoader.DOMAIN + ":loaderoff");
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
        return new ChunkLoadTile();
    }
}
