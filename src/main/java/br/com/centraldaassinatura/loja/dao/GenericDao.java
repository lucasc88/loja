package br.com.centraldaassinatura.loja.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

public class GenericDao<T> implements Serializable {

	private static final long serialVersionUID = -2997399539062108527L;
	private final Class<T> classe;
	private EntityManager em;

	public GenericDao(EntityManager manager, Class<T> classe) {
		this.em = manager;
		this.classe = classe;
	}

	public void persist(T t) {
		em.persist(t);
	}

	public void remove(T t) {
		em.remove(em.merge(t));
	}

	public void update(T t) {
		em.merge(t);
	}

	public List<T> selectAll() {
		CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(classe);
		query.select(query.from(classe));
		List<T> lista = em.createQuery(query).getResultList();
		return lista;
	}

	public T findById(Integer id) {
		T instancia = em.find(classe, id);
		return instancia;
	}

	public int countAll() {
		long result = (Long) em.createQuery("select count(n) from livro n").getSingleResult();

		return (int) result;
	}

	public List<T> selectAllByPage(int firstResult, int maxResults) {
		CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(classe);
		query.select(query.from(classe));
		List<T> lista = em.createQuery(query).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
		return lista;
	}

}
