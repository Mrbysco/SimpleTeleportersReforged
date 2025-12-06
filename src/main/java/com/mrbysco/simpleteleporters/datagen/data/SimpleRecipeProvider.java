package com.mrbysco.simpleteleporters.datagen.data;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class SimpleRecipeProvider extends RecipeProvider {
	public SimpleRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		super(provider, recipeOutput);
	}

	@Override
	protected void buildRecipes() {
		// Clear shard recipe
		shapeless(RecipeCategory.TRANSPORTATION, SimpleTeleportersItems.ENDER_SHARD.get())
				.requires(SimpleTeleportersItems.ENDER_SHARD.get())
				.unlockedBy("has_ender_shard", has(SimpleTeleportersItems.ENDER_SHARD.get()))
				.save(output, SimpleTeleporters.id("clear_shard").toString());

		// Teleporter recipe
		shaped(RecipeCategory.TRANSPORTATION, SimpleTeleportersBlocks.TELEPORTER.get())
				.pattern(" C ")
				.pattern("GNG")
				.pattern("QQQ")
				.define('C', SimpleTeleportersItems.ENDER_SHARD.get())
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('Q', Blocks.QUARTZ_BLOCK)
				.define('N', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_ender_shard", has(SimpleTeleportersItems.ENDER_SHARD.get()))
				.unlockedBy("has_gold_block", has(Tags.Items.STORAGE_BLOCKS_GOLD))
				.unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
				.save(output);

		// Ender shard from ender pearl
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.ENDER_PEARL), RecipeCategory.TRANSPORTATION,
						SimpleTeleportersItems.ENDER_SHARD.get(), 0.7F, 200)
				.unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
				.save(output);

		// Hearth crystal from ender shard
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SimpleTeleportersItems.ENDER_SHARD.get()), RecipeCategory.TRANSPORTATION,
						SimpleTeleportersItems.HEARTH_CRYSTAL.get(), 0.7F, 200)
				.unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
				.save(output);

		// Enhanced Ender Shard - crafted with Echo Shard
		shapeless(RecipeCategory.TRANSPORTATION, SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get())
				.requires(SimpleTeleportersItems.ENDER_SHARD.get())
				.requires(Items.ECHO_SHARD)
				.unlockedBy("has_ender_shard", has(SimpleTeleportersItems.ENDER_SHARD.get()))
				.unlockedBy("has_echo_shard", has(Items.ECHO_SHARD))
				.save(output);

		// Clear recipe for enhanced shard
		shapeless(RecipeCategory.TRANSPORTATION, SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get())
				.requires(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get())
				.unlockedBy("has_enhanced_ender_shard", has(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get()))
				.save(output, SimpleTeleporters.id("clear_enhanced_shard").toString());

		// Recharge hearth crystal with ender pearl
		shapeless(RecipeCategory.TRANSPORTATION, SimpleTeleportersItems.HEARTH_CRYSTAL.get())
				.requires(SimpleTeleportersItems.HEARTH_CRYSTAL.get())
				.requires(Items.ENDER_PEARL)
				.unlockedBy("has_hearth_crystal", has(SimpleTeleportersItems.HEARTH_CRYSTAL.get()))
				.save(output, SimpleTeleporters.id("recharge_hearth_crystal").toString());
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			return new SimpleRecipeProvider(provider, recipeOutput);
		}

		@Override
		public String getName() {
			return "Simple Teleporters Reforged Recipes";
		}
	}
}
