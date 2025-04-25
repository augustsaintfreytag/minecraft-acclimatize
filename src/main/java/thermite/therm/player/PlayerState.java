package thermite.therm.player;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class PlayerState extends PersistentState {

	// Properties

	public double bodyTemperature = 0;
	public double temperatureRate = 0;
	public double ambientTemperature = 0;
	public double ambientMinTemperature = 0;
	public double ambientMaxTemperature = 0;

	public String damageType = "";
	public int damageTick = 0;
	public int maxDamageTick = 10;

	public double baseWindTemperature = 0;
	public double windTemperature = 0;

	// NBT

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putDouble("temperature", bodyTemperature);
		nbt.putDouble("temperatureRate", temperatureRate);
		nbt.putDouble("restingTemperature", ambientTemperature);
		nbt.putDouble("minTemperature", ambientMinTemperature);
		nbt.putDouble("maxTemperature", ambientMaxTemperature);

		nbt.putString("damageType", damageType);
		nbt.putInt("damageTick", damageTick);
		nbt.putInt("maxDamageTick", maxDamageTick);

		nbt.putDouble("baseWindTemperature", baseWindTemperature);
		nbt.putDouble("windTemperature", windTemperature);

		return nbt;
	}

	public static PlayerState fromNbt(NbtCompound nbt) {
		PlayerState playerState = new PlayerState();

		playerState.bodyTemperature = nbt.getDouble("temperature");
		playerState.temperatureRate = nbt.getDouble("temperatureRate");
		playerState.ambientTemperature = nbt.getDouble("restingTemperature");
		playerState.ambientMinTemperature = nbt.getDouble("minTemperature");
		playerState.ambientMaxTemperature = nbt.getDouble("maxTemperature");

		playerState.damageType = nbt.getString("damageType");
		playerState.damageTick = nbt.getInt("damageTick");
		playerState.maxDamageTick = nbt.getInt("maxDamageTick");

		playerState.baseWindTemperature = nbt.getDouble("baseWindTemperature");
		playerState.windTemperature = nbt.getDouble("windTemperature");

		return playerState;
	}
}