package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.dao.client.ClientService;
import br.com.centraldaassinatura.loja.dao.company.CompanyService;
import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;
import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.Subscription;
import br.com.centraldaassinatura.loja.service.CepWebService;

@Named
@ViewScoped
public class SettingsCompanyBean implements Serializable {

	private static final long serialVersionUID = 3851757163663738636L;
	@Inject
	private ClientService usuerService;
	@Inject
	private CompanyService companyService;
	@Inject
	private SubscriptionService subscriptionService;
	@Inject
	private AnnouncementService announcementService;
	private LineChartModel zoomModel;
	private Client user = new Client();
	private Address address = new Address();
	private List<Announcement> announcement = new ArrayList<>();

	public void findUserById(Integer id) {
		System.out.println("!!!!!!!!!&&&&&&&&&!!!! chamou findUserById(Integer id)");
		user = usuerService.findById(id);
		address = user.getCompany().getAddress();
		announcement = announcementService.allAnnouncementByCompanyId(user.getCompany().getId());
		System.out.println("$$$$$$$$$$$$$$$ findUserById " + announcement.size());
	}
	
    @PostConstruct
    public void init() {
    	System.out.println("!!!!!!!!&&&&&&&&&&!!!!!! chamou @PostConstruct ");
    	userLogged();
    	createZoomModel();
    }

	private void userLogged() {
		FacesContext context = FacesContext.getCurrentInstance();
		Client userLogged = (Client) context.getExternalContext().getSessionMap().get("userLogged");
		if (userLogged != null) {
			user = usuerService.findById(userLogged.getId());
			announcement = announcementService.allAnnouncementByCompanyId(user.getCompany().getId());		
		}
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
    
	public List<Announcement> getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(List<Announcement> announcement) {
		this.announcement = announcement;
	}

	public void findCEP() {
		Address a = CepWebService.findAddress(address);
		if (a == null) {// ConnectException
			RequestContext req = RequestContext.getCurrentInstance();
			req.execute("PF('connectionFailWid').show(); PF('statusDialog').hide();");
		} else {
			address = a;
			if (address.getCep() == null) {
				RequestContext req = RequestContext.getCurrentInstance();
				req.execute("PF('addressNotFoundWid').show(); PF('statusDialog').hide();");
			}
		}
	}

	public void saveAddress() {
		user.getCompany().setAddress(address);
		companyService.update(user.getCompany());
	}
	
	public String editAnnouncement(){
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementUuId");
		return "/restrict/settingsAnnouncement.xhtml?faces-redirect=true&id=" + id;
	}
	
	public String announcementSubscriptions(){
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementUuId");
		return "/restrict/settingsSubscribers.xhtml?faces-redirect=true&id=" + id;
	}
	
    public LineChartModel getZoomModel() {
        return zoomModel;
    }
    
    private void createZoomModel() {
    	zoomModel = new LineChartModel();
    	System.out.println("################ chamou createZoomModel()");
    	System.out.println("tamanho " + announcement.size());
//    	for (Announcement a : announcement) {
//    		LineChartSeries announcement1 = new LineChartSeries();
//    		announcement1.setLabel(a.getTitle());
//    		System.out.println("################# IdDoPlano: " + a.getId());
//    		List<Subscription> assinaturas = subscriptionService.findSubscriptionsByAgreementIdActiveOrderDate(a.getId());
//    		System.out.println("tamanho assinaturas " + assinaturas.size());
//    		for (Subscription subscription : assinaturas) {
//    			System.out.println("!!!!!!!!!!!!!!!! dia: " + subscription.getPaymentStartDate());
//    			announcement1.set(subscription.getPaymentStartDate().toString(), 1);
//			}
//    		System.out.println("!!!!!!!!!!!!!!!!! adiciounu ao gráfico: " + announcement1.getLabel());
//    		zoomModel.addSeries(announcement1);
//		}
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Vinhos do Mundo");
        series1.set("2017-11-01", 1);
        series1.set("2017-11-06", 2);
        series1.set("2017-11-12", 1);
        series1.set("2017-11-18", 3);
        zoomModel.addSeries(series1);
        
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Livros Históricos");
        series2.set("2017-11-02", 2);
        series2.set("2017-11-05", 1);
        series2.set("2017-11-08", 1);
        series2.set("2017-11-15", 2);
        series2.set("2017-11-19", 1);
        zoomModel.addSeries(series2);
         
        zoomModel.setTitle("Planos de Assinatura Vendidos");
        zoomModel.setZoom(true);
        zoomModel.setLegendPosition("e");
        zoomModel.getAxis(AxisType.Y).setLabel("Quantidade");
        DateAxis axis = new DateAxis("Dias");
        axis.setTickAngle(-50);
        axis.setMax("2017-11-20");
        axis.setTickFormat("%#d %b %y");
         
        zoomModel.getAxes().put(AxisType.X, axis);
    }
    
}
