package br.com.centraldaassinatura.loja.model;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.centraldaassinatura.loja.dao.subscription.SubscriptionService;

@Model
public class SubscriptionsDataModel extends LazyDataModel<Subscription> {

	private static final long serialVersionUID = -3508246497083234169L;
	@Inject
	private SubscriptionService subscriptionService;

	@PostConstruct
	public void init() {
		System.out.println("@@@@@@@@@@@@@ " + subscriptionService);
		int i = subscriptionService.totalSubscriptions();
		System.out.println("@@@@@@@@@@@@@ " + i);
		super.setRowCount(i);
	}

	// public SubscriptionsDataModel() {
	// System.out.println("@@@@@@@@@@@@@ " + subscriptionService);
	// int i = subscriptionService.totalSubscriptions();
	// System.out.println("@@@@@@@@@@@@@ " + i);
	// super.setRowCount(i);
	// }

	@Override
	public List<Subscription> load(int inicio, int quantidade, String campoOrdenacao, SortOrder sentidoOrdenacao,
			Map<String, Object> filtros) {
		EntityManager em = subscriptionService.getEntityManager();
		Session session = em.unwrap(Session.class);

		Criteria criteria = session.createCriteria(Subscription.class);
		criteria.setFirstResult(inicio);
		criteria.setMaxResults(quantidade);

		for (String key : filtros.keySet()) {
			System.out.println("@@@@@@@@@@@@@@@@@@@ FilterKey: " + key + ", " + filtros.get(key));
			if (key != null && key.equals("client.name")) {
				criteria.createAlias("client", "client");
		    }
			
			// ilike nao leva em consideracao maiuscula minuscula
			criteria.add(Restrictions.ilike(key, filtros.get(key) + "%"));
		}
		if (campoOrdenacao != null) {
			if (sentidoOrdenacao.name().contains("ASC")) {
				criteria.addOrder(Order.asc(campoOrdenacao));
			} else {
				criteria.addOrder(Order.desc(campoOrdenacao));
			}
		}
		List<Subscription> list = criteria.list();
		return list;
	}
}
