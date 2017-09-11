package br.com.centraldaassinatura.loja.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.dao.category.CategoryService;
import br.com.centraldaassinatura.loja.model.Category;

@Model
public class InitialBean implements Serializable {

	private static final long serialVersionUID = 4548068412310165579L;
	@Inject
	private CategoryService categoryService;
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
}