
package acme.features.any.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.entities.leg.Leg;

@Controller
public class AnyLegController extends AbstractGuiController<Any, Leg> {

	@Autowired
	protected AnyLegListService	listService;

	@Autowired
	protected AnyLegShowService	showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
