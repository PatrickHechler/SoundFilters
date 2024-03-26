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
+ graphical configuration page
    + this allows to configure the mod without the need to restart the client
