package br.com.centraldaassinatura.loja.bean.cart;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.CartItem;
import br.com.centraldaassinatura.loja.model.ShoppingCart;

@Model
public class ShoppingCartBean {

	@Inject
	private AnnouncementService announcementService;
	@Inject
	private ShoppingCart shoppingCart;
	
	public void add(Integer id) {
		Announcement ann = announcementService.findById(id);
		CartItem ci = new CartItem(ann);
		System.out.println("Adicionando ao Carrinho o " + ann.getTitle());
		shoppingCart.add(ci);
		System.out.println("Adicionado");
	}

	public List<CartItem> getItens() {
		return shoppingCart.getItens();
	}
	
	public void remove(CartItem i){
		shoppingCart.remove(i);
	}
}
