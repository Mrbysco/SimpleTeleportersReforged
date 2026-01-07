---
navigation:
  title: Ender Core
  icon: simpleteleporters:ender_shard
  parent: index.md
  position: 2
item_ids:
  - simpleteleporters:ender_shard
---

# Ender Core

<Row>
<Column>

The Ender Core is the key component for linking <ItemLink id="simpleteleporters:teleporter" /> blocks together.

<ItemImage id="simpleteleporters:ender_shard" scale="4" />

</Column>
<Column>

## Recipe

<Recipe id="simpleteleporters:ender_shard" />

</Column>
</Row>

## How to Use

### Linking to a Location

1. Hold the Ender Core in your hand
2. **Sneak** and **right-click** on a block to bind the core to that location
3. The core will remember the exact coordinates and dimension

### Tooltip Information

- **Unlinked cores** display "Unlinked" in red with instructions on how to link
- **Linked cores** show the exact X, Y, Z coordinates and dimension they're bound to

## Features

- **Stackable**: Up to 16 Ender Cores can stack together (unlinked only - linked cores with different coordinates won't stack)
- **Smart Positioning**: When linking:
  - Clicking on a non-solid block binds to that exact position
  - Clicking on a Teleporter binds to one block above it (so you land on top)
  - Otherwise, binds to the clicked face of the block

## Upgrading

Ender Cores can only teleport within the same dimension. To enable **cross-dimensional teleportation**, upgrade your Ender Core to an <ItemLink id="simpleteleporters:enhanced_ender_shard" /> at a Smithing Table using an Echo Shard.

The linked position is preserved during the upgrade!

## Tips

- Keep spare unlinked Ender Cores for quickly setting up new teleport destinations
- The coordinates shown in the tooltip help you identify where each core leads
- To change a core's destination, simply sneak + right-click on a new location
