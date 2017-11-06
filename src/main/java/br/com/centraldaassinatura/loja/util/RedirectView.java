package br.com.centraldaassinatura.loja.util;

public class RedirectView {

	private String viewName;

	public RedirectView(String viewName) {
		if(viewName != null && !viewName.equals("")){
			this.viewName = viewName + "?faces-redirect=true";
		} else {
			this.viewName = "";
		}
	}

	@Override
	public String toString() {
		return viewName;
	}
}
