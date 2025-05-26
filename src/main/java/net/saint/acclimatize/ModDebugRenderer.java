package net.saint.acclimatize;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.saint.acclimatize.data.space.SunShadeTemperatureUtil;

public final class ModDebugRenderer {

	public static void renderSunVectorDebug(MinecraftClient client) {
		var player = client.player;
		var world = client.world;
		var sunPosition = SunShadeTemperatureUtil.sunPositionFromWorld(world);
		var startVector = new Vec3d(player.getX(), player.getY() + 1.0, player.getZ());
		var endVector = startVector.add(sunPosition.multiply(100));

		SunShadeTemperatureUtil.renderSunVectorDebug(world, startVector, endVector);
	}

}
