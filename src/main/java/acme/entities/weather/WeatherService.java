
package acme.entities.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import acme.entities.airport.Airport;

@Service
public class WeatherService {

	private static final String	API_KEY	= "OPENWEATHERMAP_API_KEY";
	private static final String	API_URL	= "https://api.openweathermap.org/data/2.5/weather";


	public WeatherConditions getWeatherForAirport(final Airport airport) {
		try {
			String urlString = WeatherService.API_URL + "?q=" + airport.getCity() + "&appid=" + WeatherService.API_KEY + "&units=metric"; // Consider using airport.getCity()
			URI uri = URI.create(urlString);

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				WeatherData weatherData = mapper.readValue(response.body(), WeatherData.class);

				WeatherConditions weatherConditions = this.createWeatherConditions(airport, weatherData);
				return weatherConditions;
			} else
				throw new IOException("Failed to fetch weather data: " + response.statusCode() + " - " + response.body());

		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Error fetching weather data", e);
		}
	}

	private WeatherConditions createWeatherConditions(final Airport airport, final WeatherData weatherData) {
		WeatherConditions weatherConditions = new WeatherConditions();
		weatherConditions.setAirport(airport);
		weatherConditions.setTemperature(weatherData.getMain().getTemp());
		weatherConditions.setWindSpeed(weatherData.getWind().getSpeed());
		if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
			weatherConditions.setConditions(weatherData.getWeather().get(0).getMain());
			weatherConditions.setDescription(weatherData.getWeather().get(0).getDescription());
			weatherConditions.setIconCode(weatherData.getWeather().get(0).getIcon());
		}
		weatherConditions.setHumidity(weatherData.getMain().getHumidity());
		weatherConditions.setPressure(weatherData.getMain().getPressure());
		weatherConditions.setWindDirection(weatherData.getWind().getDeg());
		weatherConditions.setTimestamp(LocalDateTime.ofEpochSecond(weatherData.getDt(), 0, ZoneOffset.UTC));
		return weatherConditions;
	}
}
