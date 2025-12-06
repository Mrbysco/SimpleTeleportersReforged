package com.mrbysco.simpleteleporters.datagen.data;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class SimpleRecipeProvider extends RecipeProvider {
	public SimpleRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, SimpleTeleportersItems.ENDER_SHARD.get())
				.requires(SimpleTeleportersItems.ENDER_SHARD.get())
				.unlockedBy("has_ender_shard", has(SimpleTeleportersItems.ENDER_SHARD.get()))
				.save(recipeOutput, SimpleTeleporters.id("clear_shard"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, SimpleTeleportersBlocks.TELEPORTER.get())
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
				.save(recipeOutput);

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.ENDER_PEARL), RecipeCategory.TRANSPORTATION,
						SimpleTeleportersItems.ENDER_SHARD.get(), 0.7F, 200)
				.unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
				.save(recipeOutput, SimpleTeleporters.id("ender_shard"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SimpleTeleportersItems.ENDER_SHARD), RecipeCategory.TRANSPORTATION,
						SimpleTeleportersItems.HEARTH_CRYSTAL.get(), 0.7F, 200)
				.unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
				.save(recipeOutput, SimpleTeleporters.id("hearth_crystal"));

		// Enhanced Ender Shard - smithing upgrade with Echo Shard
		SmithingTransformRecipeBuilder.smithing(
						Ingredient.EMPTY, // No template required
						Ingredient.of(SimpleTeleportersItems.ENDER_SHARD.get()),
						Ingredient.of(Items.ECHO_SHARD),
						RecipeCategory.TRANSPORTATION,
						SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get())
				.unlocks("has_ender_shard", has(SimpleTeleportersItems.ENDER_SHARD.get()))
				.unlocks("has_echo_shard", has(Items.ECHO_SHARD))
				.save(recipeOutput, SimpleTeleporters.id("enhanced_ender_shard_smithing"));

		// Clear recipe for enhanced shard
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get())
				.requires(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get())
				.unlockedBy("has_enhanced_ender_shard", has(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get()))
				.save(recipeOutput, SimpleTeleporters.id("clear_enhanced_shard"));
	}
}