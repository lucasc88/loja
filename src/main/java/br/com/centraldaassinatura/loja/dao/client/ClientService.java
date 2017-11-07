package br.com.centraldaassinatura.loja.dao.client;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import br.com.centraldaassinatura.loja.model.Client;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ClientService {

	@Inject
	private ClientDao clientDao;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Client> allClients() {
		return clientDao.allClients();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save(Client user) {
		clientDao.persist(user);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Client findByEmail(String email) {
		return clientDao.findByEmail(email);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Client findById(Integer id) {
		return clientDao.findById(id);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Client c) {
		clientDao.update(c);
	}
}
