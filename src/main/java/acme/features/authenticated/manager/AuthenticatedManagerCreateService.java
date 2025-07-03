/*
 * AuthenticatedConsumerCreateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.authenticated.manager;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.Manager;

@GuiService
public class AuthenticatedManagerCreateService extends AbstractGuiService<Authenticated, Manager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedManagerRepository repository;

	// AbstractService<Authenticated, Consumer> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		if (super.getRequest().getMethod().equals("POST"))
			status = status && this.validateAircraft();

		super.getResponse().setAuthorised(status);
	}

	private boolean validateAircraft() {
		int aircraftId = super.getRequest().getData("airline", int.class);
		if (aircraftId != 0) {
			Airline aircraft = this.repository.findAirlineById(aircraftId);
			if (aircraft == null)
				return false;
		}
		return true;
	}

	@Override
	public void load() {
		Manager object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new Manager();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Manager object) {
		assert object != null;
		int airlineId;
		Airline airline;
		airlineId = super.getRequest().getData("airline", int.class);
		airline = this.repository.findAirlineById(airlineId);

		super.bindObject(object, "identifier", "yearsOfExperience", "dateOfBirth", "pictureUrl");
		object.setAirline(airline);
	}

	@Override
	public void validate(final Manager object) {
		assert object != null;
	}

	@Override
	public void perform(final Manager object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Manager object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "identifier", "yearsOfExperience", "dateOfBirth", "pictureUrl");

		SelectChoices selectedAirline = new SelectChoices();
		Collection<Airline> airlines = this.repository.findAllAirlines();
		selectedAirline = SelectChoices.from(airlines, "name", object.getAirline());
		dataset.put("airlines", selectedAirline);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
