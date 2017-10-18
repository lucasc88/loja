package br.com.centraldaassinatura.loja.model;

public class CartItem {
	
	private Announcement announcement;
	private Integer quantity;

	public CartItem(Announcement ann2) {
		this.announcement = ann2;
		quantity = 1;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	public Integer getQuantity() {
		return quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((announcement == null) ? 0 : announcement.hashCode());
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
		CartItem other = (CartItem) obj;
		if (announcement == null) {
			if (other.announcement != null)
				return false;
		} else if (!announcement.equals(other.announcement))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CartItem: announcement: " + announcement + ", quantity: " + quantity;
	}
}
