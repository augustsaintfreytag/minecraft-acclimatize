package thermite.therm.player;

public class PlayerState {

	public double bodyTemperature = 0;
	public double temperatureRate = 0;
	public double ambientTemperature = 0;
	public double ambientMinTemperature = 0;
	public double ambientMaxTemperature = 0;

	public String damageType = "";
	public int damageTick = 0;
	public int maxDamageTick = 10;
	public int searchFireplaceTick = 4;
	public int fireplaces = 0;

	public double baseWindTemperature = 0;
	public double windTemperature = 0;
	public double windTurbulence = 23;

}