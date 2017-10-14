package br.com.centraldaassinatura.loja.dao.secundaryImages;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import br.com.centraldaassinatura.loja.dao.GenericDao;
import br.com.centraldaassinatura.loja.model.SecundaryImage;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SecundaryImagesServiceDao {

	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	private EntityManager em;
	private GenericDao<SecundaryImage> dao;

	@PostConstruct
	void init() {
		this.dao = new GenericDao<SecundaryImage>(this.em, SecundaryImage.class);
	}

	public boolean containsSecundaryImagesByAnnouncementId(Integer id) {
		TypedQuery<SecundaryImage> query = em.createQuery("SELECT a FROM SecundaryImage a WHERE a.ann.id = :id",
				SecundaryImage.class);
		query.setParameter("id", id);
		if (query.getResultList().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public void save(SecundaryImage s) {
		dao.persist(s);
	}
}
