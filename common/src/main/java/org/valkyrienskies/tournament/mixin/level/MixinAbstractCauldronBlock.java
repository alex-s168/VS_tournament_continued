package org.valkyrienskies.tournament.mixin.level;

import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.CauldronRecipes;
import org.valkyrienskies.tournament.ShaftsKt;
import org.valkyrienskies.tournament.util.HeatKt;

import java.util.ArrayList;
import java.util.HashMap;

@Mixin(AbstractCauldronBlock.class)
public abstract class MixinAbstractCauldronBlock {
    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (hand == InteractionHand.OFF_HAND) {
            var items = new HashMap<Item, Integer>();

            var es = level.getEntities(null, AABB.ofSize(Vec3.atCenterOf(pos), 1, 1, 1));
            for (Entity e : es) {
                if (e instanceof ItemEntity ie) {
                    var stack = ie.getItem();
                    var old = 0;
                    if (items.containsKey(stack.getItem())) {
                        old = items.get(stack.getItem());
                    }

                    items.put(stack.getItem(), old + stack.getCount());
                }
            }

            int heat = 0;
            for (BlockPos p : ShaftsKt.neighborBlocks(pos)) {
                heat += HeatKt.getHeat(level.getBlockState(p));
            }

            var clicked = player.getMainHandItem();

            var env = new CauldronRecipes.Env(heat);

            var oldItems = new HashMap<>(items);

            boolean once = false;
            while (CauldronRecipes.craftOnce(clicked, items, (ItemStack i) -> {
                        Block.popResource(level, pos, i);
                        return Unit.INSTANCE;
                    }, env)) {
                once = true;

                oldItems.forEach((entry, count) -> {
                    int neww = items.get(entry);
                    if (neww < count) {
                        var removed = count - neww;

                        var toKill = new ArrayList<Entity>();
                        for (var e : es) {
                            if (e instanceof ItemEntity ie) {
                                var stack = ie.getItem();

                                if (stack.getItem() == entry) {
                                    int a = stack.getCount() - removed;
                                    if (a < 0) a = 0;

                                    stack.setCount(a);
                                    if (a == 0) {
                                        toKill.add(e);
                                    }
                                }
                            }
                        }

                        for (var e : toKill) {
                            e.kill();
                        }
                    }
                });
                oldItems = new HashMap<>(items);
            }

            if (once) {
                cir.setReturnValue(InteractionResult.CONSUME_PARTIAL);
            }
        }
    }
}
