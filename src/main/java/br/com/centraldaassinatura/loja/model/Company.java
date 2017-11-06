package br.com.centraldaassinatura.loja.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nameFantasy;
	private String reasonSocial;
	private String cnpj;
	private String site;
	private String clientId;
	private String clientSecret;
	@Enumerated(EnumType.STRING)
	private NaturesLegals legalNature;
	private Boolean valid;
	@OneToOne(cascade = CascadeType.ALL)
	private Address address;
	@OneToOne
	private Client client;
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<Announcement> announcement;

	public Integer getId() {
		return id;
	}

	public String getNameFantasy() {
		return nameFantasy;
	}

	public void setNameFantasy(String nameFantasy) {
		this.nameFantasy = nameFantasy;
	}

	public String getReasonSocial() {
		return reasonSocial;
	}

	public void setReasonSocial(String reasonSocial) {
		this.reasonSocial = reasonSocial;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public NaturesLegals getLegalNature() {
		return legalNature;
	}

	public void setLegalNature(NaturesLegals legalNature) {
		this.legalNature = legalNature;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<Announcement> getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(List<Announcement> announcement) {
		this.announcement = announcement;
	}

	@Override
	public String toString() {
		return "Company: id: " + id + ", nameFantasy: " + nameFantasy + ", reasonSocial: " + reasonSocial + ", cnpj: "
				+ cnpj + ", clientId: " + clientId + ", clientSecret: " + clientSecret + ", legalNature: " + legalNature
				+ ", valid: " + valid + ", address: " + address;
	}

}
