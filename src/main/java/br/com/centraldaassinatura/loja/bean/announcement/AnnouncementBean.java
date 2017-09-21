package br.com.centraldaassinatura.loja.bean.announcement;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;

import br.com.centraldaassinatura.loja.dao.category.CategoryService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Category;
import br.com.centraldaassinatura.loja.model.Client;

@Named
@ViewScoped
public class AnnouncementBean implements Serializable {

	private static final long serialVersionUID = 7209807319329147237L;
	private List<Category> categories;
	private Category categorySelected;
	private Announcement announcement = new Announcement();
	private UploadedFile file;
	private boolean hasCompany;
	@Inject
	private CategoryService categoryService;

	@PostConstruct
	public void init(){
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userLogged");
		if (userLogged != null && userLogged.getCompany() != null) {
			hasCompany = true;
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
	public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
     
    public boolean isHasCompany() {
		return hasCompany;
	}

	public void setHasCompany(boolean hasCompany) {
		this.hasCompany = hasCompany;
	}

	public void upload() {
        if(file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}
}
