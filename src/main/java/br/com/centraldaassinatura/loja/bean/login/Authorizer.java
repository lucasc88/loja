package br.com.centraldaassinatura.loja.bean.login;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import br.com.centraldaassinatura.loja.model.Client;

public class Authorizer implements PhaseListener {

	private static final long serialVersionUID = 1785396939481713032L;

	@Override
	public void afterPhase(PhaseEvent evento) {
		FacesContext context = evento.getFacesContext();
		String nomePagina = context.getViewRoot().getViewId();
		if (nomePagina.equals("/login.xhtml") || nomePagina.equals("/index.xhtml")) {
			return;
		}
		Client userLogged = (Client) context.getExternalContext().getSessionMap().get("userLogged");
		if (userLogged != null) {
			return;
		}
		NavigationHandler handler = context.getApplication().getNavigationHandler();
		handler.handleNavigation(context, null, "/login?faces-redirect=true");
		context.renderResponse();
	}

	@Override
	public void beforePhase(PhaseEvent event) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
