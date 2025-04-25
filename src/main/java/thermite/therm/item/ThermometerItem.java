package thermite.therm.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import thermite.therm.util.EnvironmentalTemperatureUtil;
import thermite.therm.util.ItemTemperatureUtil;
import thermite.therm.util.ServerStateUtil;

public class ThermometerItem extends Item {

	public ThermometerItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		var itemStack = user.getStackInHand(hand);

		if (world.isClient()) {
			return TypedActionResult.success(itemStack);
		}

		if (!(user instanceof PlayerEntity)) {
			return TypedActionResult.pass(itemStack);
		}

		var player = (ServerPlayerEntity) user;
		var playerState = ServerStateUtil.getPlayerState(player);
		var bodyTemperature = formatTemperature(playerState.bodyTemperature);
		var ambientTemperature = formatTemperature(playerState.ambientTemperature);
		var ambientMinTemperature = formatTemperature(playerState.ambientMinTemperature);
		var ambientMaxTemperature = formatTemperature(playerState.ambientMaxTemperature);
		var environmentalTemperature = formatTemperature(
				EnvironmentalTemperatureUtil.temperatureDeltaForEnvironment(player));
		var itemTemperature = formatTemperature(ItemTemperatureUtil.temperatureDeltaForAllArmorItems(player));

		user.sendMessage(Text
				.of("♜ Body: " + bodyTemperature + " (☼ Ambient " + ambientMinTemperature + " < " + ambientTemperature
						+ " > " + ambientMaxTemperature + ", ♢ Env " + environmentalTemperature + ", ☵ Items "
						+ itemTemperature + ")"));

		return TypedActionResult.success(itemStack);
	}

	private static String formatTemperature(double temperature) {
		return String.format("%.1f", temperature);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

}
