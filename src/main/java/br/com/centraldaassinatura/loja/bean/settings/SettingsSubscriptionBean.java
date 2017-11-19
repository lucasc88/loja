package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;
import br.com.centraldaassinatura.loja.model.Subscription;
import br.com.centraldaassinatura.loja.service.GatewayPayPal;

@Named
@ViewScoped
public class SettingsSubscriptionBean implements Serializable{

	private static final long serialVersionUID = -1777879655266447098L;
	@Inject
	private SubscriptionService subscriptionService;
	@Inject
	private GatewayPayPal gateway;
	private String type;
	private Subscription subscription = new Subscription();
	private String id;
	
	public void findSubscriptionByAgreementId(){
		subscription = subscriptionService.findByUuId(id);
		String status = gateway.showAgreementDetails(subscription.getAnnouncement().getCompany().getClientId(),
				subscription.getAnnouncement().getCompany().getClientSecret(),
				subscription.getAgreementId());
		subscription.setState(status);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void cancelSubscription(){
		gateway.cancelSubscription(subscription.getAnnouncement().getCompany().getClientId(),
				subscription.getAnnouncement().getCompany().getClientSecret(),
				subscription.getAgreementId());
		subscription.setState("Cancelled");
		subscriptionService.update(subscription);
	}
}
