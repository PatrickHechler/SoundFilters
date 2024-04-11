package com.tmtravlr.soundfilters;

import static com.tmtravlr.soundfilters.SoundFiltersConfig.LOGGER;
import static com.tmtravlr.soundfilters.SoundFiltersConfig.REVERB_FILTER;
import static com.tmtravlr.soundfilters.SoundFiltersConfig.configure;

import java.io.File;

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

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER = event.getModLog();
		
		File confFile = event.getSuggestedConfigurationFile();
		configure(confFile == null ? new File("soundfilters.conf") : confFile);
		proxy.registerTickHandlers();
		proxy.registerEventHandlers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//called as late as possible to give other mods time to register their blocks
		configure();
		
		REVERB_FILTER.density = 0.0F;
		REVERB_FILTER.diffusion = 0.6F;
		REVERB_FILTER.gain = 0.15F;
		REVERB_FILTER.gainHF = 0.8F;
		REVERB_FILTER.decayTime = 0.1F;
		REVERB_FILTER.decayHFRatio = 0.7F;
		REVERB_FILTER.reflectionsGain = 0.6F;
		REVERB_FILTER.reflectionsDelay = 0.0F;
		REVERB_FILTER.lateReverbGain = 0.9F;
		REVERB_FILTER.lateReverbDelay = 0.0F;
		REVERB_FILTER.airAbsorptionGainHF = 0.99F;
		REVERB_FILTER.roomRolloffFactor = 0.0F;
	}

}
