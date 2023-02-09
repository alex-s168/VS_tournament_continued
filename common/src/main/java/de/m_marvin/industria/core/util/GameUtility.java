package de.m_marvin.industria.core.util;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ScheduledTick;

public class GameUtility {
	
	public static Vec3f getWorldGravity(BlockGetter level) {
		return new Vec3f(0, 0.1F, 0); // TODO
	}
	
	public static void dropItem(Level level, ItemStack stack, Vec3f position, float spreadFactH, float spreadFactV) {
		ItemEntity drop = new ItemEntity(level, position.x, position.y, position.z, stack);
		Vec3f spread = new Vec3f(
				(level.random.nextFloat() - 0.5F) * spreadFactH,
				level.random.nextFloat() * spreadFactV,
				(level.random.nextFloat() - 0.5F) * spreadFactH
				);
		drop.setDeltaMovement(spread.writeTo(new Vec3(0, 0, 0)));
		level.addFreshEntity(drop);
	}
	
	public static void setBlock(Level level, BlockPos pos, BlockState state) {
		LevelChunk chunk = (LevelChunk) level.getChunk(pos);
		LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(pos.getY()));
		BlockState oldState = level.getBlockState(pos);
		section.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
		PhysicUtility.triggerBlockChange(level, pos, oldState, state);
	}

	public static void removeBlock(Level level, BlockPos pos) {
		level.removeBlockEntity(pos);
		setBlock(level, pos, Blocks.AIR.defaultBlockState());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void copyBlock(Level level, BlockPos from, BlockPos to) {
		BlockState state = level.getBlockState(from);
		BlockEntity blockentity = level.getBlockEntity(from);
		
		setBlock(level, to, state);
		
		// Transfer pending schedule-ticks
		if (level.getBlockTicks().hasScheduledTick(from, state.getBlock())) {
			level.getBlockTicks().schedule(new ScheduledTick(state.getBlock(), to, 0, 0));	
		}
		
		// Transfer block-entity data
		if (state.hasBlockEntity() && blockentity != null) {
			CompoundTag data = blockentity.saveWithoutMetadata();
			level.setBlockEntity(blockentity);
			BlockEntity newBlockentity = level.getBlockEntity(to);
			if (newBlockentity != null) {
				newBlockentity.load(data);
			}
		}
	}
	
	public static void relocateBlock(Level level, BlockPos from, BlockPos to) {
		copyBlock(level, from, to);
		removeBlock(level, from);
	}
	
	public static void triggerUpdate(Level level, BlockPos pos) {
		level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 8);
	}
	
	public static HitResult raycast(Level level, Vec3d from, Vec3d direction, double range) {
		return raycast(level, from, from.add(direction.mul(range)));
	}
	
	public static HitResult raycast(Level level, Vec3d from, Vec3d to) {
		ClipContext clipContext = new ClipContext(from.writeTo(new Vec3(0, 0, 0)), to.writeTo(new Vec3(0, 0, 0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
		return level.clip(clipContext);
	}
	
}
