package com.tmtravlr.soundfilters;

import net.minecraftforge.common.MinecraftForge;
/**
 * 
 * @author Rebeca Rey
 * @Date 2014
 */
public class ClientProxy extends CommonProxy
{
    @Override
	public void registerTickHandlers()
    {
    	MinecraftForge.EVENT_BUS.register(new SoundTickHandler());
    	super.registerTickHandlers();
    }

    @Override
	public void registerEventHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new SoundEventHandler());
        super.registerEventHandlers();
    }
}
