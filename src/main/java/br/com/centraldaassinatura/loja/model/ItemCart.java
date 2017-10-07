package br.com.centraldaassinatura.loja.model;

public class ItemCart {

	private Announcement ann;
	private Integer quantity;

	public ItemCart(Announcement ann2) {
		this.ann = ann2;
	}

	public Announcement getAnn() {
		return ann;
	}

	public void setAnn(Announcement ann) {
		this.ann = ann;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ann == null) ? 0 : ann.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemCart other = (ItemCart) obj;
		if (ann == null) {
			if (other.ann != null)
				return false;
		} else if (!ann.equals(other.ann))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ItemCart: ann: " + ann + ", quantity: " + quantity;
	}
	
}
