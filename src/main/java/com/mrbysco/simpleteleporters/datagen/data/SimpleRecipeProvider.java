package com.mrbysco.simpleteleporters.datagen.data;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;


public class SimpleRecipeProvider extends RecipeProvider {
	public SimpleRecipeProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, SimpleTeleportersItems.ENDER_SHARD.get())
				.requires(SimpleTeleportersItems.ENDER_SHARD.get())
				.unlockedBy("has_ender_shard", has(SimpleTeleportersItems.ENDER_SHARD.get()))
				.save(consumer, new ResourceLocation(SimpleTeleporters.MOD_ID, "clear_shard"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, SimpleTeleportersBlocks.TELEPORTER.get())
				.pattern(" C ")
				.pattern("GNG")
				.pattern("QQQ")
				.define('C', SimpleTeleportersItems.ENDER_SHARD.get())
				.define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
				.define('Q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
				.define('N', Tags.Items.INGOTS_NETHERITE)
				.unlockedBy("has_ender_shard", has(SimpleTeleportersItems.ENDER_SHARD.get()))
				.unlockedBy("has_gold_block", has(Tags.Items.STORAGE_BLOCKS_GOLD))
				.unlockedBy("has_quartz_block", has(Tags.Items.STORAGE_BLOCKS_QUARTZ))
				.unlockedBy("has_netherite_ingot", has(Tags.Items.INGOTS_NETHERITE))
				.save(consumer);

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.ENDER_EYE), RecipeCategory.TRANSPORTATION,
						SimpleTeleportersItems.ENDER_SHARD.get(), 0.7F, 200)
				.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
				.save(consumer, new ResourceLocation(SimpleTeleporters.MOD_ID, "ender_shard"));
	}
}