package br.com.centraldaassinatura.loja.bean.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.faces.context.FacesContext;

import org.primefaces.json.JSONObject;

import br.com.centraldaassinatura.loja.model.Client;
import br.com.centraldaassinatura.loja.model.Company;
import br.com.centraldaassinatura.loja.model.NaturesLegals;

public class CompanyWebService {

	private static final String URL_WEBSERVICE = "https://www.receitaws.com.br/v1/cnpj/";

	public static Company getCompanyFromCNPJ(String cnpj) {
		cnpj = cnpj.replace(".", "");
		cnpj = cnpj.replace("/", "");
		cnpj = cnpj.replace("-", "");
		HttpURLConnection connection = null;
		try {
			URL url = new URL(URL_WEBSERVICE + cnpj);
			connection = (HttpURLConnection) url.openConnection();
			InputStream content = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(content, "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			JSONObject json = new JSONObject(sb.toString());

			String situacao = json.getString("situacao");
			String situacaoEspacial = json.getString("situacao_especial");
			if (situacaoEspacial.equalsIgnoreCase("FALIDO") || !situacao.equalsIgnoreCase("ATIVA")) {
				return null;
			}
			return buildCompany(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			connection.disconnect();
		}
	}

	private static Company buildCompany(JSONObject json) {
		Company company = new Company();
		company.setCnpj(json.getString("cnpj"));
		company.setCep(json.getString("cep"));
		company.setCity(json.getString("municipio"));
		company.setLegalNature(findLegalNature(json));
		company.setState(json.getString("uf"));
		company.setStreet(json.getString("logradouro") + ", " + json.getString("numero"));
		company.setNeighborhood(json.getString("bairro"));
		company.setReasonSocial(json.getString("nome"));
		company.setValid(true);
		company.setNameFantasy("");
		Client userLogged = (Client) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userLogged");
		company.setClient(userLogged);
		return company;
	}

	private static NaturesLegals findLegalNature(JSONObject json) {
		NaturesLegals natureslegals = null;
		String cod = json.getString("natureza_juridica").substring(0, 5);
		for (NaturesLegals item : NaturesLegals.values()) {
			if (item.getCod().equals(cod)) {
				natureslegals = item;
				break;
			}
		}
		return natureslegals;
	}
}
