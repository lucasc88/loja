package br.com.centraldaassinatura.loja.dao.category;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import br.com.centraldaassinatura.loja.dao.GenericDao;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Category;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CategoryDao {

	@PersistenceContext
	private EntityManager em;

	private GenericDao<Category> dao;

	@PostConstruct
	void init() {
		this.dao = new GenericDao<Category>(this.em, Category.class);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Category> allCategories() {
		return dao.selectAll();
	}
	
	public void save(Category c){
		dao.persist(c);
	}
}
