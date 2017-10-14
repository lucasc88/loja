package br.com.centraldaassinatura.loja.bean.announcement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.primefaces.event.FileUploadEvent;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.category.CategoryService;
import br.com.centraldaassinatura.loja.infra.FileSaver;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Category;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.model.SecundaryImage;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class AnnouncementBean implements Serializable {

	private static final long serialVersionUID = 7209807319329147237L;
	private static final FileSaver FS = new FileSaver();
	private List<Category> categories;
	private List<SecundaryImage> secundaryImages = new ArrayList<>();
	private Category categorySelected;
	private Announcement announcement = new Announcement();
	private Part mainImage;
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

	public Company getCompany() {
		return company;
	}

	public Part getMainImage() {
		return mainImage;
	}

	public void setMainImage(Part mainImage) {
		this.mainImage = mainImage;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void validator(FacesContext context, UIComponent component, Object value) {
		Part arquivo = (Part) value;
		if (arquivo.getSize() > 500000) {// 5MB
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Imagem muito grande",
					"A imagem deve ter tamanho máximo de 5MB.");
			throw new ValidatorException(msg);
		}
		if (!(arquivo.getContentType().equals("image/jpeg") || arquivo.getContentType().equals("image/png"))) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tipo inválido",
					"A imagem deve ser .jpg ou .png");
			throw new ValidatorException(msg);
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		SecundaryImage si = new SecundaryImage();
		si.setPath(FS.fileUploadEvent(event, company));
		si.setAnn(announcement);
		secundaryImages.add(si);
	}

	public RedirectView save() {
		announcement.setCompany(company);
		String relativePath = FS.write(mainImage, "imagesUploaded/companyId" + announcement.getCompany().getId());
		announcement.setPath(relativePath);
		if(!secundaryImages.isEmpty()){
			announcement.setSecundaryImage(secundaryImages);
		}
		announcementService.save(announcement);
		return new RedirectView("/index");
	}
}
