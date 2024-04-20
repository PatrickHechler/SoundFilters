# SoundFilters
Sound Filters is a small minecraft mod that adds reverb to caves and mutes sounds when you're underwater or when the sound source is behind a wall.

If you want to contribute to Sound Filters (for instance update it to a new version of MC, or fix a bug), send up a PR and I'll review it as soon as I can. =)

For a list of modifications compare the [original](https://github.com/Tmtravlr/SoundFilters/tree/1.12) and the [forked](https://github.com/PatrickHechler/SoundFilters) (this) repositories    
Currently this fork adds two things:
+ improvements of the custom block occlusion/reverb configs
    + the special meta value 16 is replaced with the star (*) character
        + by default the old format is converted on the first start
    + the configuration file is now loaded during the post-init stage
        + this makes it possible to reference blocks registered by other mods (usually blocks are registered in the init (or even pre-init) stage)
    + the search order is swapped, now the mod searches first for the specific meta value and then for the generic one
        + this allows to easily catch almost all blocks with the same block id
+ graphical configuration page
    + this allows to configure the mod without the need to restart the client

## back-port: 0.13
+ todo:
+ done:
    - Added sponge and wet sponge to the occlusion block list with high occlusion like wool
    - Added dimension reverb list, to set default reverb per-dimension (by default the nether has high reverb)
        + I don't know how the value is used, but it seems to work
        + TODO (someone that knows how sound works) : look at this
+ done differently:
+ skipped:
    - Added a lot more control over the reverb filter in the config file, in an advanced section. Play around with it, and see how it sounds!
    	+ I don't know how sound stuff works
    - Added block states and nbt to the block reverb/occlusion lists, as well as block tags
        + the class BlockPredicateArgument does not exist in 1.12 (I might look at it later)
    - Improved reverb checks to see if you are on the surface (and should have little to no reverb)
        + the onlySkyAboveBlock check and its uses did not change, what was imporved?
    - Improved underwater reverb
        + how
    - Improved sound occlusion to check how thick the block is
        + how do you know how thick a block is (parse graphic?)
