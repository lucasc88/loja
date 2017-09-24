package br.com.centraldaassinatura.loja.dao.client;

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
import br.com.centraldaassinatura.loja.model.Client;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ClientDao {

	@PersistenceContext
	private EntityManager em;

	private GenericDao<Client> dao;

	@PostConstruct
	void init() {
		this.dao = new GenericDao<Client>(this.em, Client.class);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Client> allCategories() {
		return dao.selectAll();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void persist(Client user) {
		dao.persist(user);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Client findById(Integer id) {
		return dao.findById(id);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Client findByEmail(String email) {
		TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c WHERE c.email LIKE :email", Client.class);
		query.setParameter("email", email.trim());
		try {
			return query.getSingleResult();
		} catch (NoResultException nre) {//in case it does not find
			return null;
		}
	}
}
