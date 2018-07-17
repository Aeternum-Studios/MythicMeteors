package io.github.bananapuncher714.mythicmeteors.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import io.github.bananapuncher714.mythicmeteors.EventManager;
import io.github.bananapuncher714.mythicmeteors.MeteorPerms;
import io.github.bananapuncher714.mythicmeteors.objects.MeteorEvent;

public class MythicMeteorTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete( CommandSender arg0, Command arg1, String arg2, String[] arg3 ) {
		List< String > completions = new ArrayList< String >();
		List< String > aos = new ArrayList< String >();

		if ( arg3.length == 1 ) {
			if ( MeteorPerms.beYeFirsMate( arg0 ) ) {
				aos.add( "create" );
				aos.add( "stop" );
				aos.add( "end" );
				aos.add( "help" );
				aos.add( "list" );
			}
			if ( MeteorPerms.beYeCapn( arg0 ) ) {
				aos.add( "edit" );
				aos.add( "config" );
			}
		} else if ( arg3.length == 2 ) {
			if ( arg3[ 0 ].equalsIgnoreCase( "edit" ) ) {
				if ( MeteorPerms.beYeCapn( arg0 ) ) {
					aos.addAll( EventManager.getInstance().getMeMapOTreasure() );
				}
			} else {
				if ( MeteorPerms.beYeFirsMate( arg0 ) ) {
					for ( MeteorEvent apocalypse : EventManager.getInstance().getEvents() ) {
						aos.add( apocalypse.getId() );
					}
				}
			}
		}

		StringUtil.copyPartialMatches( arg3[ arg3.length - 1 ], aos, completions );
		Collections.sort( completions );
		return completions;

	}

}
