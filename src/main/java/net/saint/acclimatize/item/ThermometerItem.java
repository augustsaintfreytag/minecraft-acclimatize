package net.saint.acclimatize.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.saint.acclimatize.util.ServerStateUtil;

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

		user.sendMessage(Text
				.of("♜ Body: " + formattedValue(playerState.bodyTemperature) + " (↕ Acclim "
						+ formattedValue(playerState.temperatureRate)
						+ ") \n(☼ Ambient " + formattedValue(playerState.ambientTemperature)
						+ ", ♣ Biome " + formattedValue(playerState.biomeTemperature) + ", ☰ Wind "
						+ formattedValue(playerState.windTemperature)
						+ ", ♢ Blocks " + formattedValue(playerState.blockTemperature)
						+ ", ☍ Items " + formattedValue(playerState.itemTemperature) + ")"));

		return TypedActionResult.success(itemStack);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	private static String formattedValue(double value) {
		return String.format("%.3f", value);
	}

}
