package net.saint.acclimatize.mixinlogic;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.saint.acclimatize.ModClient;
import net.saint.acclimatize.compat.FallingLeafParticleCompat;
import net.saint.acclimatize.util.MathUtil;

public interface ParticleMixinLogic {

	// Properties

	public Vec3d getPosition();

	public void setPosition(Vec3d position);

	Box getBoundingBox();

	ClientWorld getWorld();

	double getHeatValue();

	void setHeatValue(double value);

	// Delta Calculation

	default double calculateDeltaX(double dx) {
		var windEffect = calculateWindEffect();
		var adjustedWind = new Vec3d(-windEffect.z, 0, windEffect.x);

		var position = getPosition();
		var direction = ModClient.cachedWindDirection;
		var particleDirection = new Vec3d(MathUtil.cos(direction), 0, MathUtil.sin(direction));
		var baseFactor = getWindInfluenceFactor(position, particleDirection);
		var typeFactor = getWindInfluenceFactorForParticleType();

		// Update heat/lift for particle.
		updateHeatValue(position);

		return dx + adjustedWind.x * baseFactor * typeFactor;
	}

	default double calculateDeltaZ(double dz) {
		var windEffect = calculateWindEffect();
		var adjustedWind = new Vec3d(-windEffect.z, 0, windEffect.x);

		var position = getPosition();
		var direction = ModClient.cachedWindDirection;
		var particleDirection = new Vec3d(MathUtil.cos(direction), 0, MathUtil.sin(direction));
		var baseFactor = getWindInfluenceFactor(position, particleDirection);
		var typeFactor = getWindInfluenceFactorForParticleType();

		updateHeatValue(position);

		return dz + adjustedWind.z * baseFactor * typeFactor;
	}

	// Calculation Details

	private double getWindInfluenceFactorForParticleType() {
		if (this instanceof SnowflakeParticle) {
			return 1.25;
		}

		if (this instanceof RainSplashParticle) {
			return 0.5;
		}

		if (FallingLeafParticleCompat.isLeafParticle(this)) {
			return 1.5;
		}

		return 1.0;
	}

	private double getWindInfluenceFactor(Vec3d particlePosition, Vec3d windDirection) {
		var world = getWorld();

		// Define how far back in wind origin direction we should check.
		var range = 5;

		// Invert wind direction for checking.
		var invertedWindDirection = windDirection.multiply(-1);

		for (int i = 1; i <= range; i++) {
			var checkPosition = particlePosition.add(invertedWindDirection.multiply(i));
			var blockPosition = BlockPos.ofFloored(checkPosition);
			var state = world.getBlockState(blockPosition);

			if (state.isAir()) {
				// Check if particle is within 0.5 blocks away from a fluid block.
				var fluidPosition = getFluidBlockNearby(blockPosition);

				if (fluidPosition != null) {
					var fluidPositionCenter = fluidPosition.toCenterPos();
					var distance = particlePosition.distanceTo(fluidPositionCenter);

					if (distance < 0.5) {
						// No influence if within 0.5 blocks away from fluid.
						return 0.0;
					}
				}

				// Full influence if wind exposure is confirmed.
				return 1;
			} else if (isNonSolidBlock(state)) {
				// No influence if in water or lava.
				return 0.0;
			}
		}

		return 0.0;
	}

	private BlockPos getFluidBlockNearby(BlockPos position) {
		var world = getWorld();

		for (var x = -1; x <= 1; x++) {
			for (var y = -1; y <= 1; y++) {
				for (var z = -1; z <= 1; z++) {
					var nearbyPos = position.add(x, y, z);
					var nearbyState = world.getBlockState(nearbyPos);

					if (isNonSolidBlock(nearbyState)) {
						return nearbyPos;
					}
				}
			}
		}
		return null;
	}

	private boolean isNonSolidBlock(BlockState state) {
		return state.getFluidState().getFluid() == Fluids.WATER || state.getFluidState().getFluid() == Fluids.LAVA;
	}

	private Vec3d calculateWindEffect() {
		if (!getWorld().getRegistryKey().equals(World.OVERWORLD)) {
			// Return 0 wind strength if not in overworld.
			return new Vec3d(0, 0, 0);
		}

		var windDirection = ModClient.cachedWindDirection;
		var windIntensity = ModClient.cachedWindIntensity;

		var windX = Math.cos(windDirection) * windIntensity * 0.01;
		var windZ = Math.sin(windDirection) * windIntensity * 0.01;
		var initialWindEffect = new Vec3d(windX, 0, windZ);
		var position = getPosition();

		return calculateRealisticWindFlow(initialWindEffect, position);
	}

	private boolean checkForWallInteraction(Vec3d particlePosition) {
		var blockPosition = BlockPos.ofFloored(particlePosition);

		for (var direction : Direction.values()) {
			var state = getWorld().getBlockState(blockPosition.offset(direction));

			if (state.isSolidBlock(getWorld(), blockPosition.offset(direction))) {
				return true;
			}
		}

		return false;
	}

	private Vec3d deflectWind(double windX, double windZ, Vec3d particlePosition) {
		var windDirection = getWindDirection(windX, windZ);
		var wallDirection = getWallFacingDirection(particlePosition, windDirection);
		var incidenceAngle = calculateIncidenceAngle(windDirection, wallDirection);
		var deflectionFactor = calculateDeflectionFactor(incidenceAngle, windX, windZ);

		var deflectedWindX = windX * deflectionFactor;
		var deflectedWindZ = windZ * deflectionFactor;

		deflectedWindX += randomizeDeflection(incidenceAngle);
		deflectedWindZ += randomizeDeflection(incidenceAngle);

		return new Vec3d(deflectedWindX, 0, deflectedWindZ);
	}

	private double randomizeDeflection(double incidenceAngle) {
		var random = getWorld().getRandom();
		return random.nextDouble() * MathUtil.cos(Math.toRadians(incidenceAngle)) * 0.05;
	}

	private Direction getWindDirection(double windX, double windZ) {
		var angle = Math.toDegrees(Math.atan2(windZ, windX));

		if (angle < 0)
			angle += 360;
		if (angle <= 45 || angle > 315)
			return Direction.EAST;
		if (angle > 45 && angle <= 135)
			return Direction.SOUTH;
		if (angle > 135 && angle <= 225)
			return Direction.WEST;
		if (angle > 225)
			return Direction.NORTH;

		return Direction.EAST;
	}

	private Direction getWallFacingDirection(Vec3d particlePosition, Direction windDirection) {
		var blockPosition = BlockPos.ofFloored(particlePosition);

		for (var direction : Direction.values()) {
			var state = getWorld().getBlockState(blockPosition.offset(direction));

			if (state.isSolidBlock(getWorld(), blockPosition.offset(direction)) && direction.getAxis().isHorizontal()) {
				return direction;
			}
		}
		return windDirection;
	}

	private double calculateIncidenceAngle(Direction windDirection, Direction wallDirection) {
		var windAngle = directionToAngle(windDirection);
		var wallAngle = directionToAngle(wallDirection);
		var angleDifference = Math.abs(windAngle - wallAngle);

		if (angleDifference > 180) {
			angleDifference = 360 - angleDifference;
		}

		return angleDifference;
	}

	private int directionToAngle(Direction direction) {
		return switch (direction) {
		case NORTH -> 180;
		case WEST -> 270;
		case EAST -> 90;
		default -> 0;
		};
	}

	private double calculateDeflectionFactor(double incidenceAngle, double windX, double windZ) {
		var baseDeflection = 0.01;
		var velocityFactor = MathUtil.sqrt(windX * windX + windZ * windZ) * 0.01;
		var angleFactor = MathUtil.sqrt(Math.toRadians(incidenceAngle));

		return baseDeflection * angleFactor * velocityFactor;
	}

	private boolean checkForLaminarFlow(double incidenceAngle) {
		return incidenceAngle < 45;
	}

	private Vec3d adjustWindFlow(Vec3d windEffect, Vec3d particlePosition, double windX, double windZ) {
		var windDirection = getWindDirection(windX, windZ);
		var wallDirection = getWallFacingDirection(particlePosition, windDirection);
		var incidenceAngle = calculateIncidenceAngle(windDirection, wallDirection);

		if (checkForLaminarFlow(incidenceAngle)) {
			return slideWindAlongWall(windEffect, wallDirection);
		} else {
			return deflectWind(windX, windZ, particlePosition);
		}
	}

	private Vec3d slideWindAlongWall(Vec3d windEffect, Direction wallDirection) {
		return switch (wallDirection) {
		case NORTH, SOUTH -> new Vec3d(windEffect.x, windEffect.y, 0);
		case EAST, WEST -> new Vec3d(0, windEffect.y, windEffect.z);
		default -> windEffect;
		};
	}

	private Vec3d funnelWindAroundStructure(Vec3d windEffect, Vec3d particlePosition) {
		var windDirection = getWindDirection(windEffect.x, windEffect.z);
		var wallDirection = getWallFacingDirection(particlePosition, windDirection);
		var incidenceAngle = calculateIncidenceAngle(windDirection, wallDirection);

		if (incidenceAngle >= 45 && incidenceAngle <= 135) {
			var funnelFactor = 1.0 + (1.0 - MathUtil.cos(Math.toRadians(incidenceAngle))) * 0.5;
			return windEffect.multiply(funnelFactor);
		}

		return windEffect;
	}

	private boolean isNearTunnel(Vec3d particlePosition) {
		var numberOfAirBlocks = 0;
		var numberOfSolidBlocks = 0;

		var world = getWorld();
		var blockPosition = BlockPos.ofFloored(particlePosition);

		for (var dx = -1; dx <= 1; dx++) {
			for (var dy = -1; dy <= 1; dy++) {
				for (var dz = -1; dz <= 1; dz++) {
					var checkPos = blockPosition.add(dx, dy, dz);
					var state = world.getBlockState(checkPos);

					if (state.isAir()) {
						numberOfAirBlocks++;
					} else if (state.isSolidBlock(getWorld(), checkPos)) {
						numberOfSolidBlocks++;
					}
				}
			}
		}

		return numberOfAirBlocks >= 15 && numberOfSolidBlocks >= 10;
	}

	private Vec3d adjustForTunnelAttraction(Vec3d windEffect, Vec3d particlePosition) {
		if (isNearTunnel(particlePosition)) {
			var attractionFactor = 1.5;
			return windEffect.multiply(attractionFactor);
		}

		return windEffect;
	}

	private Vec3d calculateRealisticWindFlow(Vec3d windEffect, Vec3d particlePosition) {
		if (checkForWallInteraction(particlePosition)) {
			windEffect = adjustWindFlow(windEffect, particlePosition, windEffect.x, windEffect.z);
		}

		windEffect = funnelWindAroundStructure(windEffect, particlePosition);
		return adjustForTunnelAttraction(windEffect, particlePosition);
	}

	private void updateHeatValue(Vec3d particlePosition) {
		var blockPosition = BlockPos.ofFloored(particlePosition);
		var maxHeatInfluenceDistance = 5.0;
		var heatValueIncrement = 0.05;

		// Reset heat value for the new tick
		setHeatValue(0.0);

		var checkPosition = new BlockPos.Mutable();
		checkPosition.set(blockPosition);

		for (; checkPosition.getY() >= 0; checkPosition.move(Direction.DOWN)) {
			var state = getWorld().getBlockState(checkPosition);

			if (isHeatSource(state)) {
				var destinationPos = new Vec3d(checkPosition.getX() + 0.5, checkPosition.getY() + 0.5, checkPosition.getZ() + 0.5);
				var distance = particlePosition.distanceTo(destinationPos);

				if (distance <= maxHeatInfluenceDistance) {
					var influence = (maxHeatInfluenceDistance - distance) / maxHeatInfluenceDistance;
					setHeatValue(getHeatValue() + heatValueIncrement * influence);

					var maxHeatValue = 1.0;

					// Cap the heat value to `maxHeatValue`.
					setHeatValue(Math.min(getHeatValue(), maxHeatValue));

					// Bail after finding the first heat source.
					break;
				}
			}
		}

		if (getHeatValue() > 0) {
			var motionY = calculateLift();
			setMotion(motionY);
		} else {
			setMotion(0);
		}
	}

	private double calculateLift() {
		// Multiply heatValue by a factor to determine lift strength
		return getHeatValue() * 0.1;
	}

	private boolean isHeatSource(BlockState state) {
		return state.isOf(Blocks.LAVA) || state.isOf(Blocks.FIRE) || state.isOf(Blocks.TORCH) || state.isOf(Blocks.CAMPFIRE);
	}

	private void setMotion(double motionY) {
		var position = getPosition();
		var updatedPosition = new Vec3d(position.getX(), position.getY() + motionY, position.getZ());

		setPosition(updatedPosition);
	}
}
