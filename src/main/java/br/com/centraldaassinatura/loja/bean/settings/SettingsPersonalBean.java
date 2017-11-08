package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.service.CepWebService;

@Named
@ViewScoped
public class SettingsPersonalBean implements Serializable {

	private static final long serialVersionUID = -4007792113947668901L;
	@Inject
	private ClientService usuerService;
	private Client user = new Client();
	private Address address = new Address();

	public void findUserById(Integer id) {
		user = usuerService.findByIdWithSubscriptions(id);
		address = user.getAddress();
	}

	public Client getUser() {
		return user;
	}

	public void setUser(Client user) {
		this.user = user;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void findCEP() {
		Address a = CepWebService.findAddress(address);
		if (a == null) {// ConnectException
			RequestContext req = RequestContext.getCurrentInstance();
			req.execute("PF('connectionFailWid').show(); PF('statusDialog').hide();");
		} else {
			address = a;
			if (address.getCep() == null) {
				RequestContext req = RequestContext.getCurrentInstance();
				req.execute("PF('addressNotFoundWid').show(); PF('statusDialog').hide();");
			}
		}
	}

	public void saveAddress() {
		user.setAddress(address);
		usuerService.update(user);
		;
	}

	public String edit() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("agreementId");
		System.out.println("AgreementId: " + id);
		return "/restrict/settingsSubscription.xhtml?faces-redirect=true&id=" + id;
	}
}
