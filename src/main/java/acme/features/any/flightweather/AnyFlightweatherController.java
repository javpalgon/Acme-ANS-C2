package acme.features.any.flightweather;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.entities.flight.Flight;

@Controller
public class AnyFlightweatherController extends AbstractGuiController<Any, Flight> {

	@Autowired
	protected AnyFlightweatherListService listService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-weather", "list", this.listService);
	}

}
