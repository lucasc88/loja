package br.com.centraldaassinatura.loja.dao.subscription;

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
import br.com.centraldaassinatura.loja.model.Subscription;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SubscriptionDao {

	@PersistenceContext
	private EntityManager em;
	private GenericDao<Subscription> dao;
	
	@PostConstruct
	void init() {
		this.dao = new GenericDao<Subscription>(this.em, Subscription.class);
	}
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void persist(Subscription subscription) {
		dao.persist(subscription);
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public Subscription findByToken(String token) {
		TypedQuery<Subscription> query = em.createQuery("SELECT s FROM Subscription s WHERE s.token LIKE :token", Subscription.class);
		query.setParameter("token", token);
		try {
			return query.getSingleResult();
		} catch (NoResultException nre) {//in case it does not find
			return null;
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void update(Subscription s) {
		dao.update(s);
	}

}
