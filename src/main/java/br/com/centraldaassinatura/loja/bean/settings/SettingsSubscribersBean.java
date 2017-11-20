package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Subscription;
import br.com.centraldaassinatura.loja.model.SubscriptionsDataModel;

@Named
@ViewScoped
public class SettingsSubscribersBean implements Serializable {

	private static final long serialVersionUID = 7614944397289854257L;
	@Inject
	private AnnouncementService announcementService;
	@Inject
	private SubscriptionService subscriptionService;
//	@Inject
//	private SubscriptionsDataModel subscriptionsDataModel;
	private String uuId;
	private Announcement announcement = new Announcement();
	private List<Subscription> subscriptions = new ArrayList<>();

	public void findSubscribersByAnnouncementUuId() {
		System.out.println("!!!!!!!!!!!!! UuId: " + uuId);
		System.out.println("!!!!!!!!!!!!! subscriptionService: " + subscriptionService);
		announcement = announcementService.findByUuId(uuId);
		subscriptions = subscriptionService.findSubscriptionsByAgreementId(announcement.getId());
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

//	public SubscriptionsDataModel getSubscriptionsDataModel() {
//		return subscriptionsDataModel;
//	}
//
//	public void setSubscriptionsDataModel(SubscriptionsDataModel subscriptionsDataModel) {
//		this.subscriptionsDataModel = subscriptionsDataModel;
//	}

	public void saveTrackingCode() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("subscriptionId");
		System.out.println("############### subscriptionId: " + id);
	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();
		if (newValue != null && !newValue.equals(oldValue)) {
			Subscription s = (Subscription) ((DataTable) event.getComponent()).getRowData();
			System.out.println("############### " + s);
			System.out.println("############### " + newValue.toString());
			s.setTrackingCode(newValue.toString());
			subscriptionService.update(s);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo", "Cod. de Rastreio Salvo");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}
}
