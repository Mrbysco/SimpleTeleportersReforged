package com.mrbysco.simpleteleporters.block;

import com.mojang.serialization.MapCodec;
import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
import com.mrbysco.simpleteleporters.config.SimpleTeleportersConfig;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

import java.util.Set;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class TeleporterBlock extends BaseEntityBlock {
	public static final MapCodec<TeleporterBlock> CODEC = simpleCodec(TeleporterBlock::new);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty ON = BooleanProperty.create("on");

	protected static final VoxelShape SHAPE = Shapes.box(0D, 0.0D, 0D, 1D, 0.38D, 1D);

	@Override
	public MapCodec<TeleporterBlock> codec() {
		return CODEC;
	}

	public TeleporterBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(ON, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean p_451772_) {
		if (!level.isClientSide() && entity instanceof ServerPlayer player) {
			if (entity.isShiftKeyDown()) {
				if (level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter) {
					if (!teleporter.hasCrystal()) {
						player.displayClientMessage(Component.translatable("text.simpleteleporters.error.no_crystal").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
					} else if (!teleporter.isInDimension(entity)) {
						player.displayClientMessage(Component.translatable("text.simpleteleporters.error.wrong_dimension").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
					} else if (!teleporter.isCoolingDown()) {
						GlobalPos globalPos = teleporter.getCrystal().get(SimpleTeleportersComponents.GLOBAL_POS);
						if (globalPos == null) {
							player.displayClientMessage(Component.translatable("text.simpleteleporters.error.unlinked_teleporter").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
							return;
						}

						BlockPos teleportPos = globalPos.pos();
						ResourceKey<Level> targetDimension = globalPos.dimension();
						MinecraftServer server = level.getServer();
						ServerLevel targetLevel = server != null ? server.getLevel(targetDimension) : null;

						if (targetLevel == null) {
							player.displayClientMessage(Component.translatable("text.simpleteleporters.error.invalid_position").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
						} else if (targetLevel.getBlockState(teleportPos).isSuffocating(targetLevel, teleportPos)) {
							player.displayClientMessage(Component.translatable("text.simpleteleporters.error.invalid_position").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
						} else {
							Vec3 targetPos = new Vec3(teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5);

							// Handle cross-dimensional or same-dimension teleport
							if (targetDimension.equals(level.dimension())) {
								// Same dimension - simple teleport
								player.hurtMarked = true;
								player.connection.teleport(targetPos.x(), targetPos.y(), targetPos.z(), entity.getYRot(), entity.getXRot());
							} else {
								// Cross-dimensional teleport
								player.teleportTo(targetLevel, targetPos.x(), targetPos.y(), targetPos.z(),
										Set.of(), entity.getYRot(), entity.getXRot(), true);
							}

							player.setDeltaMovement(0, 0, 0);
							player.hasImpulse = true;

							level.playSound(null, pos, SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
							teleporter.setCooldown(10);

							// Set cooldown on destination teleporter if present
							BlockEntity down = targetLevel.getBlockEntity(teleportPos.below());
							if (down instanceof TeleporterBlockEntity tpDown) {
								tpDown.setCooldown(10);
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter) {
			// Handle dyeing with a dye - works with or without sneaking
			if (stack.getItem() instanceof DyeItem dyeItem) {
				DyeColor newColor = dyeItem.getDyeColor();
				if (teleporter.getColor() != newColor) {
					if (!level.isClientSide()) {
						teleporter.setColor(newColor);
						if (!player.getAbilities().instabuild) {
							stack.shrink(1);
						}
					}
					level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.CONSUME;
			}

			if (teleporter.hasCrystal()) {
				ItemStack crystal = teleporter.getCrystal();

				if (!player.addItem(crystal)) {
					player.drop(crystal, true);
				}

				player.playSound(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_REMOVED.get(), 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

				level.setBlockAndUpdate(pos, state.setValue(ON, false));
				teleporter.setCrystal(ItemStack.EMPTY);

				level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
				return InteractionResult.SUCCESS;
			} else {
				if (!stack.isEmpty() && (stack.is(SimpleTeleportersItems.ENDER_SHARD.get()) || stack.is(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get()))) {
					if (stack.has(SimpleTeleportersComponents.GLOBAL_POS)) {
						player.playSound(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED.get(), 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
						level.setBlockAndUpdate(pos, state.setValue(ON, true));
						ItemStack crystal = stack.split(1);
						teleporter.setCrystal(crystal);

						return InteractionResult.SUCCESS;
					} else {
						player.displayClientMessage(Component.translatable("text.simpleteleporters.error.unlinked_shard").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter && teleporter.hasCrystal()) {
			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), teleporter.getCrystal());
		}
		return super.playerWillDestroy(level, pos, state, player);
	}


	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ON).add(WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TeleporterBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(ON) && !SimpleTeleportersConfig.CLIENT.disableParticles.getAsBoolean()) {
			for (int i = 0; i < 15; i++) {
				level.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.2F + (random.nextFloat() / 2), pos.getY() + 0.4F, pos.getZ() + 0.2F + (random.nextFloat() / 2), 0, random.nextFloat(), 0);    // originally method_8406
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
		FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
		boolean isWater = fluidState.getType().equals(Fluids.WATER);
		return this.defaultBlockState().setValue(WATERLOGGED, isWater);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SimpleTeleportersBlockEntities.TELEPORTER.get(), level.isClientSide() ? null : TeleporterBlockEntity::serverTick);
	}

	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter) {
			boolean isPowered = level.hasNeighborSignal(pos);
			boolean wasPowered = teleporter.wasPowered();

			// Only trigger on rising edge (unpowered -> powered)
			if (isPowered && !wasPowered) {
				teleportAllEntities(level, pos, teleporter);
			}

			teleporter.setPowered(isPowered);
		}
	}

	/**
	 * Teleports all entities standing on the teleporter to the linked destination.
	 */
	private void teleportAllEntities(Level level, BlockPos pos, TeleporterBlockEntity teleporter) {
		if (!teleporter.hasCrystal() || teleporter.isCoolingDown()) {
			return;
		}

		GlobalPos globalPos = teleporter.getCrystal().get(SimpleTeleportersComponents.GLOBAL_POS);
		if (globalPos == null) {
			return;
		}

		BlockPos teleportPos = globalPos.pos();
		ResourceKey<Level> targetDimension = globalPos.dimension();
		MinecraftServer server = level.getServer();
		ServerLevel targetLevel = server != null ? server.getLevel(targetDimension) : null;

		if (targetLevel == null || targetLevel.getBlockState(teleportPos).isSuffocating(targetLevel, teleportPos)) {
			return;
		}

		// Get all entities standing on the teleporter
		AABB area = new AABB(pos).inflate(0.1, 0.5, 0.1).move(0, 0.5, 0);
		List<Entity> entities = level.getEntities(null, area);

		if (entities.isEmpty()) {
			return;
		}

		boolean teleportedAny = false;
		Vec3 targetPos = new Vec3(teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5);
		boolean crossDimensional = !targetDimension.equals(level.dimension());

		for (Entity entity : entities) {
			// Check dimension compatibility
			if (!teleporter.isInDimension(entity)) {
				continue;
			}

			if (entity instanceof ServerPlayer player) {
				if (crossDimensional) {
					// Cross-dimensional teleport for players
					player.teleportTo(targetLevel, targetPos.x(), targetPos.y(), targetPos.z(),
							Set.of(), entity.getYRot(), entity.getXRot(), true);
				} else {
					// Same dimension teleport
					player.hurtMarked = true;
					player.connection.teleport(targetPos.x(), targetPos.y(), targetPos.z(), entity.getYRot(), entity.getXRot());
				}
				player.setDeltaMovement(0, 0, 0);
				player.hasImpulse = true;
			} else {
				if (crossDimensional) {
					// Cross-dimensional teleport for non-player entities
					entity.teleportTo(targetLevel, targetPos.x(), targetPos.y(), targetPos.z(),
							Set.of(), entity.getYRot(), entity.getXRot(), true);
				} else {
					// Same dimension teleport
					entity.teleportTo(targetPos.x(), targetPos.y(), targetPos.z());
				}
				entity.setDeltaMovement(0, 0, 0);
			}

			teleportedAny = true;
		}

		if (teleportedAny) {
			level.playSound(null, pos, SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			teleporter.setCooldown(10);

			// Also set cooldown on destination teleporter if present
			BlockEntity down = targetLevel.getBlockEntity(teleportPos.below());
			if (down instanceof TeleporterBlockEntity tpDown) {
				tpDown.setCooldown(10);
			}
		}
	}
}
