package br.com.centraldaassinatura.loja.infra;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.dao.category.CategoryDao;
import br.com.centraldaassinatura.loja.model.Category;

/**
 * Class developed for database persistence when server application is
 * initialized.
 * 
 * @author LucasDeCastro
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DataPersistenceBean {

	@Inject
	private CategoryDao categoryDao;

	@PostConstruct
    public void init() {
//		List<Category> catList = new ArrayList<>();
//		catList.add(new Category("Alimentação", null));
		if(categoryIsEmpty()){
			categoryDao.save(new Category("Alimentação", null));
			categoryDao.save(new Category("Bebidas", null));
			categoryDao.save(new Category("Moda", null));
			categoryDao.save(new Category("Literatura", null));
			categoryDao.save(new Category("Beleza", null));
			categoryDao.save(new Category("Infantil", null));
			categoryDao.save(new Category("Petz", null));
		}
	}

	private boolean categoryIsEmpty() {
		return categoryDao.allCategories().isEmpty();
	}
}
