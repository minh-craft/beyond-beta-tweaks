## v0.41.0

### Added

- Port Better Recipe Book changes from fork https://github.com/minh-craft/BetterRecipeBook
  - Remove lingering potion tab
  - Disable recipe book scrolling when hovering over bundle (contributed by [@Leclowndu93150](https://github.com/Leclowndu93150))

---

## v0.40.0

### Added

- Port Better Days adjustments from fork https://github.com/minh-craft/BetterDays/pull/1
  - Changed earliest allowed sleep time to just before midnight
  - Changed wake up time to early sunrise

---

## v0.39.0

### Added

- Modified climbing speed in https://modrinth.com/mod/better-climbing/ mod

---

## v0.38.0

### Added

- Shovels now convert grass/dirt into coarse dirt instead of paths
- Add config option to adjust end gateway teleport distance
- Add config option to adjust boat water friction

### Changed

- Re-added skeleton horse trap
  - Now with skeletons no longer spawning with enchanted gear

---

## v0.37.0

### Added

- Disable randomized attributes for horses and donkeys
  - Set horse and donkey health to 10 hearts
  - Set jump strength to 0.5 for donkeys and 0.6 for horses
  - Add config option to set horse and donkey movement speed

---

## v0.36.0

### Added

- Added new rotten leather texture, created by Glorious Emperor Creeper https://www.youtube.com/@Emperor_Creeper

---

## v0.35.0

### contributed by [@mahmudindev](https://github.com/mahmudindev)

### Added

- Added per-dimension monster mob cap configuration options

---

## v0.34.0

### Added

- Silverfish no longer infest into stone blocks

---

## v0.33.1

### Changed

- Move credits directly into README

---

## v0.33.0

### Added

- Disable shulker duplication when shot with a bullet to disable shulker farms
  - Code taken from https://github.com/pajicadvance/misctweaks by @pajicadvance 

### Changed

- Update credits page format 
- Embed credits directly into README

---

## v0.32.0

### Added

- Added giant ai and attributes based on zombie ai and attributes
- Allow giants to spawn using normal biome mob placements
  - Spawn conditions: new moon, and on the surface (not underground)

### Removed

- Remove https://github.com/Serilum/Giant-Spawn compat

---

## v0.31.0

### contributed by [@Leclowndu93150](https://github.com/Leclowndu93150)

### Added

- Make ravines only spawn as waterlogged in areas where they are directly below water
- Update recipe book icons
- Updated recipe book to only scroll through craftable items in within a recipe group if at least one item is craftable

---

## v0.30.0

### Added

- Added a config value for configuring the mob cap for ambient water mob spawns

---

## v0.29.0

### Added

- Bonemeal no longer turns short grass into tall grass

---

## v0.28.1

### Changed

- Removed iceberg config options, hardcode disabling giant icebergs in frozen oceans and larger height scaling variables

---

## v0.28.0

### Added

- Added config option to disable giant icebergs from generating in the frozen ocean biome (not deep frozen ocean)
- Added config options to adjust giant iceberg scaling variables which affect iceberg height

---

## v0.27.0

### Added

- Disable endermite spawning from thrown enderpearl
- Added config option to adjust enderpearl teleport damage
- Disable eye of ender destruction after use

---

## v0.26.0

### Added

- Added config option to cap maximum boat slipperiness to allow nerfing ice boats

---

## v0.25.1

### Changed

- Fixed fox item override not working
- Adjusted fox items to be only feathers, eggs, and leather

---

## v0.25.0

### Added

- Allow enchantments to be retained when repairing in the crafting grid
- Disable equipment drops for wither skeletons, piglins, piglin brutes, zombified piglins
  - Code from https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks by @Adrenix 
- Disable charged creeper explosion dropping mob heads
- Disable dripstone creating lava in cauldrons

---

## v0.24.0

### Added

- Foxes:
  - Disable baby foxes from spawning
  - Added ability to set foxes to trusting by feeding them
  - Override fox spawn items - 25% each of wheat, egg, leather, feather
- Goats:
  - Reduce ramming damage to 1/2 heart
  - Add config options for adjusting wait time between rams

---

## v0.23.0

### Added

- Added eating sound to cake when overeating too
- Added config option to set chance of screaming goat spawn

---

## v0.22.0

### Added

- Added new paintings by Kristoffer Zetterstrand, adapted by https://www.youtube.com/@Mongster83

---

## v0.21.0

### Added

- Added light level of 15 to Amethyst Block

---

## v0.20.1

### Changed

- Removed unnecessary access widener

---

## v0.20.0

### Added

- Disable enchantment glint on enchanted armor
- Add configuration options for adjusting crossbow charge speed and shot power and base damage
- Disable crit arrow
- Add Archery Expansion's arrow GUI to crossbow loading
    - Also add option to disable arrow GUI entirely


---

## v0.19.1

### Added

- Lang entries for new config values

---

## v0.19.0

### Added

- Added customization options for adjusting
    - Nostalgic Tweak's round robin lighting maximum deducted sky light level
    - True Darkness's minimum true darkness level
    - True Darkness's maximum true darkness level

---

## v0.18.0

### Added

- Added customization options for adjusting the brightness of the End and the night vision effect

---

## v0.17.0

### Added

- Added ability to fully customize nighttime brightness by moon phase in True Darkness Refabricated mod

---

## v0.16.0

### Changed

- Giants now despawn in daylight

---

## v0.15.0

### Changed

- baby hoglins can no longer spawn

---

## v0.14.0

### Contributed by [@Partonetrain](https://github.com/Partonetrain/)

### Changed

- updated ender dragon end gateway spawning code to create 4 gateways at once, in the north, west, south, and eastmost
  corners of the end island.

---

## v0.13.0

### Changed

- updated ocelot to cat conversion code to updated code from https://github.com/Partonetrain/trains_tweaks by Partonetrain

---

## v0.12.0

### Added

- add code from github.com/Partonetrain/ocelotfix to tame ocelot into cat
    - fix possible multiple cats bug
- add config options to set ocelot avoid distance and disable ocelot avoiding
    - change default ocelot avoid distance to 8 blocks instead of 16

---

## v0.11.0

### Added

- make it easier to tame ocelots
    - don't need to be in a tempted state to be tamed
    - 1/2 chance instead of 1/3

---

## v0.10.0

### Added

- remove end crystal and iron bars from all end spikes

---

## v0.9.0

### Added

- disable anvil damage

---

## v0.8.0
### Added

- mixin to https://modrinth.com/mod/giant-spawn mod
    - allow modifying giant attack range
- increase range of giant sounds

---

## v0.7.0
### Added

- update husk loot table to match zombie's
- add text for config title screen

---

## v0.6.0

### Added

- add configuration for recipe custom sorting
- add configuration for other mod properties

---

## v0.5.0

### Added

- decrease pitch of giant sounds
- add loot table for giant, drops 6-9 rotten flesh

---

## v0.4.0

### Added

- add rotten flesh to rotten leather to leather conversion process
- change zombie loot table to only drop 0-1 rotten flesh

---

## v0.3.0

### Added

- add sound for eating cake normally
- giants
   - add low pitched zombie sounds
   - set health to 45

---

## v0.2.0

### Added

- make bundles no longer stack inside shulker boxes and supplementaries sacks

---

## v0.1.0

### Added

- zombies
    - disable drowned conversion
    - make zombies, husks float again, rather than sinking
    - make husks burn in daylight
    - increase attack range
- spiders
    - increase attack range
- skeletons
    - make skeletons float
    - disable trap horse spawning during thunderstorms
    - make skeleton arrow velocity slower, matching instant beta bow speed
- enchant
    - make swift sneak apply to all armor slots
    - make swift sneak bonus cumulative across all equipped armor pieces
- cauldron
    - disable powder snow generation in snowy weather
- cake
    - add overeat for extra absorption hearts feature
- swords
    - increase attack range by 0.5 blocks
- mud
    - make mud blocks have full block collision
