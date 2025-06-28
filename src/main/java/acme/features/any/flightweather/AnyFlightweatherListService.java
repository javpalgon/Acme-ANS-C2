
package acme.features.any.flightweather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.weather.Weather;
import acme.features.any.weather.AnyWeatherRepository;

@GuiService
public class AnyFlightweatherListService extends AbstractGuiService<Any, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Flight> flights = new ArrayList<>();

		// Get current moment and calculate date from 30 days ago
		Date currentMoment = MomentHelper.getCurrentMoment();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentMoment);
		cal.add(Calendar.DAY_OF_MONTH, -30);
		Date startDate = cal.getTime();

		// Get all published flights from last month
		List<Flight> publishedFlights = this.repository.findAllPublishedFlightsLastMonth(startDate);

		// Get all bad weather records from last month
		List<Weather> badWeathers = this.repository.findAllBadWeathersLastMonth(startDate);

		// Filter flights that have bad weather conditions at their origin city on the same date
		flights = publishedFlights.stream().filter(flight -> this.hasBadWeatherAtOriginOnSameDate(flight, badWeathers)).collect(Collectors.toList());

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "description", "isDraftMode");
		super.getResponse().addData(dataset);
	}

	// Ancillary methods ------------------------------------------------------

	private boolean hasBadWeatherAtOriginOnSameDate(final Flight flight, final List<Weather> badWeathers) {
		// Get the origin city and departure date from the first leg
		String originCity = flight.getOriginCity();
		Date departureDate = flight.getDeparture();

		// Check if there's bad weather for this city on the same date
		return badWeathers.stream().anyMatch(weather -> weather.getCity().equals(originCity) && this.isSameDate(weather.getTimestamp(), departureDate));
	}

	private boolean isSameDate(final Date date1, final Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

}
