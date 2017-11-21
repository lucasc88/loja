package br.com.centraldaassinatura.loja.bean.checkout;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.CartItem;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.ShoppingCart;
import br.com.centraldaassinatura.loja.model.Subscription;
import br.com.centraldaassinatura.loja.service.CepWebService;
import br.com.centraldaassinatura.loja.service.GatewayPayPal;

@Named
@ViewScoped
public class CheckoutBean implements Serializable {

	private static final long serialVersionUID = -6916680887680904813L;
	@Inject
	private ShoppingCart shoppingCart;
	@Inject
	private GatewayPayPal gateway;
	@Inject
	private SubscriptionService subscriptionService;
	private String type;
	private BigDecimal shipping;
	private Client user = new Client();
	private List<CartItem> itens;
	private CartItem itemToBeFinalized;

	// The dependencies are injected after construction
	@PostConstruct
	public void init() {
		itemToBeFinalized = shoppingCart.getItemToBeFinalized();
		itens = new ArrayList<>();
		itens.add(itemToBeFinalized);
		user = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userLogged");
	}

	public CartItem getItemToBeFinalized() {
		return itemToBeFinalized;
	}

	public void setItemToBeFinalized(CartItem itemToBeFinalized) {
		this.itemToBeFinalized = itemToBeFinalized;
	}

	public Client getUser() {
		return user;
	}

	public void setUser(Client user) {
		this.user = user;
	}

	public List<CartItem> getItens() {
		return itens;
	}

	public void setItens(List<CartItem> itens) {
		this.itens = itens;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	public String onFlowProcess(FlowEvent event) {
		System.out.println(" step: " + event.getNewStep());
		if (event.getNewStep().equals("confirm")) {
			String value = CepWebService.findShippingValue(
					itemToBeFinalized.getAnnouncement().getCompany().getAddress().getCep(), user.getAddress().getCep(),
					type, itemToBeFinalized.getAnnouncement().getHeightBox(),
					itemToBeFinalized.getAnnouncement().getWidthBox(),
					itemToBeFinalized.getAnnouncement().getLengthBox(),
					itemToBeFinalized.getAnnouncement().getWeightBox());
			System.out.println("Valor do Frete: R$ " + value);
			setShipping(new BigDecimal(value.replace(',', '.')));
		}
		return event.getNewStep();
	}

	public BigDecimal getTotal(CartItem item) {
		return item.getAnnouncement().getPrice().multiply(new BigDecimal(item.getQuantity()));
	}

	public String sendPayment() {
		String[] datas = gateway.newAgreement(user, itemToBeFinalized.getAnnouncement(), getShipping());
		String url = datas[0];
		String agreementId = datas[1];
		String paymentStartDate = datas[2];
		Date payStartDate = convertStringToDate(paymentStartDate);
		Date payLastDate = lastDateByAnnouncement(paymentStartDate, itemToBeFinalized.getAnnouncement());

		// ao chegar em paymentsuccess.xhtml ativar a assinatura e atualiza-la
		Subscription s = new Subscription(agreementId, "PENDING", gateway.extractedTokenFromURL(url), user,
				itemToBeFinalized.getAnnouncement(), payStartDate, payLastDate, getShipping());
		subscriptionService.save(s);
		shoppingCart.remove(itemToBeFinalized);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		try {
			externalContext.redirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return url;
	}

	private Date lastDateByAnnouncement(String paymentStartDate, Announcement announcement) {
		Date date = null;
		int qtd = announcement.getCycles();
		if(qtd == 0){//infinite cycle
			return date;
		}
		// DAY, WEEK, MONTH, YEAR
		String frequency = announcement.getFrequency();
		System.out.println("Cálculo último dia: frequenci: " + frequency);
		Instant instant = null;
		switch (frequency) {
		case "DAY":
			instant = ZonedDateTime.now().plus(qtd, ChronoUnit.DAYS).toInstant();
			break;
		case "WEEK":
			instant = ZonedDateTime.now().plus(qtd, ChronoUnit.WEEKS).toInstant();
			break;
		case "MONTH":
			instant = ZonedDateTime.now().plus(qtd, ChronoUnit.MONTHS).toInstant();
//			ZonedDateTime.now().plus(qtd, ChronoUnit.MONTHS);
			break;
		case "YEAR":
			instant = ZonedDateTime.now().plus(qtd, ChronoUnit.YEARS).toInstant();
			break;
		default:
			instant = ZonedDateTime.now().toInstant();
			break;
		}
		date = Date.from(instant);
		return date;
	}

	private Date convertStringToDate(String paymentStartDate) {
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateInString = paymentStartDate.substring(0, 10);
			date = formatter.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}
