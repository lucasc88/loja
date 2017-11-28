package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.service.GatewayPayPal;

@Named
@ViewScoped
public class SettingsAnnouncementBean implements Serializable{

	private static final long serialVersionUID = 194235791203104340L;
	@Inject
	private AnnouncementService announcementService;
	@Inject
	private GatewayPayPal gateway;
	private String uuId;
	private Announcement ann;

	public void findAnnouncementByUuId(){
		System.out.println("!!!!!!!!!!!!!!! UuId: " + uuId);
		ann = announcementService.findByUuId(uuId);
		if(ann.getPlanId() != null && !ann.getPlanId().isEmpty()){
			String status = gateway.showAnnouncementDetails(ann.getCompany().getClientId(),
					ann.getCompany().getClientSecret(),
					ann.getPlanId());
			System.out.println("!!!!!!!!!!!!!!! status do plano: " + status);
			if(!ann.getState().equalsIgnoreCase(status)){
				ann.setState(status);
				announcementService.update(ann);
			}
		}
	}

	public Announcement getAnn() {
		return ann;
	}

	public void setAnn(Announcement ann) {
		this.ann = ann;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public void cancelAnnouncement(){
		if(ann.getPlanId() != null && !ann.getPlanId().isEmpty()){
			String state = gateway.cancelPlan(ann.getCompany().getClientId(), ann.getCompany().getClientSecret(), ann.getPlanId());
			System.out.println("@@@@@@@@@@@@@@@@@@@@ status cancelado? " + state);
			ann.setState(state);
			announcementService.update(ann);
		} else {
			ann.setState("INACTIVE");
			announcementService.update(ann);
		}
		RequestContext.getCurrentInstance().update("formSettings");
	}
}
