package com.mrbysco.simpleteleporters.datagen.data;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import guideme.internal.GuideME;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class SimpleRecipeProvider extends RecipeProvider {
	public SimpleRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
		super(recipeOutput, advancementOutput);
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
				.unlockedBy("has_gold_block", has(Tags.BlockItems.STORAGE_BLOCKS_GOLD.item()))
				.unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
				.save(output);

		// Ender shard from ender pearl
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.ENDER_PEARL), RecipeCategory.TRANSPORTATION, CookingBookCategory.MISC,
						SimpleTeleportersItems.ENDER_SHARD.get(), 0.7F, 200)
				.unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
				.save(output);

		// Hearth crystal from ender shard
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SimpleTeleportersItems.ENDER_SHARD.get()), RecipeCategory.TRANSPORTATION,
						CookingBookCategory.MISC, SimpleTeleportersItems.HEARTH_CRYSTAL.get(), 0.7F, 200)
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

		if (ModList.get().isLoaded("guideme")) {
			shapeless(RecipeCategory.MISC, new ItemStackTemplate(GuideME.GUIDE_ITEM.get(), DataComponentPatch.builder()
					.set(GuideME.GUIDE_ID_COMPONENT, SimpleTeleporters.id("guide")).build()))
					.requires(Items.PAPER)
					.requires(Items.AMETHYST_SHARD)
					.unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
					.save(output.withConditions(new ModLoadedCondition("guideme")),
							SimpleTeleporters.id("guide_book").toString());

		}
	}
}
