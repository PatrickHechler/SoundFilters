package com.tmtravlr.soundfilters;

import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class handles the config events, specifically when the client modifies the config-file.
 * 
 * @author Patrick
 * @Date 2024
 */
public class ConfigEventHandler {

	@SubscribeEvent
	public void configChangedEventHandler(OnConfigChangedEvent event) {
        if (SoundFiltersMod.MOD_ID.equals(event.getModID())) {
            SoundFiltersMod.configure();
            if (SoundFiltersMod.DEBUG) {
            	SoundFiltersMod.logger.debug("Sound-Filter config changed");
            }
        }
	}
	
}
