package br.com.centraldaassinatura.loja.bean.company;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.centraldaassinatura.loja.dao.company.CompanyService;
import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.model.NaturesLegals;
import br.com.centraldaassinatura.loja.service.CepWebService;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class CompanyBean implements Serializable {

	private static final long serialVersionUID = -5955737954450419737L;
	private Company company = new Company();
	private NaturesLegals[] legalsNatures;
	private boolean showCompany = false;
	private boolean cepValid = false;
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

	public boolean isCepValid() {
		return cepValid;
	}

	public void setCepValid(boolean cepValid) {
		this.cepValid = cepValid;
	}

	public void isCnpjValid(FacesContext fc, UIComponent component, Object valueParam) throws ValidatorException {
		String cnpj = valueParam.toString();
		CNPJValidator validator = new CNPJValidator();
		try {
			validator.assertValid(cnpj);
		} catch (InvalidStateException e) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "CNPJ Inválido"));
		}
	}

	public void checkCNPJ() {
		Company c = CompanyWebService.getCompanyFromCNPJ(company.getCnpj());
		if (c == null) {
			setShowCompany(false);
			setCompany(new Company());
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "CNPJ Irregular",
					"Verifique a situação deste CNPJ na Receita Federal");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			if (cnpjExist(company.getCnpj())) {
				setShowCompany(false);
				setCompany(new Company());
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "CNPJ Já Cadastrado",
						"Este CNPJ já está cadastrado");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else {
				setShowCompany(true);
				setCompany(c);
			}
		}
	}

	private boolean cnpjExist(String cnpj) {
		Company c = companyService.findByCnpj(cnpj);
		if (c != null) {
			return true;
		}
		return false;
	}

	public String onFlowProcess(FlowEvent event) {
		if (!isShowCompany()) {
			return event.getOldStep();
		}
		if (!isCepValid()) {
			return event.getOldStep();
		}
		return event.getNewStep();
	}

	public RedirectView saveCompany() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getExternalContext().getFlash().setKeepMessages(true);
		fc.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Cadastro Efetuado", "Empresa Cadastrada com Sucesso!"));
		companyService.save(company);
		return new RedirectView("/index");
	}

	public void findCEP() {
		Address a = CepWebService.findAddress(company.getAddress());
		if (a == null) {// ConnectException
			RequestContext req = RequestContext.getCurrentInstance();
			req.execute("PF('connectionFailWid').show()");
			setCepValid(false);
		} else {
			if (a.getCep() == null) {
				RequestContext req = RequestContext.getCurrentInstance();
				req.execute("PF('addressNotFoundWid').show()");
				setCepValid(false);
			} else {
				setCepValid(true);
			}
		}
	}

	public void isCepValid(FacesContext fc, UIComponent component, Object valueParam) throws ValidatorException {
		String cep = valueParam.toString();
		company.getAddress().setCep(cep);
		Address a = CepWebService.findAddress(company.getAddress());
		try {
			if (a == null) {// ConnectException
				RequestContext req = RequestContext.getCurrentInstance();
				req.execute("PF('connectionFailWid').show()");
				setCepValid(false);
				throw new Exception();
			} else {
				if (a.getCep() == null) {
					RequestContext req = RequestContext.getCurrentInstance();
					req.execute("PF('addressNotFoundWid').show()");
					setCepValid(false);
					throw new Exception();
				} else {
					setCepValid(true);
				}
			}
		} catch (InvalidStateException e) {
			throw new ValidatorException(new FacesMessage("CEP Inválido"));
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage("CEP Inválido"));
		}
	}

	public void checkUrlFormat(FacesContext fc, UIComponent component, Object valueParam) throws ValidatorException {
		String url = valueParam.toString();
		if (!url.isEmpty()) {
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				throw new ValidatorException(new FacesMessage("Formato de URL inválido"));
			}
		}
	}
}
