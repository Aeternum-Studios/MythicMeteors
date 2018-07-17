# Zombie Apocalypse
A reformed plugin by BananaPuncher714 - Much therapy
### What is this?
Zombie Apocalypse is a recoded plugin based off of [this](https://dev.bukkit.org/projects/zombieapocalypse) plugin.
It provides more customization and is easy to integrate with other people. (It also has tab completes).

### Permissions:
```
zombieapocalypse.admin - Allows players to run every command
zombieapocalypse.manage - Allows players to start/stop apocalypses
```

### Commands:
- **/zombieapocalypse**
  - **start \[id\]** - Starts the given apocalypse provided, starts all nearby if no arguments are provided
  - **stop \[id\]** - Stops the given apocalypse naturally, stops all nearby if no arguments are provided
  - **end <id>** - Stops the apocalypse given forcefully, without giving out rewards or notifying players
  - **panel** - Opens the Zombie Apocalypse control panel
  - **saveexamples** - Saves config examples that can be configured for custom content
  - **open** <reward set id> - Opens the editor to create your own reward sets
  - **help** - Shows the help
  
### Integration with other plugins:
Zombie Apocalypse provides a PlaceholderAPI expansion. It also provides a cursor shader for Cartographer.

#### The placeholders:
`%zombieapocalypse_<apocalypse-id>_running%` - Returns true or false.  
`%zombieapocalypse_<apocalypse-id>_kill_percent_required%` - The percentage of mobs required to be killed. Returned as a decimal.  
`%zombieapocalypse_<apocalypse-id>_percent_killed%` - The percentage of mobs that have been already killed.  Returned as a decimal.  
`%zombieapocalypse_<apocalypse-id>_amount_cleared%` - The amount of mobs already killed.  
`%zombieapocalypse_<apocalypse-id>_amount_total%` - The amount of mobs in total, including the ones killed.  

## Configuring the files
Each folder(spawns, rewards, apocalypses) contains info for spawning, rewards, and apocalypses respectively. The main config also contains a list of standard rewards. The name of each list and the names of the files in the rewards file make up the ids of RewardSets. Note that the .yml extension for the files is not included in their ids. The same goes for spawns and apocalypses.

So for a reward, spawn, or apocalypse file, if the name is `example-rewards.yml`, then the id is `example-rewards`. The files should not contain any spaces in their name.

### Messages
You can find the messages inside the config. They all accept placeholder values and use ampersands as the color character.
