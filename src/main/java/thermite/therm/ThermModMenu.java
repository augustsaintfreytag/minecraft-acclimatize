package thermite.therm;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;

public class ThermModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(ThermConfig.class, parent).get();
	}

}
