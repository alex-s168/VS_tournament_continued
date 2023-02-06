package de.m_marvin.industria.core.util;

import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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

    public static void copyBlock(Level level, BlockPos from, BlockPos to) {
        BlockState state = level.getBlockState(from);
        BlockEntity blockentity = level.getBlockEntity(from);
        level.setBlock(to, state, 50);
        if (state.hasBlockEntity() && blockentity != null) {
            CompoundTag data = blockentity.saveWithoutMetadata();
            level.setBlockEntity(blockentity);
            BlockEntity newBlockentity = level.getBlockEntity(to);
            if (newBlockentity != null) {
                newBlockentity.load(data);
            }
        }
    }

    public static void removeBlock(Level level, BlockPos pos) {
        level.removeBlockEntity(pos);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 50);
    }

    public static void relocateBlock(Level level, BlockPos from, BlockPos to) {
        copyBlock(level, from, to);
        removeBlock(level, from);
    }

}