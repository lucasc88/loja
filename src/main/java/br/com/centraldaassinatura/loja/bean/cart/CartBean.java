package br.com.centraldaassinatura.loja.bean.cart;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.dao.announcement.AnnouncementService;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.ItemCart;
import br.com.centraldaassinatura.loja.model.ShoppingCart;

@Model
public class CartBean {

	@Inject
	private AnnouncementService announcementService;
	@Inject
	private ShoppingCart shoppingCart;
	
	public void add(Integer id) {
		Announcement ann = announcementService.findById(id);
		System.out.println("Adicionou ao carrinho: " + ann.getTitle());
		ItemCart itemCart = new ItemCart(ann);
		shoppingCart.add(itemCart);
		shoppingCart.getItens().forEach((v) -> System.out.println("Item : " + v));
	}
}
