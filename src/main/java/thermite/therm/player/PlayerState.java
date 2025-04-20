package thermite.therm.player;

public class PlayerState {

	public double bodyTemperature = 50;
	public double temperatureRate = 0.0625;
	public double ambientTemperature = 404;
	public double ambientMinTemperature = -400;
	public double ambientMaxTemperature = 400;

	public String damageType = "";
	public int damageTick = 0;
	public int maxDamageTick = 10;
	public int searchFireplaceTick = 4;
	public int fireplaces = 0;

	public double baseWindTemperature = 0;
	public double windTemperature = 0;
	public double windTurbulence = 23;

}