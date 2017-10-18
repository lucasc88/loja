package br.com.centraldaassinatura.loja.bean.checkout;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

import br.com.centraldaassinatura.loja.model.CartItem;
import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.ShoppingCart;

@Named
@ViewScoped
public class CheckoutBean implements Serializable {

	private static final long serialVersionUID = -6916680887680904813L;
	@Inject
	private ShoppingCart shoppingCart;
	private Client user = new Client();
	private List<CartItem> itens = new ArrayList<>();

	// The dependencies are injected after construction
	@PostConstruct
	public void init() {
		itens = shoppingCart.getItens();
		user = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userLogged");
	}

	public List<CartItem> getItens() {
		return itens;
	}

	public Client getUser() {
		return user;
	}

	public void setUser(Client user) {
		this.user = user;
	}

	public void setItens(List<CartItem> itens) {
		this.itens = itens;
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}

	public BigDecimal getTotal(CartItem item) {
		return item.getAnnouncement().getPrice().multiply(new BigDecimal(item.getQuantity()));
	}

	public void getPayPal() {
		//here you have Client, Anouncements and yours Companies, Itens from cart
		for (CartItem cartItem : itens) {
			System.out.println(cartItem.getAnnouncement().getTitle() + ", " + cartItem.getQuantity() + " from Company: "
					+ cartItem.getAnnouncement().getCompany().getNameFantasy());
		}
		System.out.println("Client: " + user.getName());
		System.out.println(" Call PayPal here! ");
	}
}
