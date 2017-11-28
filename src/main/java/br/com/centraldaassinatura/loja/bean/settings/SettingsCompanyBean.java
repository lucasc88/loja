package br.com.centraldaassinatura.loja.bean.settings;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
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

	public String editAnnouncement() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementUuId");
		return "/restrict/settingsAnnouncement.xhtml?faces-redirect=true&id=" + id;
	}

	public String announcementSubscriptions() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("announcementUuId");
		return "/restrict/settingsSubscribers.xhtml?faces-redirect=true&id=" + id;
	}

	public LineChartModel getZoomModel() {
		return zoomModel;
	}

	private void createZoomModel() {
		zoomModel = new LineChartModel();
		for (Announcement a : announcement) {
			if (a.getState().equals("ACTIVE")) {// somente ACTIVE
				LineChartSeries series = new LineChartSeries();
				series.setLabel(a.getTitle());
				System.out.println("################# IdDoPlano: " + a.getId());
				List<Subscription> subs = subscriptionService.findSubscriptionsByAgreementIdActiveOrderDate(a.getId());
				System.out.println("tamanho assinaturas " + subs.size());
				if (subs != null && !subs.isEmpty()) {
					buildDatas(series, subs);
				} else {
					String d = ZonedDateTime.now().toString();
					series.set(d.substring(0, d.indexOf("T")), 0);
				}
				System.out.println("!!!!!!!!!!!!!!!!! adiciounu ao gráfico: " + series.getLabel());
				zoomModel.addSeries(series);
			}
		}
		zoomModel.setTitle("Planos de Assinatura Vendidos");
		zoomModel.setZoom(true);
		zoomModel.setLegendPosition("e");
		zoomModel.getAxis(AxisType.Y).setLabel("Quantidade");
		DateAxis axis = new DateAxis("Dias");
		axis.setTickAngle(-50);
		String s = ZonedDateTime.now().plus(1, ChronoUnit.DAYS).toString();
		axis.setMax(s.substring(0, s.indexOf("T")));
		axis.setTickFormat("%#d %b %y");

		zoomModel.getAxes().put(AxisType.X, axis);
	}

	private void buildDatas(LineChartSeries series, List<Subscription> subs) {
		int cont = 0;
		Map<String, Integer> map = new HashMap<>();
		String tempDate = "";
		for (Subscription s : subs) {//2 - 21/11 ...... 1 - 22/11
			if(!s.getPaymentStartDate().toString().equals(tempDate)){
				tempDate = s.getPaymentStartDate().toString();
				cont = 0;
			}
			map.put(s.getPaymentStartDate().toString(), ++cont);
		}
		for (Map.Entry<String, Integer> pair : map.entrySet()) {
			series.set(pair.getKey(), pair.getValue());
		}
	}
}
