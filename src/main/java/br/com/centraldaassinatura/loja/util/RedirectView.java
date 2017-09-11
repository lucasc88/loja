package br.com.centraldaassinatura.loja.util;

public class RedirectView {

	private String viewName;

	public RedirectView(String viewName) {
		this.viewName = viewName;
	}

	@Override
	public String toString() {
		return viewName + "?faces-redirect=true";
	}
	
}
