package br.com.centraldaassinatura.loja.dao.category;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.model.Category;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CategoryService {

	@Inject
	private CategoryDao categoryDao;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Category> allCategories() {
		return categoryDao.allCategories();
	}
	
}
