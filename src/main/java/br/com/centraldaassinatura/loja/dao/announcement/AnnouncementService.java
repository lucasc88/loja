package br.com.centraldaassinatura.loja.dao.announcement;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.model.Announcement;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AnnouncementService {

	@Inject
	private AnnouncementDao announcementDao;

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Announcement> allAnnouncement() {
		return announcementDao.allAnnouncement();
	}

	public Announcement findById(Integer id) {
		return announcementDao.findById(id);
	}

	public void save(Announcement announcement) {
		announcementDao.persist(announcement);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Announcement> lastAnnouncements() {
		return announcementDao.lastAnnouncements();
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Announcement findByIdWithSecundaryImages(Integer id) {
		return announcementDao.findByIdWithSecundaryImages(id);
	}

	public List<Announcement> allAnnouncementByCompanyId(Integer id) {
		return announcementDao.allAnnouncementByCompanyId(id);
	}

	public Announcement findByUuId(String uuId) {
		return announcementDao.findByUuId(uuId);
	}

	public void update(Announcement ann) {
		announcementDao.update(ann);
	}

	public List<Announcement> findByCategories(List<Integer> categoriesSelected) {
		return announcementDao.findByCategories(categoriesSelected);
	}

	public List<Announcement> findByKeyWord(String keyWord) {
		return announcementDao.findByKeyWord(keyWord);
	}

	public List<Announcement> bestSellerAnnouncements() {
		return announcementDao.bestSellerAnnouncements();
	}
}
