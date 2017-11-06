package br.com.centraldaassinatura.loja.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.com.centraldaassinatura.loja.model.Category;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Announcement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private String description;
	private BigDecimal price;
	private String path;
	private String type;
	private String frequency;
	private String planId;
	private String chargeModelIdShipping;
	private int widthBox;
	private int heightBox;
	private int lengthBox;
	private double weightBox;
	private int cycles;
	@OneToMany(mappedBy = "ann", cascade = CascadeType.ALL)
	private List<SecundaryImage> secundaryImage;
	@ManyToOne
	private Company company;
	@ManyToOne
	private Category category;

	public Integer getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<SecundaryImage> getSecundaryImage() {
		return secundaryImage;
	}

	public void setSecundaryImage(List<SecundaryImage> secundaryImage) {
		this.secundaryImage = secundaryImage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public int getCycles() {
		return cycles;
	}

	public void setCycles(int cycles) {
		this.cycles = cycles;
	}
	
	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getChargeModelIdShipping() {
		return chargeModelIdShipping;
	}

	public void setChargeModelIdShipping(String chargeModelIdShipping) {
		this.chargeModelIdShipping = chargeModelIdShipping;
	}

	public int getWidthBox() {
		return widthBox;
	}

	public void setWidthBox(int widthBox) {
		this.widthBox = widthBox;
	}

	public int getHeightBox() {
		return heightBox;
	}

	public void setHeightBox(int heightBox) {
		this.heightBox = heightBox;
	}

	public int getLengthBox() {
		return lengthBox;
	}

	public void setLengthBox(int lengthBox) {
		this.lengthBox = lengthBox;
	}

	public double getWeightBox() {
		return weightBox;
	}

	public void setWeightBox(double weightBox) {
		this.weightBox = weightBox;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Announcement other = (Announcement) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Announcement: id: " + id + ", title: " + title + ", description: " + description + ", price: " + price
				+ ", path: " + path + ", company: " + company + ", category: " + category;
	}
}
