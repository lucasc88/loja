package br.com.centraldaassinatura.loja.bean.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
	private List<Category> categories;
	private List<Category> categoriesSelected = new ArrayList<>();

	public void action() {
		System.out.println("button clicked!");
	}

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

	public List<Category> getCategoriesSelected() {
		return categoriesSelected;
	}

	public void setCategoriesSelected(List<Category> categoriesSelected) {
		this.categoriesSelected = categoriesSelected;
	}

	public RedirectView redirectAnnouncement() {
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLogged");
		userLogged = clientService.findById(userLogged.getId());
		if (userLogged != null && userLogged.getCompany() != null && userLogged.getCompany().getValid() == true) {
			return new RedirectView("/restrict/announcement");
		} else {
			return new RedirectView("/restrict/registerCompany");
		}
	}

	public String details() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementId");
		return "/assinatura-detalhe.xhtml?faces-redirect=true&id=" + id;
	}
}