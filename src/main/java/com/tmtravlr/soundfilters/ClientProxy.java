package com.tmtravlr.soundfilters;

import net.minecraftforge.common.MinecraftForge;
/**
 * 
 * @author Rebeca Rey
 * @author Patrick Hechler
 * @Date 2014
 */
public class ClientProxy extends CommonProxy
{
    @Override
	public void registerTickHandlers()
    {
    	MinecraftForge.EVENT_BUS.register(new SoundTickHandler());
    }

    @Override
	public void registerEventHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new SoundEventHandler());
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }
}
