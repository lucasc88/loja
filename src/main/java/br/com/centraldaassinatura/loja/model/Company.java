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
		return "Company: nameFantasy: " + nameFantasy + ", reasonSocial: " + reasonSocial + ", cnpj: " + cnpj
				+ ", legalNature: " + legalNature + ", valid: " + valid + ", client: " + client;
	}

}
