package br.com.centraldaassinatura.loja.dao.company;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import br.com.centraldaassinatura.loja.dao.GenericDao;
import br.com.centraldaassinatura.loja.model.Company;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CompanyDao {

	@PersistenceContext
	private EntityManager em;

	private GenericDao<Company> dao;

	@PostConstruct
	void init() {
		this.dao = new GenericDao<Company>(this.em, Company.class);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Company> allCompanies() {
		return dao.selectAll();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void persist(Company c) {
		dao.persist(c);
	}

	public void update(Company company) {
		dao.update(company);
	}

	public Company findByCnpj(String cnpj) {
		TypedQuery<Company> query = em.createQuery(
				"SELECT a FROM Company a WHERE a.cnpj LIKE :cnpj", Company.class);
		query.setParameter("cnpj", cnpj);
		try {
			return query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

}
