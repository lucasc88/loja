package br.com.centraldaassinatura.loja.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.centraldaassinatura.loja.model.Category;

@FacesConverter("categoryConverter")
public class CategoryConverter implements Converter, Serializable {

	private static final long serialVersionUID = -7642022446063440492L;

	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		if (value != null && !value.trim().isEmpty() && !value.equals("Selecione uma")) {
			Category c = new Category();
			c.setId(Integer.valueOf(value));
			return c;
		}
		return null;
	}

	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		if (value != null) {
			Category c = (Category) value;
			return c.getId().toString();
		}
		return (String) value;
	}
}
