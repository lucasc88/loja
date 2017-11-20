package br.com.centraldaassinatura.loja.dao.subscription;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.centraldaassinatura.loja.model.Subscription;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SubscriptionService {

	@Inject
	private SubscriptionDao subscriptionDao;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save(Subscription subscription) {
		subscriptionDao.persist(subscription);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Subscription findByToken(String token) {
		return subscriptionDao.findByToken(token);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Subscription s) {
		subscriptionDao.update(s);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Subscription findByAgreementId(String id) {
		return subscriptionDao.findByAgreementId(id);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Subscription findByUuId(String id) {
		return subscriptionDao.findByUuId(id);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Subscription> findSubscriptionsByAgreementId(Integer id) {
		return subscriptionDao.findSubscriptionsByAgreementId(id);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int totalSubscriptions() {
		return subscriptionDao.totalSubscriptions();
	}

	public EntityManager getEntityManager() {
		return subscriptionDao.getEntityManager();
	}

	public List<Subscription> findSubscriptionsByAgreementIdActiveOrderDate(Integer id) {
		return subscriptionDao.findSubscriptionsByAgreementIdActiveOrderDate(id);
	}
}
