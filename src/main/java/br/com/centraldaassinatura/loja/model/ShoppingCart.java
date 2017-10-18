package br.com.centraldaassinatura.loja.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import br.com.centraldaassinatura.loja.util.RedirectView;

@Named
@SessionScoped
public class ShoppingCart implements Serializable {

	private static final long serialVersionUID = -1379866240401823737L;
	private Set<CartItem> itens = new HashSet<>();

	public void add(CartItem c) {
		if (!itens.add(c)) {
			FacesMessage msg = new FacesMessage("Já Adicionado", "Este item já está no carrinho");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			FacesMessage msg = new FacesMessage("Adicionado!", "Item adicionado ao carrinho");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public List<CartItem> getItens() {
		return new ArrayList<CartItem>(itens);
	}

	public BigDecimal getTotal(CartItem item) {
		return item.getAnnouncement().getPrice().multiply(new BigDecimal(item.getQuantity()));
	}

	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (CartItem cartItem : itens) {
			total = total.add(cartItem.getAnnouncement().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
		}
		return total;
	}

	public void remove(CartItem item) {
		this.itens.remove(item);
	}

	public Integer getTotalAmount() {
		// sum total amount from each itens
		return itens.stream().mapToInt(item -> item.getQuantity()).sum();
	}

	public RedirectView redirectCheckout() {
		return new RedirectView("/restrict/checkout");
	}
	
	public RedirectView redirectLogin() {
		return new RedirectView("login");
	}
}
