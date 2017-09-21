package br.com.centraldaassinatura.loja.bean.company;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.centraldaassinatura.loja.dao.company.CompanyService;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.model.NaturesLegals;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class CompanyBean implements Serializable {

	private static final long serialVersionUID = -5955737954450419737L;
	private Company company = new Company();
	private NaturesLegals[] legalsNatures;
	private boolean showCompany = false;
	@Inject
	private CompanyService companyService;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public NaturesLegals[] getLegalsNatures() {
		if (legalsNatures == null) {
			legalsNatures = NaturesLegals.values();
		}
		return legalsNatures;
	}

	public void setLegalsNatures(NaturesLegals[] naturesLegals) {
		this.legalsNatures = naturesLegals;
	}

	public boolean isShowCompany() {
		return showCompany;
	}

	public void setShowCompany(boolean showCompany) {
		this.showCompany = showCompany;
	}

	public void isCnpjValid(FacesContext fc, UIComponent component, Object valueParam) throws ValidatorException {
		String cnpj = valueParam.toString();
		CNPJValidator validator = new CNPJValidator();
		try {
			validator.assertValid(cnpj);
		} catch (InvalidStateException e) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, null, "CNPJ Inválido"));
		}
	}

	public void checkCNPJ() {
		Company c = CompanyWebService.getCompanyFromCNPJ(company.getCnpj());
		if (c == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "CNPJ Irregular",
					"Verifique a situação deste CNPJ na receita");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			setShowCompany(true);
			setCompany(c);
		}
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}
	
	public RedirectView saveCompany(){
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getExternalContext().getFlash().setKeepMessages(true);
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa Cadastrada com Sucesso!", null));
		companyService.save(company);
		return new RedirectView("/index");
	}
}
