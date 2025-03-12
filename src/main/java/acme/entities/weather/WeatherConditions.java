
package acme.entities.weather;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import acme.entities.airport.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WeatherConditions extends AbstractEntity implements Serializable {

	private static final long	serialVersionUID	= 1L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "airport_id")
	private Airport				airport;

	private Double				temperature;
	private Double				windSpeed;
	private String				conditions;
	private String				description;
	private Double				humidity;
	private LocalDateTime		timestamp;
	private String				iconCode;
	private Double				pressure;
	private Double				windDirection;

	/*
	 * {
	 * "coord":{
	 * "lon":-0.13,
	 * "lat":51.51
	 * },
	 * "weather":[
	 * {
	 * "id":300,
	 * "main":"Drizzle",
	 * "description":"light intensity drizzle",
	 * "icon":"09d"
	 * }
	 * ],
	 * "base":"stations",
	 * "main":{
	 * "temp":280.32,
	 * "pressure":1012,
	 * "humidity":81,
	 * "temp_min":279.15,
	 * "temp_max":281.15
	 * },
	 * "visibility":10000,
	 * "wind":{
	 * "speed":4.1,
	 * "deg":80
	 * },
	 * "clouds":{
	 * "all":90
	 * },
	 * "dt":1485789600,
	 * "sys":{
	 * "type":1,
	 * "id":5091,
	 * "message":0.0103,
	 * "country":"GB",
	 * "sunrise":1485762037,
	 * "sunset":1485794875
	 * },
	 * "id":2643743,
	 * "name":"London",
	 * "cod":200
	 * }
	 */

}
