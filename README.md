# Pure Origins

An in-game way to change [Origins](https://github.com/apace100/origins-fabric)!

## Building

Simple clone the repo and run `gradlew remapJar` and the jar will be available in `build/libs/pure_origins-x.y.z.jar`
where x.y.z is the current version.

## Lore

Origins came from old, pure vessels whose shrines of worship can still be found scattered around the
world.

It is said that one could switch origins by cleansing oneself of impurities and using an Obelisk
and the power of the Moon...

## Features / TODO

I highly recommend using REI until better in-game docs are added.

- [ ] Pure crystals of each of the stock origins
    - [X] Can be created by sacrificing your origin to a Hollow Crystal in an Obelisk
    - [X] Switches your origin
    - [ ] Found in ruins of old
- [X] Hollow Crystal
    - [X] Can be embued with the origin of the player, at the cost of stripping the player into a human
- [X] Void Crystal
    - [X] Crafted with a diamond and fruits of purity
    - [X] Only serves to convert a player into a human
- [X] Obelisk
    - [X] Holds up to four pure crystals
    - [X] Requires direct view of the sky, a max of 5 blocks, and only works at night
    - [X] Can mix Pure Stone and Obelisk blocks
    - [X] Strikes users with lightning when switching origins
    - [X] Requires the user to be Pure and right click with an empty hand on a pure crystal
- [ ] Fruit of Purity
    - [X] Grows only at night with a direct view of the sky
    - [X] Gives the player a Purity status effect
    - [X] Status effect removes all harmful effects
    - [ ] Spawns at ruins
- [X] Pure Stone
    - [X] Crafted by placing any burnable log into a Pure Campfire
    - [X] Glows at night
    - [X] Can be interspersed among Obelisk blocks
- [X] Pure Campfire
    - [X] Crafted using the fruit of purity
    - [X] Can change burnable logs into Pure Stone
    - [X] Only burns at night

## Licenses

All of the image resources are based on the [Unity Resource Pack](https://github.com/Unity-Resource-Pack/Unity)
and are thus licensed under the [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International Public License](https://creativecommons.org/licenses/by-nc-sa/4.0/)

The license for the codebase can be found in LICENSE.