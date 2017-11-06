package br.com.centraldaassinatura.loja.bean.checkout;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;
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
			setShipping(new BigDecimal(value.replace(',', '.')));
		}
		return event.getNewStep();
	}

	public BigDecimal getTotal(CartItem item) {
		return item.getAnnouncement().getPrice().multiply(new BigDecimal(item.getQuantity()));
	}

	public String sendPayment() {
		// for (CartItem cartItem : itens) {
		// persist Subscription
		// }
		// String contextName =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		// HttpServletResponse response = (HttpServletResponse)
		// FacesContext.getCurrentInstance().getExternalContext()
		// .getResponse();
		// // it allows a temporary redirect, it does not change the URL in the
		// browser
		// response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
		// // when the browser receives the redirect for that URL
		// (/service/payment)
		// // it will do the redirection maintaining the method that was
		// invoked,
		// // that is, it does the redirection while maintaining the current
		// call request
		// // it makes redicrect by Header Location
		// response.setHeader("Location", contextName +
		// "/services/payment?uuid="
		// + s.getUuId());
		// // it makes payment using asynchronous way

		// remove from cart
		String[] urlAndPaymentId = gateway.newAgreement(user, itemToBeFinalized.getAnnouncement(), getShipping());
		String url = urlAndPaymentId[0];
		String agreementId = urlAndPaymentId[1];
		// ao chegar em paymentsuccess.xhtml ativar a assinatura e atualiza-la
		Subscription s = new Subscription(agreementId, "PENDING", gateway.extractedTokenFromURL(url), user,
				itemToBeFinalized.getAnnouncement(), getShipping());
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

	public void geraToken() {
		System.out.println(
				"@@@@@@@@@@ " + gateway.refreshToken(itemToBeFinalized.getAnnouncement().getCompany().getClientId(),
						itemToBeFinalized.getAnnouncement().getCompany().getClientSecret()));
	}
}
