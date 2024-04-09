package com.tmtravlr.soundfilters;

import static com.tmtravlr.soundfilters.SoundFiltersConfig.ALL_METAS;
import static com.tmtravlr.soundfilters.SoundFiltersConfig.config;
import static com.tmtravlr.soundfilters.SoundFiltersConfig.configure;
import static com.tmtravlr.soundfilters.SoundFiltersConfig.logger;
import static com.tmtravlr.soundfilters.SoundFiltersConfig.reverbFilter;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;

/**
 * Main mod class.
 *
 * @author Tmtravlr (Rebeca Rey)
 * @Date 2014
 */
@Mod(modid = SoundFiltersMod.MOD_ID, name = SoundFiltersMod.MOD_NAME, version = SoundFiltersMod.MOD_VERSION, 
	clientSideOnly = true, guiFactory = "com.tmtravlr.soundfilters.ConfigGUIFactory")
public class SoundFiltersMod {
	public static final String MOD_ID = "soundfilters";
	public static final String MOD_NAME = "Sound Filters";
	public static final String MOD_VERSION = "0.13.0_for_1.12";//THIS LINE IS AUTOMATICALLY GENERATED

	@Instance("soundfilters")
	public static SoundFiltersMod soundFilters;

	@SidedProxy(clientSide = "com.tmtravlr.soundfilters.ClientProxy", serverSide = "com.tmtravlr.soundfilters.CommonProxy")
	public static CommonProxy proxy;

	static {
		if (SoundSystemConfig.getLibraries() != null) {
			SoundSystemConfig.getLibraries().clear();
		}

		try {
			SoundSystemConfig.addLibrary(ModifiedLWJGLOpenALLibrary.class);
		} catch (SoundSystemException e) {
			System.out.println("[Sound Filters] Problem while loading modified library!");
			e.printStackTrace();
		}

		System.out.println("[Sound Filters] Loaded modified library.");
	}

	/**
	 * Holds info about both a block's id and metadata.
	 *
	 * @author Rebeca
	 * @Date 2014
	 */
	public static class BlockMeta {
		public Block block;
		public int meta;

		public BlockMeta(Block b, int m) {
			block = b;
			meta = m;
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

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());

		configure();
		proxy.registerTickHandlers();
		proxy.registerEventHandlers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//called as late as possible to give other mods time to register their blocks
		configure();
		
		reverbFilter.density = 0.0F;
		reverbFilter.diffusion = 0.6F;
		reverbFilter.gain = 0.15F;
		reverbFilter.gainHF = 0.8F;
		reverbFilter.decayTime = 0.1F;
		reverbFilter.decayHFRatio = 0.7F;
		reverbFilter.reflectionsGain = 0.6F;
		reverbFilter.reflectionsDelay = 0.0F;
		reverbFilter.lateReverbGain = 0.9F;
		reverbFilter.lateReverbDelay = 0.0F;
		reverbFilter.airAbsorptionGainHF = 0.99F;
		reverbFilter.roomRolloffFactor = 0.0F;
	}

}
