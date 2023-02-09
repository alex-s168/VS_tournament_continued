package de.m_marvin.industria.core.physics.types;

import org.valkyrienskies.core.api.ships.Ship;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ContraptionHitResult extends HitResult {

	private final Ship contraption;
	private final BlockPos shipBlock;
	private final boolean miss;
		
	protected ContraptionHitResult(boolean miss, Vec3 location, BlockPos shipBlock, Ship contraption) {
		super(location);
		this.miss = miss;
		this.shipBlock = shipBlock;
		this.contraption = contraption;
	}
	
	@Override
	public Type getType() {
		return this.miss ? Type.MISS : Type.BLOCK;
	}
	
	public BlockPos getShipBlock() {
		return shipBlock;
	}
	
	public Ship getContraption() {
		return contraption;
	}
	
	public static ContraptionHitResult miss(Vec3 location) {
		return new ContraptionHitResult(true, location, null, null);
	}
	
	public static ContraptionHitResult hit(Vec3 location, BlockPos shipBlock, Ship contraption) {
		return new ContraptionHitResult(false, location, shipBlock, contraption);
	}
	
}
