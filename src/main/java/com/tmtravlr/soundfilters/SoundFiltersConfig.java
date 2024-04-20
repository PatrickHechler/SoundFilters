package com.tmtravlr.soundfilters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.tmtravlr.soundfilters.filters.FilterLowPass;
import com.tmtravlr.soundfilters.filters.FilterReverb;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class SoundFiltersConfig {
	
	public static Logger         LOGGER;
	private static Configuration config;
	
	public static final int     ALL_METAS        = -1;
	private static final int    LEGACY_ALL_METAS = 16;
	private static final String ALL_METAS_STR    = "*";
	
	public static FilterLowPass LOW_PASS_FILTER = new FilterLowPass();
	public static FilterReverb  REVERB_FILTER   = new FilterReverb();
	
	private static final String S_DEBUG = "debug";
	
	private static final String N_DEBUG = "Debug";
	private static final String C_DEBUG = "Set to true to write simple debug info to the console.";
	public static boolean       DEBUG   = false;
	
	private static final String N_SUPER_DUPER_DEBUG = "High Output Debug";
	private static final String C_SUPER_DUPER_DEBUG =                     //
		/*	*/"You probably don't want to set this to true\n"             //
			+ "unless you actually want to debug the mod.\n"              //
			+ "It writes quite a lot in the console.";
	public static boolean       SUPER_DUPER_DEBUG   = false;
	
	private static final String N_CONVERT_LEGACY_META = "Convert legacy Meta 16";
	private static final String C_CONVERT_LEGACY_META =                                              //
		/*	*/"convert the legacy meta " + LEGACY_ALL_METAS + " values to " + ALL_METAS_STR + "\n"   //
			+ "0: do not convert\n"                                                                  //
			+ "1, 2: convert\n"                                                                      //
			+ "1: after convert: set this value to 0 (do one initial convert)\n"                     //
			+ "2: do not change this value (I don't know why you could want to use this value)"      //
			+ "the value of this option will be ignored if:\n"                                       //
			+ "  'Legacy Meta 16 Value' is set to true";
	private static int          CONVERT_LEGACY_META   = 0;
	
	private static final String N_USE_LEGACY_META = "Legacy Meta 16 Value";
	private static final String C_USE_LEGACY_META =                                                //
		/*	*/"if set to true the special meta value * will be invalid and\n"                      //
			+ "the meta value " + LEGACY_ALL_METAS + " will instead be converted to any meta\n"    //
			+ "if set to true the following settings will be ignored\n"                            //
			+ "  'Convert legacy Meta 16' will be treated like zero\n"                             //
			+ "  'Legacy Custom List' will be treated like true\n"                                 //
			+ "this restores compatibility of the custom occlusion/reverb lists (0.12 and before)";
	private static boolean      USE_LEGACY_META   = false;
	
	private static final String N_ADD_SPONGE = "Add Sponge";
	private static final String C_ADD_SPONGE =                                                      //
		/*	*/"Add 'minecraft:sponge-" + ALL_METAS_STR + "-2.0' to the occlusion list\n"            //
			+ "0: do not add sponge\n"                                                              //
			+ "1, 2: add sponge\n"                                                                  //
			+ "1: after sponge is added: set this value to 0 (do one initial add)\n"                //
			+ "2: do not change this value (I don't know why you could want to use this value)\n"   //
			+ "this allows a fluent transition of the old occlusion list (0.13 and before)\n"       //
			+ "to the new one. If there is already an entry for minecraft:sponge no new entry\n"    //
			+ "will be added (if the value will was 1, it still be set to 0)";
	private static int          ADD_SPONGE   = 0;
	
	
	private static final String S_LOW_PASS = "low_pass";
	
	private static final String N_DO_LOW_PASS = "Use Low Pass?";
	private static final String C_DO_LOW_PASS = "Set to false to disable low pass filter in water and lava.";
	public static boolean       DO_LOW_PASS   = true;
	
	private static final String N_WATER_LOW_PASS_AMOUNT = "Water Low Pass Amount";
	private static final String C_WATER_LOW_PASS_AMOUNT = "Set to false to disable low pass filter in water and lava.";
	public static float         WATER_LOW_PASS_AMOUNT   = 0.4f;
	
	private static final String N_WATER_VOLUME = "Water Low Pass Volume";
	private static final String C_WATER_VOLUME = "The multiplier for volume when you are in water. Lower is quieter.";
	public static float         WATER_VOLUME   = 1.0f;
	
	private static final String N_LAVA_LOW_PASS_AMOUNT = "Lava Low Pass Amount";
	private static final String C_LAVA_LOW_PASS_AMOUNT = "The amount of low pass that will be applied in lava. Lower is stronger.";
	public static float         LAVA_LOW_PASS_AMOUNT   = 0.1f;
	
	private static final String N_LAVA_VOLUME = "Lava Low Pass Volume";
	private static final String C_LAVA_VOLUME = "The multiplier for volume when you are in lava. Lower is quieter.";
	public static float         LAVA_VOLUME   = 0.6f;
	
	
	private static String S_OCCLUSION = "occlusion";
	
	private static String N_DO_OCCLUSION = "Use Occluded Sounds (muting sounds behind solid walls)?";
	private static String C_DO_OCCLUSION =                                                           //
		/*	*/"Set to false to disable low pass filter for sounds behind solid walls.\n"             //
			+ "If you are getting lag, disabling this might help.";
	public static boolean DO_OCCLUSION   = true;
	
	private static String N_OCCLUSION_PERCENT = "Occlusion Percent";
	private static String C_OCCLUSION_PERCENT =                     //
		/*	*/"The percentage of occlusion you can get.\n"          //
			+ "You can lower this if you find\n"                    //
			+ "the occlusion to be too much or raise it\n"          //
			+ "for a more noticeable effect.";
	public static float   OCCLUSION_PERCENT   = 1.0f;
	
	private static String               N_CUSTOM_OCCLUSION = "Specific block occlusion:";
	private static String               C_CUSTOM_OCCLUSION =                                          //
		/*	*/"Add new entries (each on a new line) in the format\n"                                  //
			+ "<block id>-<metadata>-<occlusion double> to customize how much sound\n"                //
			+ "they should absorb when they are between you and the sound source.\n"                  //
			+ "For the metadata, " + ALL_METAS_STR + " means any metadata value. The amount is a\n"   //
			+ "double, with 0.0 absorbing no sound (like air), and 1.0 being the normal\n"            //
			+ "amount, and 2.0 being twice the normal amount. By default, wool has\n"                 //
			+ "entry wool-" + ALL_METAS_STR + "-2.0 which is twice the normal sound absorbtion.";
	public static Map<BlockMeta,Double> CUSTOM_OCCLUSION   = new HashMap<>();
	
	
	private static String S_REVERB = "reverb";
	
	private static String N_DO_REVERB = "Use Reverb?";
	private static String C_DO_REVERB = "Set to false to disable reverb.";
	public static boolean DO_REVERB   = true;
	
	private static String N_REVERB_PERCENT = "Reverb Percent";
	private static String C_REVERB_PERCENT =                                             //
		/*	*/"The percentage of reverb you can get. You can lower this if you find\n"   //
			+ "the reverb to be too much (or raise it if you really want an\n"           //
			+ "echo).";
	public static float   REVERB_PERCENT   = 1.0f;
	
	private static String N_PROFILE_SIZE = "Number of blocks reverb will check through:";
	private static String C_PROFILE_SIZE =                                               //
		/*	*/"If you are getting lag, set this number lower. The higher it is,\n"       //
			+ "the more realistic the reverb will be.";
	public static int     PROFILE_SIZE   = 1024;
	
	private static String N_DO_SKY_CHECKS = "Do sky checks:";
	private static String C_DO_SKY_CHECKS =                                             //
		/*	*/"If this is true, when you're in an area that can see the sky, then\n"    //
			+ "there will be less reverb. This is for aboveground areas with\n"         //
			+ "lots of stone and such like extreme hills biomes. There still might\n"   //
			+ "be some, but less then when the sky isn't visible.";
	public static boolean DO_SKY_CHECKS   = true;
	
	
	private static String               N_CUSTOM_REVERB = "Specific block reverb:";
	private static String               C_CUSTOM_REVERB =                                              //
		/*	*/"Add values to this list (each on a new line) in the format\n"                           //
			+ "<block id>-<metadata>-<reverb double>, to change how the block\n"                       //
			+ "with that metadata absorbs or creates reverb. If the\n"                                 //
			+ "metadata is " + ALL_METAS_STR + ", that means it will apply to any metadata value.\n"   //
			+ "By default things like wool, snow, carpets, and plants absorb reverb\n"                 //
			+ "(value 0.0), things like wood and dirt are neutral (value 1.0),\n"                      //
			+ "and things like stone, metal, ice, and glass create reverb (value 2.0).\n"              //
			+ "So if, say, you wanted to add pumpkins of any metadata to the blocks\n"                 //
			+ "that create reverb, you would put pumpkin-" + ALL_METAS_STR + "-2.0 on a new line.";
	public static Map<BlockMeta,Double> CUSTOM_REVERB   = new HashMap<>();
	
	private static String           N_DIM_REVERB = "Specific dimension reverb:";
	private static String           C_DIM_REVERB =                                               //
		/*	*/"Add values to this list (each on a new line) in the format\n"                     //
			+ "<reverb double> <dimension name>, to change the base reverb\n"                    //
			+ "of the dimension with the given name.\n"                                          //
			+ "By default the_nether will have a high base reverb and all other dimensions none";
	public static Map<String,Float> DIM_REVERB   = new HashMap<>();
	
	public static Configuration config() {
		return config;
	}
	
	public static void configure() {
		configure(null);
	}
	
	public static void configure(File confFile) {
		if ( confFile != null ) {
			if ( config != null ) {
				throw new IllegalStateException("config file already set!");
			}
			config = new Configuration(confFile);
			config.load();
		} else if ( !config.hasChanged() ) {
			config.load();
		}
		
		// S_DEBUG
		DEBUG = config.getBoolean(N_DEBUG, S_DEBUG, false, C_DEBUG);
		SUPER_DUPER_DEBUG = config.getBoolean(N_SUPER_DUPER_DEBUG, S_DEBUG, false, C_SUPER_DUPER_DEBUG);
		USE_LEGACY_META = config.getBoolean(N_USE_LEGACY_META, S_DEBUG, false, C_USE_LEGACY_META);
		CONVERT_LEGACY_META = config.getInt(N_CONVERT_LEGACY_META, S_DEBUG, 1, 0, 2, C_CONVERT_LEGACY_META);
		ADD_SPONGE = config.getInt(N_ADD_SPONGE, S_DEBUG, 1, 0, 2, C_ADD_SPONGE);
		if ( USE_LEGACY_META ) {
			CONVERT_LEGACY_META = 0;
		} else if ( CONVERT_LEGACY_META == 1 ) {
			ConfigCategory cat = config.getCategory(S_DEBUG);
			Property prop = cat.get(N_CONVERT_LEGACY_META);
			prop.set(0);
		}
		if ( ADD_SPONGE == 1 ) {
			ConfigCategory cat = config.getCategory(S_DEBUG);
			Property prop = cat.get(N_ADD_SPONGE);
			prop.set(0);
		}
		config.getCategory(S_DEBUG).setPropertyOrder(new ArrayList<>(Arrays.asList(N_DEBUG, N_SUPER_DUPER_DEBUG, //
			N_ADD_SPONGE, N_USE_LEGACY_META, N_CONVERT_LEGACY_META)));
		
		// S_LOW_PASS
		DO_LOW_PASS = config.getBoolean(N_DO_LOW_PASS, S_LOW_PASS, true, C_DO_LOW_PASS);
		WATER_LOW_PASS_AMOUNT = config.getFloat(N_WATER_LOW_PASS_AMOUNT, S_LOW_PASS, 0.4f, 0.0f, 1.0f, C_WATER_LOW_PASS_AMOUNT);
		WATER_VOLUME = config.getFloat(N_WATER_VOLUME, S_LOW_PASS, 1.0f, 0.0f, 1.0f, C_WATER_VOLUME);
		LAVA_LOW_PASS_AMOUNT = config.getFloat(N_LAVA_LOW_PASS_AMOUNT, S_LOW_PASS, 0.2f, 0.0f, 1.0f, C_LAVA_LOW_PASS_AMOUNT);
		LAVA_VOLUME = config.getFloat(N_LAVA_VOLUME, S_LOW_PASS, 0.6f, 0.0f, 1.0f, C_LAVA_VOLUME);
		
		// S_OCCLUSION
		DO_OCCLUSION = config.getBoolean(N_DO_OCCLUSION, S_OCCLUSION, true, C_DO_OCCLUSION);
		OCCLUSION_PERCENT = config.getFloat(N_OCCLUSION_PERCENT, S_OCCLUSION, 1.0f, 0.0f, Float.POSITIVE_INFINITY,
			C_OCCLUSION_PERCENT);
		customBlockList(N_CUSTOM_OCCLUSION, S_OCCLUSION, C_CUSTOM_OCCLUSION, CUSTOM_OCCLUSION, true, //
			"wool-" + ALL_METAS_STR + "-2.0", //
			"minecraft:sponge-" + ALL_METAS_STR + "-2.0", //
			"securitycraft:reinforced_wool-" + ALL_METAS_STR + "-1.5");
		
		// S_REVERB
		DO_REVERB = config.getBoolean(N_DO_REVERB, S_REVERB, true, C_DO_REVERB);
		REVERB_PERCENT = config.getFloat(N_REVERB_PERCENT, S_REVERB, 1.0f, 0.0f, 2.0f, C_REVERB_PERCENT);
		PROFILE_SIZE = config.getInt(N_PROFILE_SIZE, S_REVERB, 1024, 0, Integer.MAX_VALUE, C_PROFILE_SIZE);
		DO_SKY_CHECKS = config.getBoolean(N_DO_SKY_CHECKS, S_REVERB, true, C_DO_SKY_CHECKS);
		customBlockList(N_CUSTOM_REVERB, S_REVERB, C_CUSTOM_REVERB, CUSTOM_REVERB, false, "soul_sand-" + ALL_METAS_STR + "-2.0");
		customDimensionList(N_DIM_REVERB, S_REVERB, C_DIM_REVERB, DIM_REVERB, "2.0 the_nether");
		
		
		if ( DEBUG ) {
			LOGGER.debug("customOcclusion: " + CUSTOM_OCCLUSION);
			LOGGER.debug("customReverb: " + CUSTOM_REVERB);
		}
		
		if ( config.hasChanged() ) {
			config.save();
		}
	}
	
	private static void customDimensionList(String name, String section, String comment, Map<String,Float> targetMap,
		String... defaultValues) {
		String[] values = config.getStringList(name, section, defaultValues, comment);
		values = parseCustomDimensionList(values, targetMap);
		if ( values != null ) {
			ConfigCategory cat = config.getCategory(section);
			Property prop = cat.get(name);
			prop.set(values);
		}
	}
	
	private static String[] parseCustomDimensionList(String[] dimList, Map<String,Float> customMap) {
		customMap.clear();
		for (int i = 0; i < dimList.length; i++) {
			String customInfo = dimList[i];
			if ( customInfo.isEmpty() ) continue;
			int si = customInfo.indexOf(' ');
			if ( si == -1 ) {
				LOGGER.warn("invalid dimension entry: '" + dimList[i] + "' (no space)");
				continue;
			}
			float value = Float.parseFloat(customInfo.substring(0, si).trim());
			if ( !( value >= 0D ) ) {
				LOGGER.warn("invalid dimension entry: '" + dimList[i] + "' (negative or NAN value)");
				continue;
			}
			customMap.put(customInfo.substring(si + 1).trim(), Float.valueOf(value));
		}
		return null;
	}
	
	private static void customBlockList(String name, String section, String comment, Map<BlockMeta,Double> targetMap,
		boolean occlusion, String... defaultValues) {
		String[] values = config.getStringList(name, section, defaultValues, comment);
		values = parseCustomBlockList(values, targetMap, section, occlusion);
		if ( values != null ) {
			ConfigCategory cat = config.getCategory(section);
			Property prop = cat.get(name);
			prop.set(values);
		}
	}
	
	private static String[] parseCustomBlockList(String[] blocksList, Map<BlockMeta,Double> customMap, String sectionName,
		boolean occlusion) {
		boolean replace = false;
		customMap.clear();
		for (int i = 0; i < blocksList.length; i++) {
			String customInfo = blocksList[i];
			Block block = null;
			String blockName = "";
			int meta = -1;
			double strength = -1;
			
			try {
				int lastDashIndex = customInfo.lastIndexOf('-');
				int secondLastDashIndex = customInfo.lastIndexOf('-', lastDashIndex - 1);
				blockName = customInfo.substring(0, secondLastDashIndex);
				String metaStr = customInfo.substring(secondLastDashIndex + 1, lastDashIndex);
				if ( USE_LEGACY_META ) {
					meta = Integer.parseInt(metaStr);
					if ( meta == LEGACY_ALL_METAS ) {
						meta = ALL_METAS;
					} else if ( meta < 0 ) {
						throw new NumberFormatException("only non-negative values are permitted");
					}
				} else if ( customInfo.startsWith("*-", secondLastDashIndex + 1) ) {
					meta = ALL_METAS;
				} else {
					meta = Integer.parseInt(metaStr);
					if ( meta < 0 ) {
						throw new NumberFormatException("only non-negative values are permitted");
					}
					if ( CONVERT_LEGACY_META > 0 && meta == LEGACY_ALL_METAS ) {
						meta = ALL_METAS;
						replace = true;
						blocksList[i] = customInfo.substring(0, secondLastDashIndex) + "-*" + customInfo.substring(lastDashIndex);
					}
				}
				strength = Double.parseDouble(customInfo.substring(lastDashIndex + 1));
				block = Block.getBlockFromName(blockName);
			} catch ( Exception e ) {
				LOGGER.error("Error while loading in custom " + sectionName + " entry: '" + customInfo + "'", e);
			}
			// meta value can only be negative in two cases:
			// 1: star value used
			// 2: failure during assignments
			// in the first case this is ok, in the second case strength/block is also still invalid
			if ( block != null && strength >= 0 ) {
				if ( DEBUG ) {
					LOGGER.debug("Loaded custom " + sectionName + ": block " + blockName + ", with "
						+ ( meta == ALL_METAS ? "any meta" : "meta " + meta ) + ", and amount " + strength);
				}
				customMap.put(new BlockMeta(block, meta), strength);
			}
		}
		if ( occlusion && ADD_SPONGE > 0 ) {
			Block sponge = Block.getBlockFromName("minecraft:sponge");
			boolean found = false;
			for (BlockMeta bm : customMap.keySet()) {
				if ( bm.block == sponge ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				replace = true;
				blocksList = Arrays.copyOf(blocksList, blocksList.length + 1);
				if (USE_LEGACY_META) {
					blocksList[blocksList.length - 1] = "minecraft:sponge-" + LEGACY_ALL_METAS + "-2.0";
				} else {
					blocksList[blocksList.length - 1] = "minecraft:sponge-" + ALL_METAS_STR + "-2.0";
				}
			}
		}
		if ( replace ) {
			return blocksList;
		}
		return null;
	}
	
}
