package br.com.centraldaassinatura.loja.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nameFantasy;
	private String reasonSocial;
	private String cnpj;
	private String street;
	private String cep;
	private String city;
	private String state;
	@ManyToOne(cascade = CascadeType.PERSIST)
	private Client client;

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

}
