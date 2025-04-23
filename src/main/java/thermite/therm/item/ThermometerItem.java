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
import thermite.therm.server.ServerState;
import thermite.therm.util.EnvironmentalTemperatureUtil;

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

		// Check if user is player
		if (!(user instanceof PlayerEntity)) {
			return TypedActionResult.pass(itemStack);
		}

		var player = (ServerPlayerEntity) user;
		var server = world.getServer();
		var serverState = ServerState.getServerState(server);
		var playerState = ServerState.getPlayerState(player);

		user.sendMessage(Text.of("Body: " + playerState.bodyTemperature + " (Ambient " + playerState.ambientTemperature
				+ ", Min " + playerState.ambientMinTemperature + ", Max " + playerState.ambientMaxTemperature
				+ ", Env " + EnvironmentalTemperatureUtil.temperatureDeltaForEnvironment(player) + ")"));

		return TypedActionResult.success(itemStack);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

}
