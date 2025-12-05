---
navigation:
  title: Teleporter
  icon: simpleteleporters:teleporter
  parent: index.md
  position: 1
item_ids:
  - simpleteleporters:teleporter
---

# Teleporter

<Row>
<Column>

The Teleporter is a block that allows instant teleportation between linked locations.

<ItemImage id="simpleteleporters:teleporter" scale="4" />

</Column>
<Column>

## Recipe

<RecipeFor id="simpleteleporters:teleporter" />

</Column>
</Row>

## How to Use

### Setting Up a Teleporter

1. Place a Teleporter block at your destination
2. Bind an <ItemLink id="simpleteleporters:ender_shard" /> to a location above the destination teleporter by **sneaking** and **right-clicking** on it
3. Place another Teleporter block at your starting location
4. **Right-click** on the starting Teleporter with the linked Ender Shard to insert it

### Teleporting

Stand on an active Teleporter (one with portal particles) and **sneak** to teleport to the linked destination.

## Features

- **Portal Particles**: Active Teleporters display portal particles when they have a linked Ender Shard
- **Cooldown**: A brief cooldown prevents instant re-teleportation
- **Waterloggable**: Can be placed underwater
- **Dimension Support**: Works within the same dimension only

## Tips

- For bidirectional travel, set up two Teleporters with Ender Shards pointing at each other
- The teleporter ejects the Ender Shard when you right-click it, allowing you to reconfigure your network
- If the destination is blocked, you'll receive an error message
