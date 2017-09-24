package br.com.centraldaassinatura.loja.dao.announcement;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.centraldaassinatura.loja.dao.GenericDao;
import br.com.centraldaassinatura.loja.model.Announcement;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AnnouncementDao {

	@PersistenceContext
	private EntityManager em;
	private GenericDao<Announcement> dao;
	
	@PostConstruct
	void init() {
		this.dao = new GenericDao<Announcement>(this.em, Announcement.class);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Announcement> allCategories() {
		return dao.selectAll();
	}

	public void persist(Announcement announcement) {
		dao.persist(announcement);
	}
	
	
}
