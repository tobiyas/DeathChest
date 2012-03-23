package de.tobiyas.deathchest.util;

import java.util.Map;

import org.bukkit.configuration.MemorySection;

public class MemorySelectionEnhanced extends MemorySection{

	public Map<String, Object> getChildren(){
		return this.map;
	}
}
