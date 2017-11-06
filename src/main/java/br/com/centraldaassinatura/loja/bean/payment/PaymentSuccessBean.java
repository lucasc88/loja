package br.com.centraldaassinatura.loja.bean.payment;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.model.Subscription;
import br.com.centraldaassinatura.loja.service.GatewayPayPal;

@Named
@ViewScoped
public class PaymentSuccessBean implements Serializable {

	private static final long serialVersionUID = -4340919481595020223L;
	@Inject
	private GatewayPayPal gateway;
	@Inject
	private SubscriptionService subscriptionService;
	private String token;
	private Subscription subscription = new Subscription();
	private Announcement announcement = new Announcement();
	private Company company = new Company();

	@PostConstruct
	public void init() {
		announcement.setCompany(company);
		subscription.setAnnouncement(announcement);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void executeAgreement() {
		if (token != null) {
			subscription = subscriptionService.findByToken(token);
			String[] agreementIdAndState = gateway.activeAgreement(
					subscription.getAnnouncement().getCompany().getClientId(),
					subscription.getAnnouncement().getCompany().getClientSecret(), token);
			subscription.setAgreementId(agreementIdAndState[0]);
			subscription.setState(agreementIdAndState[1]);
			subscriptionService.update(subscription);
		}
	}

}
