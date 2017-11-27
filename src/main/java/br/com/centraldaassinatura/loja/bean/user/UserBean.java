package br.com.centraldaassinatura.loja.bean.user;

import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.model.Client;

@Model
public class UserBean {
	
	@Inject
	private ClientService usuerService;

	public void isCpfValid(FacesContext fc, UIComponent component, Object valueParam) throws ValidatorException {
		String cpf = valueParam.toString();
		CPFValidator validator = new CPFValidator();
		try {
			validator.assertValid(cpf);
			Client c = usuerService.checkCpf(cpf);
			if(c != null){
				throw new Exception();
			}
		} catch (InvalidStateException e) {
			throw new ValidatorException(new FacesMessage("CPF Inválido"));
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage("CPF já existente"));
		}
	}
}
