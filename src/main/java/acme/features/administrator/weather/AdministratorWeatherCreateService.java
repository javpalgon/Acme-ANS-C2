
package acme.features.administrator.weather;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.weather.Weather;

@GuiService
public class AdministratorWeatherCreateService extends AbstractGuiService<Administrator, Weather> {

	@Autowired
	AdministratorWeatherRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Weather object = new Weather();
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Weather weather) {
		super.bindObject(weather, "city");
	}

	@Override
	public void perform(final Weather weather) {
		String city = weather.getCity();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String apiKey = "62e356cc8bb311af587f8f38be91ea5c";
			String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", city, apiKey);
			@SuppressWarnings("unchecked")
			Map<String, Object> response = restTemplate.getForObject(url, Map.class);

			weather.setTimestamp(new Date()); // or use dt from API if needed

			// weather[0]
			if (response.containsKey("weather")) {
				Object[] weatherArr = ((java.util.List<Object>) response.get("weather")).toArray();
				if (weatherArr.length > 0) {
					Map<String, Object> weather0 = (Map<String, Object>) weatherArr[0];
					weather.setWeatherMain((String) weather0.getOrDefault("main", ""));
					weather.setWeatherDescription((String) weather0.getOrDefault("description", ""));
				}
			}

			weather.setVisibility((Integer) response.getOrDefault("visibility", 10000));

			if (response.containsKey("wind")) {
				Map<String, Object> wind = (Map<String, Object>) response.get("wind");
				Object speedObj = wind.get("speed");
				if (speedObj instanceof Number)
					weather.setWindSpeed(((Number) speedObj).doubleValue());
			}

			String main = weather.getWeatherMain();
			boolean isBad = main != null && (main.equalsIgnoreCase("Rain") || main.equalsIgnoreCase("Snow") || main.equalsIgnoreCase("Thunderstorm") || main.equalsIgnoreCase("Drizzle"));
			weather.setIsBadWeather(isBad);
		} catch (Exception e) {
			// fallback: set default values
			weather.setTimestamp(new Date());
			weather.setWeatherMain("Clear");
			weather.setWeatherDescription("clear sky");
			weather.setVisibility(10000);
			weather.setWindSpeed(2.0);
			weather.setIsBadWeather(false);
		}
		this.repository.save(weather);
	}

	@Override
	public void validate(final Weather weather) {
		;
	}

	@Override
	public void unbind(final Weather weather) {
		Dataset dataset;
		dataset = super.unbindObject(weather, "city");
		super.getResponse().addData(dataset);
	}

}
