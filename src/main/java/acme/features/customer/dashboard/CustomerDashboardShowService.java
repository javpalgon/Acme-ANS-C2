
package acme.features.customer.dashboard;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.Travelclass;
import acme.forms.CustomerDashboard;
import acme.forms.Statistics;
import acme.realms.Customer;

@GuiService
public class CustomerDashboardShowService extends AbstractGuiService<Customer, CustomerDashboard> {

	@Autowired
	private CustomerDashboardRepository repository;


	@Override
	public void authorise() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		Customer customer = this.repository.findCustomerByUserAccountId(userAccountId);
		boolean isAuthorised = customer != null;

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		//The last five destinations. 
		String[] lastFiveDestinations = this.repository.findLastFiveDestinations(customerId).stream().limit(5).map(f -> f.getDestinationCity()).toArray(String[]::new);

		//The money spent in bookings during the last year.
		Collection<Booking> bookings = this.repository.findBookingsByCustomerId(customerId);
		Date dateLastYear = Date.from(MomentHelper.getCurrentMoment().toInstant().atZone(ZoneId.systemDefault()).minusYears(1).toInstant());

		Map<String, Double> totalMoneySpentLastYearByCurrency = bookings.stream().filter(x -> {
			return x.getPurchaseMoment() != null && x.getPurchaseMoment().after(dateLastYear) && x.getPrice() != null && x.getPrice().getAmount() != null && x.getPrice().getCurrency() != null;
		}).collect(Collectors.groupingBy(x -> x.getPrice().getCurrency(), Collectors.summingDouble(x -> x.getPrice().getAmount())));

		//Their number of bookings grouped by travel class.
		List<Object[]> results = this.repository.findNumberOfBookingsGroupedByTravelClass(customerId);

		Map<String, Integer> bookingsPerTravelClass = results.stream().collect(Collectors.toMap(entry -> ((Travelclass) entry[0]).name(), entry -> ((Long) entry[1]).intValue()));

		//Count, average, minimum, maximum, and standard deviation of the cost of their bookings in the last five years.
		Date fiveYearsAgo = Date.from(MomentHelper.getCurrentMoment().toInstant().atZone(ZoneId.systemDefault()).minusYears(5).toInstant());

		Map<String, Statistics> bookingCostStatsLastFiveYears = bookings.stream().filter(b -> b.getPurchaseMoment() != null && b.getPurchaseMoment().after(fiveYearsAgo))
			.filter(b -> b.getPrice() != null && b.getPrice().getAmount() != null && b.getPrice().getCurrency() != null)
			.collect(Collectors.groupingBy(b -> b.getPrice().getCurrency(), Collectors.collectingAndThen(Collectors.mapping(b -> b.getPrice().getAmount(), Collectors.toList()), amounts -> {
				Statistics stats = new Statistics();
				stats.setData(amounts);
				return stats;
			})));

		//	Count, average, minimum, maximum, and standard deviation of the number of pas-sengers in their bookings.

		Statistics passengersStatistics = new Statistics();
		passengersStatistics.setData(this.repository.findBookingRecordCountsPerBooking(customerId).stream().map(Long::doubleValue).toList());

		// Build dashboard
		CustomerDashboard dashboard = new CustomerDashboard();
		dashboard.setLastFiveDestinations(lastFiveDestinations);
		dashboard.setTotalMoneySpentLastYearByCurrency(totalMoneySpentLastYearByCurrency);
		dashboard.setBookingsPerTravelClass(bookingsPerTravelClass);
		dashboard.setBookingCostStatsLastFiveYears(bookingCostStatsLastFiveYears);
		dashboard.setPassengersStatistics(passengersStatistics);

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final CustomerDashboard object) {
		assert object != null;

		Dataset dataset = super.unbindObject(object, "lastFiveDestinations", "totalMoneySpentLastYearByCurrency", "bookingsPerTravelClass", "bookingCostStatsLastFiveYears", "passengersStatistics");

		super.getResponse().addData(dataset);
	}

}
