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
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.infra.FileSaver;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Category;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.model.SecundaryImage;
import br.com.centraldaassinatura.loja.service.GatewayPayPal;
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
	@Inject
	private ClientService clientService;
	@Inject
	private GatewayPayPal gateway;

	@PostConstruct
	public void init() {
		company = findCompanyByUserLogged();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!" + company);
	}

	private Company findCompanyByUserLogged() {
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLogged");
		userLogged = clientService.findById(userLogged.getId());
		if (userLogged != null && userLogged.getCompany() != null) {
			return userLogged.getCompany();
		} else {
			return null;
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

	public void setCompany(Company company) {
		this.company = company;
	}

	public Part getMainImage() {
		return mainImage;
	}

	public void setMainImage(Part mainImage) {
		this.mainImage = mainImage;
	}

	public void validatorGraterThan0(FacesContext context, UIComponent component, Object value) {
		Double cycleD = null;
		Integer cycleInt = null;
		if (value instanceof Double) {
			cycleD = (Double) value;
		} else if (value instanceof Integer) {
			cycleInt = (Integer) value;
		}
		if (cycleD != null && cycleD <= 0) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor Inválido",
					"Valor deve ser maior que 0");
			throw new ValidatorException(msg);
		} else if (cycleInt != null && cycleInt <= 0) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor Inválido",
					"Valor deve ser maior que 0");
			throw new ValidatorException(msg);
		}
	}

	public void validatorWidth(FacesContext context, UIComponent component, Object value) {
		Integer width = (Integer) value;
		if (width != null && width > 105) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Largura Inválida",
					"Largura deve ser menor que 106");
			throw new ValidatorException(msg);
		} 
		if(width != null && width < 11){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Largura Inválida",
					"Largura deve ser maior que 10");
			throw new ValidatorException(msg);
		}
	}
	
	public void validatorHeight(FacesContext context, UIComponent component, Object value) {
		Integer height = (Integer) value;
		if (height != null && height > 105) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Altura Inválida",
					"Altura deve ser menor que 106");
			throw new ValidatorException(msg);
		} 
		if(height != null && height < 2){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Altura Inválida",
					"Altura deve ser maior que 1");
			throw new ValidatorException(msg);
		}
	}
	
	public void validatorLength(FacesContext context, UIComponent component, Object value) {
		Integer length = (Integer) value;
		if (length != null && length > 105) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Comprimento Inválido",
					"Comprimento deve ser menor que 106");
			throw new ValidatorException(msg);
		} 
		if(length != null && length < 16){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Comprimento Inválido",
					"Comprimento deve ser maior que 15");
			throw new ValidatorException(msg);
		}
	}

	public void validator(FacesContext context, UIComponent component, Object value) {
		Part arquivo = (Part) value;
		if (arquivo.getSize() > 5000000) {// 5MB
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
		if (company == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Empresa NULL", "Empresa NULL");
			throw new ValidatorException(msg);
		}
		SecundaryImage si = new SecundaryImage();
		System.out.println("!!!!!!!!!!!!!!!!!! " + company);
		si.setPath(FS.fileUploadEvent(event, company.getId()));
		si.setAnn(announcement);
		secundaryImages.add(si);
	}

	public RedirectView save() {
		announcement.setCompany(company);
		String relativePath = FS.write(mainImage, "imagesUploaded/companyId" + announcement.getCompany().getId());
		announcement.setPath(relativePath);
		if (!secundaryImages.isEmpty()) {
			announcement.setSecundaryImage(secundaryImages);
		}
		System.out.println("Chamou gateWay pra Criar Plano !!!!!!!!!!!!!!!!!!!!!!!!!");
		String planIdAndChargeId = gateway.createPlan(announcement);
		System.out.println("Id do Plano e Id do frete: " + planIdAndChargeId);
		announcement.setPlanId(planIdAndChargeId.substring(0, planIdAndChargeId.indexOf(" ")));
		announcement.setChargeModelIdShipping(
				planIdAndChargeId.substring(planIdAndChargeId.indexOf(" ") + 1, planIdAndChargeId.length()));
		announcement.setState("ACTIVE");
		announcementService.save(announcement);
		return new RedirectView("/index");
	}
}
