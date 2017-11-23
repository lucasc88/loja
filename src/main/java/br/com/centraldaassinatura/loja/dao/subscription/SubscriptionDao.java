package br.com.centraldaassinatura.loja.dao.subscription;

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
		TypedQuery<Subscription> query = em.createQuery("SELECT s FROM Subscription s WHERE s.token LIKE :token",
				Subscription.class);
		query.setParameter("token", token);
		try {
			return query.getSingleResult();
		} catch (NoResultException nre) {// in case it does not find
			return null;
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void update(Subscription s) {
		dao.update(s);
	}

	public Subscription findByAgreementId(String id) {
		TypedQuery<Subscription> query = em.createQuery("SELECT s FROM Subscription s WHERE s.agreementId LIKE :agreementId",
				Subscription.class);
		query.setParameter("agreementId", id);
		try {
			return query.getSingleResult();
		} catch (NoResultException nre) {// in case it does not find
			return null;
		}
	}

	public Subscription findByUuId(String id) {
		TypedQuery<Subscription> query = em.createQuery("SELECT s FROM Subscription s WHERE s.uuId LIKE :uuId",
				Subscription.class);
		query.setParameter("uuId", id);
		try {
			return query.getSingleResult();
		} catch (NoResultException nre) {// in case it does not find
			return null;
		}
	}

	public List<Subscription> findSubscriptionsByAgreementId(Integer id) {
		TypedQuery<Subscription> query = em.createQuery("SELECT s FROM Subscription s WHERE s.announcement.id = :id",
				Subscription.class);
		query.setParameter("id", id);
		try {
			return query.getResultList();
		} catch (NoResultException nre) {// in case it does not find
			return null;
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public int totalSubscriptions() {
		TypedQuery<Long> query = em.createQuery("select count(n) from Subscription n", Long.class);
		long l = query.getSingleResult();
		return (int) l;
//		int x = (Integer) em.createQuery("select count(n) from Subscription n").getSingleResult();
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!! " + x);
//		return x;
	}

	public EntityManager getEntityManager() {
		return em;
	}

	public List<Subscription> findSubscriptionsByAgreementIdActiveOrderDate(Integer id) {
		TypedQuery<Subscription> query = em.createQuery("SELECT s FROM Subscription s WHERE s.announcement.id = :id AND s.state != 'PENDING' ORDER BY s.paymentStartDate",
				Subscription.class);
		query.setParameter("id", id);
		try {
			return query.getResultList();
		} catch (NoResultException nre) {// in case it does not find
			return null;
		}
	}

	public void remove(Subscription subscription) {
		dao.remove(subscription);
	}
}
