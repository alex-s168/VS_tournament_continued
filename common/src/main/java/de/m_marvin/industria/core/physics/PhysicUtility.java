package de.m_marvin.industria.core.physics;

import java.lang.Math;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.world.level.block.Blocks;
import org.joml.*;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.apigame.world.chunks.BlockType;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.mod.common.BlockStateInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.univec.impl.Vec3d;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import static org.valkyrienskies.mod.common.VSGameUtilsKt.getDimensionId;

public class PhysicUtility {

	protected static Map<Long, String> contraptionNames = new HashMap<>();
	
	/* Translating of positions and moving of contraptions */
	
	public static Vec3d toContraptionPos(Ship contraption, Vec3d pos) {
		Matrix4dc worldToShip = contraption.getWorldToShip();
		if (worldToShip != null) {
			Vector3d transformPosition = worldToShip.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static BlockPos toContraptionBlockPos(Ship contraption, Vec3d pos) {
		Vec3d position = toContraptionPos(contraption, pos);
		return new BlockPos(position.x, position.y, position.z);
	}
	
	public static BlockPos toContraptionBlockPos(Ship contraption, BlockPos pos) {
		return toContraptionBlockPos(contraption, Vec3d.fromVec(pos));
	}

	public static Vec3d toWorldPos(Ship contraption, Vec3d pos) {
		Matrix4dc shipToWorld = contraption.getShipToWorld();
		if (shipToWorld != null) {
			Vector3d transformedPosition = shipToWorld.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformedPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static Vec3d toWorldPos(Ship contaption, BlockPos pos) {
		return toWorldPos(contaption, Vec3d.fromVec(pos).addI(0.5, 0.5, 0.5));
	}

	public static BlockPos toWorldBlockPos(Ship contraption, BlockPos pos) {
		Vec3d position = toWorldPos(contraption, pos);
		return new BlockPos(position.x, position.y, position.z);
	}

	public static ContraptionPosition getPosition(ServerShip contraption, boolean massCenter) {
		if (massCenter) {
			Vec3d position = Vec3d.fromVec(contraption.getTransform().getPositionInWorld());
			Quaterniondc jomlQuat = contraption.getTransform().getShipToWorldRotation();
			Quaternion orientation = new Quaternion((float) jomlQuat.x(), (float) jomlQuat.y(), (float) jomlQuat.z(), (float) jomlQuat.w());
			return new ContraptionPosition(orientation, position);
		} else {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new BlockPos(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new BlockPos(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getInertiaData().getCenterOfMassInShip());
			Vec3d centerOfMassOffset = shipCoordMassCenter.sub(shipCoordCenter).add(1.0, 1.0, 1.0);
			Vec3d position = Vec3d.fromVec(contraption.getTransform().getPositionInWorld()).sub(centerOfMassOffset);
			Quaterniondc jomlQuat = contraption.getTransform().getShipToWorldRotation();
			Quaternion orientation = new Quaternion((float) jomlQuat.x(), (float) jomlQuat.y(), (float) jomlQuat.z(), (float) jomlQuat.w());
			return new ContraptionPosition(orientation, position);
		}

	}

	public static void setPosition(ServerShip contraption, ContraptionPosition position, boolean massCenter) {
		if (massCenter) {
			ShipTransform transform = contraption.getTransform();
			((Vector3d) transform.getPositionInWorld()).set(position.getPosition().writeTo(new Vector3d()));
			((Quaterniond) transform.getShipToWorldRotation()).set(position.getOrientation().i(), position.getOrientation().j(), position.getOrientation().k(), position.getOrientation().r());
			((ShipData) contraption).setTransform(transform);	// FIXME does not work with LoadedShip
		} else {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new BlockPos(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new BlockPos(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getInertiaData().getCenterOfMassInShip());
			Vec3d centerOfMassOffset = shipCoordMassCenter.sub(shipCoordCenter).add(1.0, 1.0, 1.0);
			ShipTransform transform = contraption.getTransform();
			((Vector3d) transform.getPositionInWorld()).set(position.getPosition().add(centerOfMassOffset).writeTo(new Vector3d()));
			((Quaterniond) transform.getShipToWorldRotation()).set(position.getOrientation().i(), position.getOrientation().j(), position.getOrientation().k(), position.getOrientation().r());
			((ShipData) contraption).setTransform(transform);	// FIXME does not work with LoadedShip
		}
	}


	public static ServerShip createContraptionAt(Vec3d position, float scale, Level level) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		Ship parentContraption = VSGameUtilsKt.getShipManagingPos(level, position.writeTo(new Vector3d()));
		if (parentContraption != null) {
			position = PhysicUtility.toWorldPos(parentContraption, position);
		}
		String dimensionId = getDimensionId(level);
		Ship newContraption = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewShipAtBlock(position.writeTo(new Vector3i()), false, scale, dimensionId);

		// Stone for safety reasons
		BlockPos pos2 = PhysicUtility.toContraptionBlockPos(newContraption, position);
		level.setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);

		return (ServerShip) newContraption;
	}

	public static Ship convertToContraption(AABB areaBounds, boolean removeOriginal, float scale, Level level) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";

		BlockPos structureCornerMin = null;
		BlockPos structureCornerMax = null;

		int areaMinBlockX = (int) Math.floor(areaBounds.minX);
		int areaMinBlockY = (int) Math.floor(areaBounds.minY);
		int areaMinBlockZ = (int) Math.floor(areaBounds.minZ);
		int areaMaxBlockX = (int) Math.floor(areaBounds.maxX);
		int areaMaxBlockY = (int) Math.floor(areaBounds.maxY);
		int areaMaxBlockZ = (int) Math.floor(areaBounds.maxZ);
		boolean hasSolids = false;

		for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
			for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
				for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {

					BlockPos itPos = new BlockPos(x, y, z);
					BlockState itState = level.getBlockState(itPos);

					if (PhysicUtility.isValidContraptionBlock(itState)) {

						if (structureCornerMin == null) {
							structureCornerMin = itPos;
						} else {
							structureCornerMin = MathUtility.getMinCorner(itPos, structureCornerMin);
						}

						if (structureCornerMax == null) {
							structureCornerMax = itPos;
						} else {
							structureCornerMax = MathUtility.getMaxCorner(itPos, structureCornerMax);
						}

					}

					if (PhysicUtility.isSolidContraptionBlock(itState)) hasSolids = true;

				}
			}
		}

		if (!hasSolids) return null;

		if (structureCornerMax == null) structureCornerMax = structureCornerMin = new BlockPos(areaBounds.getCenter().x(), areaBounds.getCenter().y(), areaBounds.getCenter().z());

		Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ServerShip contraption = createContraptionAt(contraptionPos, scale, level);

		Vec3d contraptionOrigin = PhysicUtility.toContraptionPos(contraption, contraptionPos);

		for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
			for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
				for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {
					BlockPos itPos = new BlockPos(x, y, z);
					Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
					Vec3d shipPos = contraptionOrigin.add(relativePosition);

					GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));

				}
			}
		}

		if (removeOriginal) {
			for (int x = structureCornerMin.getX(); x <= structureCornerMax.getX(); x++) {
				for (int z = structureCornerMin.getZ(); z <= structureCornerMax.getZ(); z++) {
					for (int y = structureCornerMin.getY(); y <= structureCornerMax.getY(); y++) {
						GameUtility.removeBlock(level, new BlockPos(x, y, z));
					}
				}
			}
		}

		for (int x = structureCornerMin.getX(); x <= structureCornerMax.getX(); x++) {
			for (int z = structureCornerMin.getZ(); z <= structureCornerMax.getZ(); z++) {
				for (int y = structureCornerMin.getY(); y <= structureCornerMax.getY(); y++) {
					BlockPos itPos = new BlockPos(x, y, z);
					Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
					Vec3d shipPos = contraptionOrigin.add(relativePosition);

					GameUtility.triggerUpdate(level, itPos);
					GameUtility.triggerUpdate(level, new BlockPos(shipPos.x, shipPos.y, shipPos.z));
				}
			}
		}

		setPosition((ServerShip) contraption, new ContraptionPosition(new Quaternion(new Vec3i(0, 1, 1), 0), contraptionPos), false);

		return contraption;

	}

	public static ServerShip assembleToContraption(List<BlockPos> blocks, boolean removeOriginal, float scale, Level level) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";

		if (blocks.isEmpty()) {
			return null;
		}

		BlockPos structureCornerMin = blocks.get(0);
		BlockPos structureCornerMax = blocks.get(0);
		boolean hasSolids = false;

		for (BlockPos itPos : blocks) {
			structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos);
			structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos);

			if (PhysicUtility.isSolidContraptionBlock(level.getBlockState(itPos))) hasSolids = true;
		}

		if (!hasSolids) return null;

		Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ServerShip contraption = createContraptionAt(contraptionPos, scale, level);

		Vec3d contraptionOrigin = PhysicUtility.toContraptionPos(contraption, contraptionPos);
		BlockPos centerBlockPos = new BlockPos(contraptionPos.x, contraptionPos.y, contraptionPos.z);

		for (BlockPos itPos : blocks) {
			Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
			Vec3d shipPos = contraptionOrigin.add(relativePosition);

			GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));

		}

		if (!blocks.contains(centerBlockPos)) {
			BlockPos centerShipPos = PhysicUtility.toContraptionBlockPos(contraption, centerBlockPos);
			level.setBlock(centerShipPos, Blocks.AIR.defaultBlockState(), 3);
		}

		if (removeOriginal) {
			for (BlockPos itPos : blocks) {
				GameUtility.removeBlock(level, itPos);
			}
		}

		for (BlockPos itPos : blocks) {
			Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
			Vec3d shipPos = contraptionOrigin.add(relativePosition);

			GameUtility.triggerUpdate(level, itPos);
			GameUtility.triggerUpdate(level, new BlockPos(shipPos.x, shipPos.y, shipPos.z));
		}

		setPosition((ServerShip) contraption, new ContraptionPosition(new Quaternion(new Vec3i(0, 1, 1), 0), contraptionPos), false);

		return contraption;

	}

	public static void removeContraption(ServerLevel level, Ship contraption) {
		AABBic bounds = contraption.getShipAABB();
		if (bounds != null) {
			for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
				for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
					for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
						GameUtility.removeBlock(level, new BlockPos(x, y, z));
					}
				}
			}
		}
		contraptionNames.remove(contraption.getId());
	}

	/* Util stuff */

	public static void triggerBlockChange(Level level, BlockPos pos, BlockState prevState, BlockState newState) {
		BlockStateInfo.INSTANCE.onSetBlock(level, pos, prevState, newState);
	}
	
	public static boolean isSolidContraptionBlock(BlockState state) {
		Pair<Double, BlockType> blockData = BlockStateInfo.INSTANCE.get(state);
		return blockData.getSecond() == VSGameUtilsKt.getVsCore().getBlockTypes().getSolid() && blockData.getFirst() > 0;
	}
	
	public static boolean isValidContraptionBlock(BlockState state) {
		return !state.isAir();
	}
	
}
