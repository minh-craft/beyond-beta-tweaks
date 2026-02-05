## Description

Various tweaks for the Beyond Beta modpack.
See [changelog](changelog.md) for full list of features.

### Dev note:
When running the mod in the IDE, time doesn't pass for some reason. This isn't a problem when the mod is compiled. I've narrowed this down to including the owo lib dependency in build.gradle, needed for True Darkness compatibility. I don't really know why though or how to fix it. 

## Credits
### Contributions
- Contributed by [@Partonetrain](https://github.com/Partonetrain/)
    - Updated ender dragon end gateway spawning code to create 4 gateways at once, in the north, west, south, and eastmost corners of the end island.
- Contributed by [@Leclowndu93150](https://github.com/Leclowndu93150)
    - Make ravines only spawn as waterlogged in areas where they are directly below water
    - Update recipe book icons
    - Updated recipe book to only scroll through craftable items in within a recipe group if at least one item is craftable
    - Scholar mod now always displays the editing toolbar when writing books, not just when text is selected.
    - Scaffolding-like placement for ladders. Can now place ladders at a configurable distance away from the base ladder. 
    - Can now shift fall through Supplementaries rope if there is a rope underneath
    - Giants now spawn smoke particles when despawning in the sunlight
    - Cartography table now accepts glass blocks in place of glass panes for locking maps
- Contributed by [@mahmudindev](https://github.com/mahmudindev)
  - Added per-dimension monster mob cap configuration options


### Assets
- Rotten leather texture created by @Glorious Emperor Creeper https://www.youtube.com/@Emperor_Creeper
- Kristoffer Zetterstrand paintings https://zetterstrand.com/works/ adapted by https://www.youtube.com/@Mongster83
    - https://www.reddit.com/r/GoldenAgeMinecraft/comments/1i5fnh0/i_made_a_bunch_of_new_paintings_based_off/

### Code
- Ocelot taming into cat code from https://github.com/Partonetrain/trains_tweaks by [@Partonetrain](https://github.com/Partonetrain/)
- Mob equipment drop removal code from https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks by [@Adrenix](https://github.com/Adrenix/)
- Ambient water mob cap code adapted from https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks by [@Adrenix](https://github.com/Adrenix/)
- Boat slipperiness cap code adapted from https://modrinth.com/mod/ice-boat-nerf by [@supersaiyansubtlety](https://gitlab.com/supersaiyansubtlety)
- Shulker farm disable code from https://github.com/pajicadvance/misctweaks by [@pajicadvance](https://github.com/pajicadvance)
- World preset disable code from https://github.com/ChaoticTrials/DefaultWorldType by [@ChaoticTrials](https://github.com/ChaoticTrials)
- Beta cave code from https://codeberg.org/Nostalgica-Reverie/moderner-beta by [@icanttellyou](https://github.com/forkiesassds) and [@b3spectacled](https://github.com/b3spectacled) and [@BlueStaggo](https://github.com/BlueStaggo)
- Rabbits don't take fall damage tweak from https://modrinth.com/datapack/rabbits-dont-take-fall-damage by @dragon3025
