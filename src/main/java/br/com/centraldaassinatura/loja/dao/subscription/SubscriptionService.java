package br.com.centraldaassinatura.loja.dao.subscription;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

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

}
