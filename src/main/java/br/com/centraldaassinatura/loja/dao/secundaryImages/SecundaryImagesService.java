package br.com.centraldaassinatura.loja.dao.secundaryImages;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.model.SecundaryImage;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SecundaryImagesService {

	@Inject
	private SecundaryImagesServiceDao secundaryImagesServiceDao;

	public boolean containsSecundaryImagesByAnnouncementId(Integer id) {
		return secundaryImagesServiceDao.containsSecundaryImagesByAnnouncementId(id);
	}

	public void save(SecundaryImage s) {
		secundaryImagesServiceDao.save(s);
	}
}
