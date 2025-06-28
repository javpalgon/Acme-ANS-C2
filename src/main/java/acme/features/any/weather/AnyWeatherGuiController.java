
package acme.features.any.weather;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.weather.Weather;

@GuiController
public class AnyWeatherGuiController extends AbstractGuiController<Any, Weather> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherListService	anyWeatherListService;

	@Autowired
	private AnyWeatherShowService	anyWeatherShowService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.anyWeatherListService);
		super.addBasicCommand("show", this.anyWeatherShowService);
	}

}
