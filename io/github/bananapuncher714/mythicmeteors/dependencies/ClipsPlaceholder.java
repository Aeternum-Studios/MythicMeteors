package io.github.bananapuncher714.mythicmeteors.dependencies;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class ClipsPlaceholder {
	public static String parse( Player player, String input ) {
		String result = PlaceholderAPI.setPlaceholders( player, input );
		return result;
	}
}
