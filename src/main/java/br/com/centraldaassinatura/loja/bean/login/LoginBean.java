package br.com.centraldaassinatura.loja.bean.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@ViewScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = -5842651762845034157L;
	@Inject
	private ClientService usuerDao;
	private boolean newUser;
	private boolean userAlreadyExists;
	private boolean skip;
	private boolean showPasswordField;
	private String password;
	private Client user = new Client();
	private Company company = new Company();

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
		System.out.println("setUserAlreadyExists(" + userAlreadyExists + ")");
		this.userAlreadyExists = userAlreadyExists;
	}

	public Client getUser() {
		return user;
	}

	public void setUser(Client user) {
		this.user = user;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void checkUser() {
		System.out.println(user.getEmail());
		Client c = usuerDao.findByEmail(user.getEmail());
		System.out.println("achou? " + (c != null));
		if (c != null) {
			setUserAlreadyExists(true);
			setShowPasswordField(true);
		} else {
			setNewUser(true);
		}
	}

	public void save() {
		usuerDao.save(user);
		setUserAlreadyExists(true);
		FacesMessage msg = new FacesMessage("Salvo com Sucesso", "Já pode logar " + user.getEmail());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public String onFlowProcess(FlowEvent event) {
		if (!skip) {
			// reset in case user goes back
			skip = false;
			user.setCompany(null);
			company = new Company();
			if (event.getNewStep().equals("company") && event.getOldStep().equals("confirm")
					&& !isUserAlreadyExists()) {
				return "personal";
			}
			return "confirm";
		} else {
			List<Company> list = new ArrayList<>();
			list.add(company);
			user.setCompany(list);
			if (!isUserAlreadyExists()) {
				return event.getNewStep();
			}
			return "confirm";
		}
	}

	public RedirectView login() {
		System.out.println("Fez Login!!!!!!!!!!");
		Client c = usuerDao.findByEmail(user.getEmail());
		if (c.getPassword() != null && c.getPassword().equals(user.getPassword())) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userLogged", this.user);
		}
		return new RedirectView("index");
	}

	public RedirectView logout() {
		System.out.println("Fez Logout!!!!!!!!!!");
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("userLogged");
		return new RedirectView("/index");
	}

	public RedirectView redirectTologin() {
		System.out.println("redirecionou p/ login");
		return new RedirectView("index");
	}
}
