package br.com.centraldaassinatura.loja.model;

import java.util.List;

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
	private String street;
	private String neighborhood;
	private String cep;
	private String city;
	@Enumerated(EnumType.STRING)
	private NaturesLegals legalNature;
	private String state;
	private Boolean valid;
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

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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
		return "Company: nameFantasy: " + nameFantasy + ", reasonSocial: " + reasonSocial + ", cnpj: " + cnpj
				+ ", street: " + street + ", neighborhood: " + neighborhood + ", cep: " + cep + ", city: " + city
				+ ", legalNature: " + legalNature + ", state: " + state + ", valid: " + valid + ", client: " + client;
	}

}
