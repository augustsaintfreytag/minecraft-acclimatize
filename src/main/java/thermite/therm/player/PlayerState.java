package thermite.therm.player;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class PlayerState extends PersistentState {

	// Properties

	public double bodyTemperature = 0;
	public double temperatureRate = 0;
	public double ambientTemperature = 0;

	public double biomeTemperature = 0;
	public double blockTemperature = 0;
	public double itemTemperature = 0;
	public double windTemperature = 0;

	public int damageTick = 0;
	public int damageTickDuration = 10;

	// NBT

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putDouble(PlayerStateNBTKeys.bodyTemperature, bodyTemperature);
		nbt.putDouble(PlayerStateNBTKeys.temperatureRate, temperatureRate);
		nbt.putDouble(PlayerStateNBTKeys.ambientTemperature, ambientTemperature);
		nbt.putDouble(PlayerStateNBTKeys.biomeTemperature, biomeTemperature);
		nbt.putDouble(PlayerStateNBTKeys.blockTemperature, blockTemperature);
		nbt.putDouble(PlayerStateNBTKeys.itemTemperature, itemTemperature);
		nbt.putDouble(PlayerStateNBTKeys.windTemperature, windTemperature);

		nbt.putInt(PlayerStateNBTKeys.damageTick, damageTick);
		nbt.putInt(PlayerStateNBTKeys.damageTickDuration, damageTickDuration);

		return nbt;
	}

	public static PlayerState fromNbt(NbtCompound nbt) {
		PlayerState playerState = new PlayerState();

		playerState.bodyTemperature = nbt.getDouble(PlayerStateNBTKeys.bodyTemperature);
		playerState.temperatureRate = nbt.getDouble(PlayerStateNBTKeys.temperatureRate);
		playerState.ambientTemperature = nbt.getDouble(PlayerStateNBTKeys.ambientTemperature);
		playerState.biomeTemperature = nbt.getDouble(PlayerStateNBTKeys.biomeTemperature);
		playerState.blockTemperature = nbt.getDouble(PlayerStateNBTKeys.blockTemperature);
		playerState.itemTemperature = nbt.getDouble(PlayerStateNBTKeys.itemTemperature);
		playerState.windTemperature = nbt.getDouble(PlayerStateNBTKeys.windTemperature);

		playerState.damageTick = nbt.getInt(PlayerStateNBTKeys.damageTick);
		playerState.damageTickDuration = nbt.getInt(PlayerStateNBTKeys.damageTickDuration);

		return playerState;
	}
}