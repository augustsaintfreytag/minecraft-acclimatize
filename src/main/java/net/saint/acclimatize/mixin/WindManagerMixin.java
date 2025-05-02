package net.saint.acclimatize.mixin;

import java.util.concurrent.atomic.AtomicInteger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.World;
import net.saint.acclimatize.util.ServerStateUtil;
import net.saint.acclimatize.util.WindTemperatureUtil;
import net.vibzz.immersivewind.wind.WindManager;

@Mixin(WindManager.class)
public abstract class WindManagerMixin {

	@Shadow
	private static volatile float currentWindDirection;

	@Shadow
	private static volatile float targetWindDirection;

	@Shadow
	private static AtomicInteger currentWindStrength;

	@Shadow
	private static AtomicInteger targetWindStrength;

	@Inject(method = "updateWindBasedOnWeather", at = @At("Tail"))
	private static void updateWindBasedOnWeatherMixin(World world, CallbackInfo callbackInfo) {
		var server = world.getServer();
		var serverState = ServerStateUtil.getServerState(server);

		WindTemperatureUtil.overrideWind(serverState, currentWindDirection);
	}

}
