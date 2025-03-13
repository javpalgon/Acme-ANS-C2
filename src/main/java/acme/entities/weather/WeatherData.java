
package acme.entities.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {

	private Coord			coord;
	private List<Weather>	weather;
	private String			base;
	private Main			main;
	private Integer			visibility;
	private Wind			wind;
	private Clouds			clouds;
	private Long			dt;
	private Sys				sys;
	private Integer			id;
	private String			name;
	private Integer			cod;


	@Getter
	@Setter
	public static class Coord {

		private Double	lon; // Longitude
		private Double	lat; // Latitude
	}

	@Getter
	@Setter
	public static class Main {

		private Double	temp; // Temperature in Kelvin
		private Double	pressure; // Atmospheric pressure in hPa
		private Double	humidity; // Humidity in %
		private Double	temp_min;
		private Double	temp_max;
		@JsonProperty("feels_like")
		private Double	feelsLike; // Added feels like temperature
		private Integer	sea_level; // Added sea level pressure
		private Integer	grnd_level; // Added ground level pressure
	}

	@Getter
	@Setter
	public static class Wind {

		private Double	speed; // Wind speed in meter/sec
		private Double	deg; // Wind direction in degrees
		private Double	gust; // Added wind gust
	}

	@Getter
	@Setter
	public static class Weather {

		private Integer	id; // Weather condition id
		private String	main; // Group of weather parameters (Rain, Snow, Extreme etc.)
		private String	description; // Weather condition within the group
		private String	icon; // Weather icon id
	}

	@Getter
	@Setter
	public static class Clouds {

		private Integer all; // Cloudiness in %
	}

	@Getter
	@Setter
	public static class Sys {

		private Integer	type;
		private Integer	id;
		private String	country;
		private Long	sunrise; // Sunrise time, Unix timestamp
		private Long	sunset; // Sunset time, Unix timestamp
	}
	
	/*
	{
	   "coord":{
	      "lon":-0.13,
	      "lat":51.51
	   },
	   "weather":[
	      {
	         "id":300,
	         "main":"Drizzle",
	         "description":"light intensity drizzle",
	         "icon":"09d"
	      }
	   ],
	   "base":"stations",
	   "main":{
	      "temp":280.32,
	      "pressure":1012,
	      "humidity":81,
	      "temp_min":279.15,
	      "temp_max":281.15
	   },
	   "visibility":10000,
	   "wind":{
	      "speed":4.1,
	      "deg":80
	   },
	   "clouds":{
	      "all":90
	   },
	   "dt":1485789600,
	   "sys":{
	      "type":1,
	      "id":5091,
	      "message":0.0103,
	      "country":"GB",
	      "sunrise":1485762037,
	      "sunset":1485794875
	   },
	   "id":2643743,
	   "name":"London",
	   "cod":200
	}
	*/

}
