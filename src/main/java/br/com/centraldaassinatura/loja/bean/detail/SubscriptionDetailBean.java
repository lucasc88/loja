package br.com.centraldaassinatura.loja.bean.detail;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Client;

@Named
@ViewScoped
public class SubscriptionDetailBean implements Serializable{

	private static final long serialVersionUID = -955631322311075280L;
	@Inject
	private AnnouncementService announcementService;
	@Inject
	private ClientService clientService;
	private Client client = new Client();
	private Announcement ann;
	private Integer id;

	public void findById() {
		ann = announcementService.findByIdWithSecundaryImages(id);
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLogged");
		if (userLogged == null) {
			client.setAddress(new Address());
		} else {
			client = clientService.findById(userLogged.getId());
		}
	}

	public Announcement getAnn() {
		return ann;
	}

	public void setAnn(Announcement ann) {
		this.ann = ann;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}
}
