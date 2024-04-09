package com.tmtravlr.soundfilters;

import static com.tmtravlr.soundfilters.SoundFiltersConfig.ALL_METAS;

import net.minecraft.block.Block;

/**
 * Holds info about both a block's id and metadata.
 *
 * @author Rebeca
 * @Date 2014
 */
public class BlockMeta {
	
	public final Block block;
	public final int   meta;
	
	public BlockMeta(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Block.getIdFromBlock(block);
		result = prime * result + meta;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof BlockMeta)) { return false; }
		BlockMeta other = (BlockMeta) obj;
		if (block != other.block && Block.getIdFromBlock(block) != Block.getIdFromBlock(other.block)) return false;
		if (meta != other.meta) { return false; }
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BlockMeta [block=");
		builder.append(block);
		builder.append(", meta=");
		if (meta == ALL_METAS) {
			builder.append("all");
		} else {
			builder.append(meta);
		}
		builder.append(']');
		return builder.toString();
	}

}
