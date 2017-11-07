package br.com.centraldaassinatura.loja.bean.login;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.service.CepWebService;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = -5842651762845034157L;
	@Inject
	private ClientService usuerService;
	private boolean newUser;
	private boolean userAlreadyExists;
	private boolean skip;
	private boolean showPasswordField;
	private String password;
	private Client user = new Client();
	private Address address = new Address();

	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	public boolean isUserAlreadyExists() {
		return userAlreadyExists;
	}

	public void setUserAlreadyExists(boolean userAlreadyExists) {
		this.userAlreadyExists = userAlreadyExists;
	}

	public Client getUser() {
		return user;
	}

	public void setUser(Client user) {
		this.user = user;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public boolean isShowPasswordField() {
		return showPasswordField;
	}

	public void setShowPasswordField(boolean showPasswordField) {
		this.showPasswordField = showPasswordField;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void checkUser() {
		Client c = usuerService.findByEmail(user.getEmail());
		if (c != null) {
			setUserAlreadyExists(true);
			setShowPasswordField(true);
		} else {
			setNewUser(true);
		}
	}

	public void save() {
		user.setAddress(address);
		usuerService.save(user);
		setUserAlreadyExists(true);
		FacesMessage msg = new FacesMessage("Salvo com Sucesso", "Já pode logar " + user.getEmail());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}

	public RedirectView login() {
		Client c = usuerService.findByEmail(user.getEmail());
		if (c.getPassword() != null && c.getPassword().equals(user.getPassword())) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userLogged", c);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getExternalContext().getFlash().setKeepMessages(true);
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Logado com Sucesso!",
					"Bem vindo " + user.getEmail()));
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Dados Incorretos", "Tente novamente"));
			return new RedirectView("");
		}
		return new RedirectView("index");
	}

	public RedirectView logout() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("userLogged");
		return new RedirectView("/index");
	}

	public RedirectView redirectTologin() {
		return new RedirectView("login");
	}

	public void findCEP() {
		Address a = CepWebService.findAddress(address);
		if (a == null) {//ConnectException
			RequestContext req = RequestContext.getCurrentInstance();
			req.execute("PF('connectionFailWid').show()");
		} else {
			address = a;
			if (address.getCep() == null) {
				RequestContext req = RequestContext.getCurrentInstance();
				req.execute("PF('addressNotFoundWid').show()");
			}
		}
	}
}
