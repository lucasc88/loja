package br.com.centraldaassinatura.loja.bean;

import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;

@Model
public class CompanyBean {

	public void isCnpjValid(FacesContext fc, UIComponent component, Object valueParam) throws ValidatorException {
		String cnpj = valueParam.toString();
		CNPJValidator validator = new CNPJValidator();
		try {
			validator.assertValid(cnpj);
		} catch (InvalidStateException e) {
			throw new ValidatorException(new FacesMessage("CNPJ Inválido"));
		}
	}
}
