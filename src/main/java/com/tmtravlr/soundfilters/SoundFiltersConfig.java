package com.tmtravlr.soundfilters;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.tmtravlr.soundfilters.SoundFiltersMod.BlockMeta;
import com.tmtravlr.soundfilters.filters.FilterLowPass;
import com.tmtravlr.soundfilters.filters.FilterReverb;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class SoundFiltersConfig {

	public static boolean DEBUG = false;
	public static boolean SUPER_DUPER_DEBUG = false;
	
	public static Logger logger;
	
	static Configuration config;
	
	public static FilterLowPass lowPassFilter = new FilterLowPass();
	public static FilterReverb reverbFilter = new FilterReverb();

	public static int profileSize = 1024;
	public static boolean doSkyChecks = true;
	public static boolean doReverb = true;
	public static float reverbPercent = 1.0f;
	public static boolean doLowPass = true;
	public static float waterLowPassAmount = 0.4f;
	public static float waterVolume = 1.0f;
	public static float lavaLowPassAmount = 0.1f;
	public static float lavaVolume = 0.6f;
	public static boolean doOcclusion = true;
	public static float occlusionPercent = 1.0f;

	public static final int ALL_METAS = -1;
	private static final int LEGACY_ALL_METAS = 16;
	private static final String ALL_METAS_STR = "*";

	public static Map<BlockMeta, Double> customOcclusion = new HashMap<BlockMeta, Double>();
	public static Map<BlockMeta, Double> customReverb = new HashMap<BlockMeta, Double>();

    public static void configure() {
    	if (!config.hasChanged()) {
    		config.load();
    	}

		DEBUG = config.getBoolean("Debug", "debug", false, "Set to true to write simple debug info to the console.");
		SUPER_DUPER_DEBUG = config.getBoolean("High Output Debug", "debug", false,
				"You probably don't want to set this to true\n"
				+ "unless you actually want to debug the mod.\n"
				+ "It writes quite a lot in the console.");

		doLowPass = config.getBoolean("Use Low Pass?", "low_pass", true, "Set to false to disable low pass filter in water and lava.");
		waterLowPassAmount = config.getFloat("Water Low Pass Amount", "low_pass", 0.4f, 0.0f, 1.0f,
				"The amount of low pass that will be applied in water. Lower is stronger.");
		lavaLowPassAmount = config.getFloat("Water Low Pass Volume", "low_pass", 1.0f, 0.0f, 1.0f,
				"The multiplier for volume when you are in water. Lower is quieter.");
		lavaLowPassAmount = config.getFloat("Lava Low Pass Amount", "low_pass", 0.2f, 0.0f, 1.0f,
				"The amount of low pass that will be applied in lava. Lower is stronger.");
		lavaLowPassAmount = config.getFloat("Lava Low Pass Volume", "low_pass", 0.6f, 0.0f, 1.0f,
				"The multiplier for volume when you are in lava. Lower is quieter.");

		doOcclusion = config.getBoolean("Use Occluded Sounds (muting sounds behind solid walls)?", "occlusion", true,
				"Set to false to disable low pass filter for sounds behind solid walls.\n"
				+ "If you are getting lag, disabling this might help.");
		occlusionPercent = config.getFloat("Occlusion Percent", "occlusion", 1.0f, 0.0f, Float.POSITIVE_INFINITY,
				"The percentage of occlusion you can get. You can lower this if you find\n"
				+ "the occlusion to be too much or raise it for a more noticeable\n"
				+ "effect.");
		String[] occlusionBlocksList = config.getStringList("Specific block occlusion:", "occlusion", 
			new String[] { "wool-" + ALL_METAS_STR + "-2.0", "securitycraft:reinforced_wool-" + ALL_METAS_STR + "-1.5" },
				"Add new entries (each on a new line) in the format\n"
				+ "<block id>-<metadata>-<occlusion double> to customize how much sound\n"
				+ "they should absorb when they are between you and the sound source.\n"
				+ "For the metadata, " + ALL_METAS_STR + " means any metadata value. The amount is a\n"
				+ "double, with 0.0 absorbing no sound (like air), and 1.0 being the normal\n"
				+ "amount, and 2.0 being twice the normal amount. By default, wool has\n"
				+ "entry wool-" + ALL_METAS_STR + "-2.0 which is twice the normal sound absorbtion.");

		doReverb = config.getBoolean("Use Reverb?", "reverb", true, "Set to false to disable reverb.");
		reverbPercent = config.getFloat("Reverb Percent", "reverb", 1.0f, 0.0f, 2.0f,
				"The percentage of reverb you can get. You can lower this if you find\n"
				+ "the reverb to be too much (or raise it if you really want an\n"
				+ "echo).");
		profileSize = config.getInt("Number of blocks reverb will check through:", "reverb", 1024, 0, Integer.MAX_VALUE,
				"If you are getting lag, set this number lower. The higher it is,\n"
				+ "the more realistic the reverb will be.");
		doSkyChecks = config.getBoolean("Do sky checks:", "reverb", true,
				"If this is true, when you're in an area that can see the sky, then\n"
				+ "there will be less reverb. This is for aboveground areas with\n"
				+ "lots of stone and such like extreme hills biomes. There still might\n"
				+ "be some, but less then when the sky isn't visible.");
		String[] reverbBlocksList = config.getStringList("Specific block reverb:", "reverb", new String[] { "soul_sand-" + ALL_METAS_STR + "-2.0" },
				"Add values to this list (each on a new line) in the format \n"
				+ "<block id>-<metadata>-<reverb double>, to change how the block\n"
				+ "with that metadata absorbs or creates reverb. If the\n"
				+ "metadata is " + ALL_METAS_STR + ", that means it will apply to any metadata value.\n"
				+ "By default things like wool, snow, carpets, and plants absorb reverb\n"
				+ "(value 0.0), things like wood and dirt are neutral (value 1.0),\n"
				+ "and things like stone, metal, ice, and glass create reverb (value 2.0).\n"
				+ "So if, say, you wanted to add pumpkins of any metadata to the blocks\n"
				+ "that create reverb, you would put pumpkin-" + ALL_METAS_STR + "-2.0 on a new line.");

		int convert = config.getInt("Convert legacy Meta 16", "debug", 1, 0, 2,
			"convert the legacy meta " + LEGACY_ALL_METAS + " values to " + ALL_METAS_STR + "\n"
			+ "0: do not convert\n"
			+ "1, 2: convert\n"
			+ "1: after convert: set this value to 0 (do one initial convert)\n"
			+ "2: do not change this value (I don't know why you could want to use this value)");

		occlusionBlocksList = parseCustomBlockList(occlusionBlocksList, customOcclusion, "occlusion", convert > 0);
		reverbBlocksList = parseCustomBlockList(reverbBlocksList, customReverb, "reverb", convert > 0);

		if (convert == 1 && !legacyAnyMeta()) {//only set to zero after convert
			ConfigCategory cat = config.getCategory("debug");
			Property prop = cat.get("Convert legacy Meta 16");
			prop.set(0);
		}
		if (occlusionBlocksList != null) {
			ConfigCategory cat = config.getCategory("occlusion");
			Property prop = cat.get("Specific block occlusion:");
			prop.set(occlusionBlocksList);
		}
		if (reverbBlocksList != null) {
			ConfigCategory cat = config.getCategory("reverb");
			Property prop = cat.get("Specific block reverb:");
			prop.set(reverbBlocksList);
		}

		if (DEBUG) {
			logger.debug("customOcclusion: " + customOcclusion);
			logger.debug("customReverb: " + customReverb);
		}

		if (config.hasChanged()) {
			config.save();
		}
	}

	private static String[] parseCustomBlockList(String[] blocksList, Map<BlockMeta,Double> customMap, String name, boolean convert) {
		boolean legacyAnyMeta = legacyAnyMeta();
		boolean replace = false;
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
				if (legacyAnyMeta) {
					meta = Integer.parseInt(metaStr);
					if (meta == LEGACY_ALL_METAS) {
						meta = ALL_METAS;
					} else if (meta < 0) {
						throw new NumberFormatException("only non-negative values are permitted");
					}
				} else if (customInfo.startsWith("*-", secondLastDashIndex + 1)) {
					meta = ALL_METAS;
				} else {
					meta = Integer.parseInt(metaStr);
					if (meta < 0) {
						throw new NumberFormatException("only non-negative values are permitted");
					}
					if (convert && meta == LEGACY_ALL_METAS) {
						meta = LEGACY_ALL_METAS;
						replace = true;
						blocksList[i] = customInfo.substring(0, secondLastDashIndex) + "-*" + customInfo.substring(lastDashIndex);
					}
				}
				strength = Double.parseDouble(customInfo.substring(lastDashIndex + 1));
				block = Block.getBlockFromName(blockName);
			} catch (Exception e) {
				logger.error("Error while loading in custom " + name + " entry: '" + customInfo + "'", e);
			}
			//meta value can only be negative in two cases:
			// 1: star value used
			// 2: failure during assignments
			// in the first case this is ok, in the second case strength/block is also still invalid
			if (block != null && strength >= 0) {
				if (DEBUG) {
					logger.debug("Loaded custom " + name + ": block " + blockName + ", with "
						+ (meta == ALL_METAS ? "any meta" : "meta " + meta) + ", and amount " + strength);
				}
				customMap.put(new BlockMeta(block, meta), strength);
			}
		}
		if (replace) {
			return blocksList;
		}
		return null;
	}

	private static boolean legacyAnyMeta() {
		return config.getBoolean("Legacy Meta 16 Value", "debug", false,
		"if set to true the special meta value * will be invalid and\n"
		+ "the meta value " + LEGACY_ALL_METAS + " will instead be converted to any meta\n"
		+ "if set to true the values will not be converted to the new meta format\n"
		+ "  ('Convert legacy Meta 16' will be ignored)");
	}

}
