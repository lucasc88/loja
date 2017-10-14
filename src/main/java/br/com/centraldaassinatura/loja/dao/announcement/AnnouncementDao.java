package br.com.centraldaassinatura.loja.dao.announcement;

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
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import br.com.centraldaassinatura.loja.dao.GenericDao;
import br.com.centraldaassinatura.loja.model.Announcement;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AnnouncementDao {

	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	private EntityManager em;
	private GenericDao<Announcement> dao;

	@PostConstruct
	void init() {
		this.dao = new GenericDao<Announcement>(this.em, Announcement.class);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Announcement> allAnnouncement() {
		return dao.selectAll();
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Announcement> lastAnnouncements() {
		TypedQuery<Announcement> query = em.createQuery("SELECT a FROM Announcement a ORDER BY a.id DESC",
				Announcement.class);
		return query.setMaxResults(9).getResultList();
	}

	public void persist(Announcement announcement) {
		dao.persist(announcement);
	}

	public Announcement findById(Integer id) {
		return dao.findById(id);
	}

	public Announcement findByIdWithSecundaryImages(Integer id) {
		TypedQuery<Announcement> query = em.createQuery(
				"SELECT a FROM Announcement a JOIN FETCH a.secundaryImage s WHERE a.id = :id", Announcement.class);
		query.setParameter("id", id);
		try {
			return query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
