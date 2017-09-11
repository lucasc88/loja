package br.com.centraldaassinatura.loja.bean.user;

import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

@Model
public class UserBean {

	public void isCpfValid(FacesContext fc, UIComponent component, Object valueParam) throws ValidatorException {
		String cpf = valueParam.toString();
		CPFValidator validator = new CPFValidator();
		try {
			validator.assertValid(cpf);
		} catch (InvalidStateException e) {
			throw new ValidatorException(new FacesMessage("CPF Inválido"));
		}
	}
}
