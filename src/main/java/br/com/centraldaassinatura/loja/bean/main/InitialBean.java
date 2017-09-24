package br.com.centraldaassinatura.loja.bean.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.dao.category.CategoryService;
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.model.Category;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Model
public class InitialBean implements Serializable {

	private static final long serialVersionUID = 4548068412310165579L;
	@Inject
	private CategoryService categoryService;
	@Inject
	private ClientService clientService;
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

	public List<Category> getCategoriesSelected() {
		return categoriesSelected;
	}

	public void setCategoriesSelected(List<Category> categoriesSelected) {
		this.categoriesSelected = categoriesSelected;
	}
	
	public RedirectView redirectAnnouncement(){
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userLogged");
		userLogged = clientService.findById(userLogged.getId());
		if (userLogged != null && userLogged.getCompany() != null && userLogged.getCompany().getValid() == true) {
			return new RedirectView("/restrict/announcement");
		} else {
			return new RedirectView("/restrict/registerCompany");
		}
	}
}