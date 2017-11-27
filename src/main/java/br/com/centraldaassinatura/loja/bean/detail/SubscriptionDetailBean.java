package br.com.centraldaassinatura.loja.bean.detail;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

import br.com.centraldaassinatura.loja.bean.cart.ShoppingCartBean;
import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.dao.secundaryImages.SecundaryImagesService;
import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.SecundaryImage;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class SubscriptionDetailBean implements Serializable {

	private static final long serialVersionUID = -955631322311075280L;
	@Inject
	private AnnouncementService announcementService;
	@Inject
	private SecundaryImagesService secundaryImagesService;
	@Inject
	private ClientService clientService;
	@Inject
	private ShoppingCartBean shoppingCartBean;
	private Client client = new Client();
	private Announcement ann;
	private Integer id;

	public void findById() {
		boolean c = secundaryImagesService.containsSecundaryImagesByAnnouncementId(id);
		if (c) {
			ann = announcementService.findByIdWithSecundaryImages(id);
		} else {
			ann = announcementService.findById(id);
			List<SecundaryImage> imgs = new ArrayList<>();
			SecundaryImage s = new SecundaryImage();
			s.setPath(ann.getPath());
			imgs.add(s);
			ann.setSecundaryImage(imgs);
		}
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

	public RedirectView redirectTologin() {
		return new RedirectView("login");
	}

	public void getExternalRedirect() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		try {
			externalContext.redirect(ann.getCompany().getSite());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String redirect(){
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLogged");
		if (userLogged == null) {
			return "/login.xhtml?faces-redirect=true&id=" + id;
		} else {
			System.out.println("Adicionando ao Carrinho o " + ann.getTitle());
			shoppingCartBean.add(id);
			return "/cart.xhtml";
		}
	}

	public void redirectToSellerPage(){
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + ann.getLinkPlan());
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(ann.getLinkPlan());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void redirectToCompanySite(String url){
		try {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + url);
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
