package de.m_marvin.industria.core.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;


import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class PhysicUtility {

    protected static Map<Long, String> contraptionNames = new HashMap<>();

    public static Iterable<Ship> getContraptionIntersecting(Level level, BlockPos position)  {
        return VSGameUtilsKt.getShipsIntersecting(level, new AABB(position, position));
    }

    public static Ship getContraptionOfBlock(Level level, BlockPos shipBlockPos) {
        return VSGameUtilsKt.getShipManagingPos(level, shipBlockPos);
    }

    public static String getDimensionId(Level level) {
        return VSGameUtilsKt.getDimensionId(level);
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

    public static void setName(Ship contraption, String name) {
        contraptionNames.put(contraption.getId(), name);
    }

    public static String getName(Ship contraption, String name) {
        return contraptionNames.get(contraption.getId());
    }

    public static Long getFirstContraptionIdWithName(String name) {
        for (Entry<Long, String> entry : contraptionNames.entrySet()) {
            if (entry.getValue().equals(name)) return entry.getKey();
        }
        return 0L;
    }

    public static Ship getFirstContraptionWithName(Level level, String name) {
        for (Entry<Long, String> entry : contraptionNames.entrySet()) {
            if (entry.getValue().equals(name)) return getContraptionById(level, entry.getKey());
        }
        return null;
    }

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

    public static List<Ship> getLoadedContraptions(Level level) {
        QueryableShipData<LoadedShip> shipData = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips();
        List<Ship> ships = new ArrayList<>();
        ships.addAll(shipData);
        return ships;
    }

    public static Ship createNewContraptionAt(ServerLevel level, BlockPos position, float scale) {
        return createContraptionAt(level, Vec3d.fromVec(position), scale);
    }

    public static ServerShip createContraptionAt(ServerLevel level, Vec3d position, float scale) {
        Ship parentContraption = VSGameUtilsKt.getShipManagingPos(level, position.writeTo(new Vector3d()));
        if (parentContraption != null) {
            position = toWorldPos(parentContraption, position);
        }
        String dimensionId = getDimensionId(level);
        Ship newContraption = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(position.writeTo(new Vector3i()), false, scale, dimensionId);

        // Stone for safety reasons
        BlockPos pos2 = toContraptionBlockPos(newContraption, position);
        level.setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);

        return (ServerShip) newContraption;
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

    public static Ship convertToContraption(ServerLevel level, AABB areaBounds, boolean removeOriginal, float scale) {

        BlockPos structureCornerMin = null;
        BlockPos structureCornerMax = null;
        boolean noSolids = true;

        int areaMinBlockX = (int) Math.floor(areaBounds.minX);
        int areaMinBlockY = (int) Math.floor(areaBounds.minY);
        int areaMinBlockZ = (int) Math.floor(areaBounds.minZ);
        int areaMaxBlockX = (int) Math.floor(areaBounds.maxX);
        int areaMaxBlockY = (int) Math.floor(areaBounds.maxY);
        int areaMaxBlockZ = (int) Math.floor(areaBounds.maxZ);

        for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
            for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
                for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {

                    BlockPos itPos = new BlockPos(x, y, z);
                    BlockState itState = level.getBlockState(itPos);

                    if (isValidContraptionBlock(itState)) {

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

                }
            }
        }

        if (structureCornerMax == null) structureCornerMax = structureCornerMin = new BlockPos(areaBounds.getCenter().x(), areaBounds.getCenter().y(), areaBounds.getCenter().z());

        Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
        ServerShip contraption = createContraptionAt(level, contraptionPos, scale);

        Vec3d contraptionOrigin = toContraptionPos(contraption, contraptionPos);

        for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
            for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
                for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {

                    BlockPos itPos = new BlockPos(x, y, z);
                    BlockState itState = level.getBlockState(itPos);

                    if (isValidContraptionBlock(itState)) {

                        Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
                        Vec3d shipPos = contraptionOrigin.add(relativePosition);

                        GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));

                        if (isSolidContraptionBlock(level, itPos, itState)) {

                            noSolids = false;

                        }

                    }

                }
            }
        }

        if (noSolids) {
            level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.STONE.defaultBlockState(), 34);
        } else {
            BlockState centerStructureBlock = level.getBlockState(new BlockPos(contraptionPos.x, contraptionPos.y, contraptionPos.z));
            if (!isValidContraptionBlock(centerStructureBlock)) {
                level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.AIR.defaultBlockState(), 34);
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

        setPosition((ServerShip) contraption, new ContraptionPosition(new Quaternion(new Vec3i(0, 1, 1), 0), contraptionPos), false);

        return contraption;

    }

    public static ServerShip assembleToContraption(ServerLevel level, List<BlockPos> blocks, boolean removeOriginal, float scale) {

        if (blocks.isEmpty()) {
            return null;
        }

        BlockPos structureCornerMin = blocks.get(0);
        BlockPos structureCornerMax = blocks.get(0);

        for (BlockPos itPos : blocks) {
            structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos);
            structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos);
        }

        Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
        ServerShip contraption = createContraptionAt(level, contraptionPos, scale);

        Vec3d contraptionOrigin = toContraptionPos(contraption, contraptionPos);
        boolean noSolids = true;

        for (BlockPos itPos : blocks) {

            BlockState itState = level.getBlockState(itPos);

            Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
            Vec3d shipPos = contraptionOrigin.add(relativePosition);

            GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));

            if (isSolidContraptionBlock(level, itPos, itState)) {

                noSolids = false;

            }

        }

        if (noSolids) {
            level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.STONE.defaultBlockState(), 34);
        } else {
            BlockState centerStructureBlock = level.getBlockState(new BlockPos(contraptionPos.x, contraptionPos.y, contraptionPos.z));
            if (!isValidContraptionBlock(centerStructureBlock)) {
                level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.AIR.defaultBlockState(), 34);
            }
        }

        if (removeOriginal) {
            for (BlockPos itPos : blocks) {
                GameUtility.removeBlock(level, itPos);
            }
        }

        setPosition((ServerShip) contraption, new ContraptionPosition(new Quaternion(new Vec3i(0, 1, 1), 0), contraptionPos), false);

        return contraption;

    }

    public static boolean isSolidContraptionBlock(Level level, BlockPos pos, BlockState state) {
        return !state.getCollisionShape(level, pos).isEmpty(); // FIXME Working way to check if block has collision as contraption part
    }

    public static boolean isValidContraptionBlock(BlockState state) {
        return !state.isAir();
    }

    public static Ship getContraptionById(Level level, long id) {
        for (Ship contraption : getLoadedContraptions(level)) {
            if (contraption.getId() == id) {
                return contraption;
            }
        }
        return null;
    }

    public static ContraptionHitResult raycastForContraption(Level level, Vec3d from, Vec3d direction, double range) {
        return raycastForContraption(level, from, from.add(direction.mul(range)));
    }

    public static ContraptionHitResult raycastForContraption(Level level, Vec3d from, Vec3d to) {
        ClipContext clipContext = new ClipContext(from.writeTo(new Vec3(0, 0, 0)), to.writeTo(new Vec3(0, 0, 0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
        HitResult clipResult = level.clip(clipContext);

        if (clipResult.getType() == Type.BLOCK) {
            BlockPos hitBlockPos = ((BlockHitResult) clipResult).getBlockPos();
            Ship contraption = getContraptionOfBlock(level, hitBlockPos);

            if (contraption != null) {
                Vec3 hitPosition = clipResult.getLocation();
                return ContraptionHitResult.hit(hitPosition, hitBlockPos, contraption);
            }

        }
        return ContraptionHitResult.miss(clipResult.getLocation());
    }

}