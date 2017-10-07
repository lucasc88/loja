package br.com.centraldaassinatura.loja.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class ShoppingCart implements Serializable {

	private static final long serialVersionUID = -9025779850135712051L;
	private Set<ItemCart> itens = new HashSet<>();
	
	public void add(ItemCart item){
		itens.add(item);
	}

	public Set<ItemCart> getItens() {
		return itens;
	}

	public void setItens(Set<ItemCart> itens) {
		this.itens = itens;
	}
	
}
