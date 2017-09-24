package br.com.centraldaassinatura.loja.bean.announcement;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.category.CategoryService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Category;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class AnnouncementBean implements Serializable {

	private static final long serialVersionUID = 7209807319329147237L;
	private List<Category> categories;
	private Category categorySelected;
	private Announcement announcement = new Announcement();
//	private UploadedFile file;
	private Part images;
	private Company company;
	@Inject
	private CategoryService categoryService;
	@Inject
	private AnnouncementService announcementService;

	@PostConstruct
	public void init() {
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLogged");
		if (userLogged != null && userLogged.getCompany() != null) {
			company = userLogged.getCompany();
		}
	}

	public List<Category> getCategories() {
		if (categories == null) {
			categories = categoryService.allCategories();
		}
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public Category getCategorySelected() {
		return categorySelected;
	}

	public void setCategorySelected(Category categorySelected) {
		this.categorySelected = categorySelected;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

//	public UploadedFile getFile() {
//		return file;
//	}
//
//	public void setFile(UploadedFile file) {
//		this.file = file;
//	}

	public Company getCompany() {
		return company;
	}

	public Part getImages() {
		return images;
	}

	public void setImages(Part images) {
		this.images = images;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

//	public void upload(FileUploadEvent event) {
//		UploadedFile uploadedFile = event.getFile();
//	}

	public RedirectView save() {
		try {
			String path = "/imagens/" + images.getSubmittedFileName();
			//save in the disk
			images.write(path);
			announcement.setPath(path);
			announcement.setCompany(company);
			announcementService.save(announcement);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new RedirectView("/index");
	}
}
