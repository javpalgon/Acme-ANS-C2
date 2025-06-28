
package acme.features.any.weather;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.weather.Weather;

@GuiService
public class AnyWeatherListService extends AbstractGuiService<Any, Weather> {

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
		//		if (city == null || city.isBlank())
		//			city = "Seville";
		//		List<Weather> weathers = this.weatherService.findWeathersByCity(city);

		//		super.getBuffer().addData(weathers);
		List<Weather> weathers = this.repository.findAllWeathers();
		super.getBuffer().addData(weathers);

	}

	@Override
	public void unbind(final Weather weather) {
		Dataset dataset = super.unbindObject(weather, "city", "timestamp", "weatherMain", "weatherDescription", "visibility", "windSpeed", "isBadWeather");
		//	dataset.put("cityName", weather.getCity());
		super.getResponse().addData(dataset);
	}
}
