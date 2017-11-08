package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;
import br.com.centraldaassinatura.loja.model.Subscription;

@Named
@ViewScoped
public class SettingsSubscriptionBean implements Serializable{

	private static final long serialVersionUID = -1777879655266447098L;
	@Inject
	private SubscriptionService subscriptionService;
	private Subscription subscription = new Subscription();
	private String id;
	
	public void findSubscriptionByAgreementId(){
		System.out.println("@@@@@@@@@@ findSubscriptionByAgreementId(): " + id);
		subscription = subscriptionService.findByAgreementId(id);
		System.out.println(subscription);
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
}
