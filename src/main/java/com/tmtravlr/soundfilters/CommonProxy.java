package com.tmtravlr.soundfilters;

import net.minecraftforge.common.MinecraftForge;

/**
 * 
 * @author Rebeca Rey
 * @Date 2014
 */
public class CommonProxy
{
    public void registerTickHandlers() {}

    public void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }
}
