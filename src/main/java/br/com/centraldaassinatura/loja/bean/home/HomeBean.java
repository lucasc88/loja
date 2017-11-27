package br.com.centraldaassinatura.loja.bean.home;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.category.CategoryService;
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Category;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class HomeBean implements Serializable {

	private static final long serialVersionUID = 4548068412310165579L;
	@Inject
	private CategoryService categoryService;
	@Inject
	private AnnouncementService announcementService;
	@Inject
	private ClientService clientService;
	private List<Announcement> announcements;
	private List<Announcement> bestSellerAnnouncements;
	private List<Category> categories;
	private List<Category> categoriesSelected = new ArrayList<>();
	private String keyWord = "";
	private boolean indexPage = false;

	public List<Category> getCategories() {
		if (categories == null) {
			categories = categoryService.allCategories();
		}
		return categories;
	}

	public List<Announcement> getAnnouncements() {
		if (announcements == null) {
			announcements = announcementService.lastAnnouncements();
		}
		return announcements;
	}

	public void setAnnouncements(List<Announcement> announcements) {
		this.announcements = announcements;
	}

	public List<Announcement> getBestSellerAnnouncements() {
		if (bestSellerAnnouncements == null) {
			bestSellerAnnouncements = announcementService.bestSellerAnnouncements();
		} else {
			bestSellerAnnouncements = announcementService.lastAnnouncements();
		}
		return bestSellerAnnouncements;
	}

	public void setBestSellerAnnouncements(List<Announcement> bestSellerAnnouncements) {
		this.bestSellerAnnouncements = bestSellerAnnouncements;
	}

	public List<Category> getCategoriesSelected() {
		return categoriesSelected;
	}

	public void setCategoriesSelected(List<Category> categoriesSelected) {
		this.categoriesSelected = categoriesSelected;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public RedirectView redirectAnnouncement() {
		Client userLogged = findUserLogged();
		if (userLogged != null && userLogged.getCompany() != null && userLogged.getCompany().getValid() == true) {
			return new RedirectView("/restrict/announcement");
		} else {
			return new RedirectView("/restrict/registerCompany");
		}
	}

	public RedirectView redirectSettings() {
		return new RedirectView("/restrict/settingsPersonal");
	}

	public RedirectView redirectSettingsCompany() {
		return new RedirectView("/restrict/settingsCompany");
	}

	public boolean userHasCompany(Integer id) {
		Client c = clientService.findById(id);
		return c.getCompany() != null;
	}

	private Client findUserLogged() {
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLogged");
		userLogged = clientService.findById(userLogged.getId());
		return userLogged;
	}

	public String details() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementId");
		return "/assinatura-detalhe.xhtml?faces-redirect=true&id=" + id;
	}

	public void updatePlans() {
		System.out.println(categoriesSelected.size());
		List<Integer> ids = new ArrayList<>();
		if (categoriesSelected.size() == 0) {
			announcements = announcementService.lastAnnouncements();
			return;
		} else {
			for (Category category : categoriesSelected) {
				ids.add(category.getId());
			}
		}
		System.out.println("mandou pro DAO: " + ids.size());
		announcements = announcementService.findByCategories(ids);
	}

	public String findByKeyWord() {
		String nomePagina = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		if (nomePagina.contains("index")) {
			categoriesSelected.clear();
			if (keyWord != null && keyWord.isEmpty()) {
				announcements = announcementService.lastAnnouncements();
				RequestContext.getCurrentInstance().update("formMain:announcements");
				RequestContext.getCurrentInstance().update("formMain:grid2");
				return "";
			}
			System.out.println("Executou o find " + keyWord);
			announcements = announcementService.findByKeyWord(keyWord);
			RequestContext.getCurrentInstance().update("formMain:announcements");
			RequestContext.getCurrentInstance().update("formMain:grid2");
			return "";
		} else {
			FacesMessage msg = new FacesMessage("Aviso", "Buscas somente na página inicial");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return "";
		}
	}
	
	public boolean isIndexPage() {
		String nomePagina = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		if(nomePagina.contains("index")){
			indexPage = true;
		}
		return indexPage;
	}
	
	public void redirectToSellerPage(String url){
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}