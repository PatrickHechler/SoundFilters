package com.tmtravlr.soundfilters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGUIFactory implements IModGuiFactory {
	
	@Override
	public void initialize(Minecraft minecraftInstance) {
	}
	
	@Override
	public boolean hasConfigGui() {
		return true;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		List<IConfigElement> confs = new ArrayList<>();
		Configuration conf = SoundFiltersConfig.config();
		for (String name : conf.getCategoryNames()) {
			confs.add(new ConfigElement(conf.getCategory(name)));
		}
		return new GuiConfig(parentScreen, confs, SoundFiltersMod.MOD_ID, false, false, SoundFiltersMod.MOD_NAME);
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}
	
}
