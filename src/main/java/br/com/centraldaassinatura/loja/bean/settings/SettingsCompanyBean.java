package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.dao.company.CompanyService;
import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.service.CepWebService;

@Named
@ViewScoped
public class SettingsCompanyBean implements Serializable {

	private static final long serialVersionUID = 3851757163663738636L;
	@Inject
	private ClientService usuerService;
	@Inject
	private CompanyService companyService;
	@Inject
	private AnnouncementService announcementService;
	private Client user = new Client();
	private Address address = new Address();
	private List<Announcement> announcement = new ArrayList<>();

	public void findUserById(Integer id) {
		user = usuerService.findById(id);
		address = user.getCompany().getAddress();
		announcement = announcementService.allAnnouncementByCompanyId(user.getCompany().getId());
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

	public List<Announcement> getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(List<Announcement> announcement) {
		this.announcement = announcement;
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
		user.getCompany().setAddress(address);
		companyService.update(user.getCompany());
	}
	
	public String editAnnouncement(){
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementUuId");
		System.out.println("AnnouncementUuId: " + id);
		return "/restrict/settingsAnnouncement.xhtml?faces-redirect=true&id=" + id;
	}
	
	public String announcementSubscriptions(){
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementUuId");
		System.out.println("AnnouncementUuId: " + id);
		return "";
	}
}
