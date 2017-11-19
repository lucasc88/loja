package br.com.centraldaassinatura.loja.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String agreementId;
	private String state;
	private String token;
	private String uuId;
	@Temporal(TemporalType.DATE)
	private Date paymentStartDate;
	@Temporal(TemporalType.DATE)
	private Date paymentLastDate;
	private BigDecimal shipping;
	@ManyToOne
	private Client client;
	@OneToOne
	@JoinColumn(name = "announcement", nullable = false)
	private Announcement announcement;

	public Subscription() {
	}

	public Subscription(String agreementId, String state, String token, Client client, Announcement announcement,
			Date paymentStartDate, Date paymentLastDate, BigDecimal shipping) {
		this.agreementId = agreementId;
		this.state = state;
		this.token = token;
		this.paymentStartDate = paymentStartDate;
		this.paymentLastDate = paymentLastDate;
		this.shipping = shipping;
		this.client = client;
		this.announcement = announcement;
	}

//	 @PrePersist does persist UuId before save in the DB
	 @PrePersist
	 public void createUuId(){
		 uuId = UUID.randomUUID().toString();
	 }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getPaymentStartDate() {
		return paymentStartDate;
	}

	public void setPaymentStartDate(Date paymentStartDate) {
		this.paymentStartDate = paymentStartDate;
	}

	public Date getPaymentLastDate() {
		return paymentLastDate;
	}

	public void setPaymentLastDate(Date paymentLastDate) {
		this.paymentLastDate = paymentLastDate;
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuid) {
		this.uuId = uuid;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Subscription other = (Subscription) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Subscription: agreementId: " + agreementId + ", state: " + state + ", token: " + token + ", client: "
				+ client + ", announcement: " + announcement;
	}

}
