package br.com.centraldaassinatura.loja.dao.company;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.model.Company;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CompanyService {

	@Inject
	private CompanyDao companyDao;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Company> allCompanies() {
		return companyDao.allCompanies();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save(Company c) {
		companyDao.persist(c);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Company company) {
		companyDao.update(company);
	}
}
