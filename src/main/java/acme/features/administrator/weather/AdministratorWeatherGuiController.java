
package acme.features.administrator.weather;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.weather.Weather;

@GuiController
public class AdministratorWeatherGuiController extends AbstractGuiController<Administrator, Weather> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorWeatherListService		administratorWeatherListService;

	@Autowired
	private AdministratorWeatherShowService		administratorWeatherShowService;

	@Autowired
	private AdministratorWeatherCreateService	administratorWeatherCreateService;
	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.administratorWeatherListService);
		super.addBasicCommand("show", this.administratorWeatherShowService);
		super.addBasicCommand("create", this.administratorWeatherCreateService);
	}

}
