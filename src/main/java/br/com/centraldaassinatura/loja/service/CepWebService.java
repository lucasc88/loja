package br.com.centraldaassinatura.loja.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.primefaces.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import br.com.centraldaassinatura.loja.model.Address;

public class CepWebService {

	public static Address findAddress(Address address) {
		try {
			if(address.getCep() == null || address.getCep().isEmpty()){
				address.setCep(null);
				return address;
			}
			URL url = new URL("https://viacep.com.br/ws/" + address.getCep().replaceAll("\\.|\\-", "") + "/json/");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream content = null;
			try {
				content = connection.getInputStream();
			} catch (ConnectException e) {
				e.printStackTrace();
				return null;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return null;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(content, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			JSONObject json = new JSONObject(sb.toString());

			if (!json.toString().contains(":true}")) {
				address.setState(json.getString("uf"));
				address.setStreet(json.getString("logradouro"));
				address.setNeighborhood(json.getString("bairro"));
				address.setCity(json.getString("localidade"));
			} else {
				// CEP not found
				address = new Address();
				return address;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * 
	 * @param cepOrigem,
	 *            cepDestino, tipoServico, altura, largura, comprimento, peso
	 * @return
	 */
	public static String findShippingValue(String cepOrigem, String cepDestino, String tipoServico, int altura,
			int largura, int comprimento, double peso) {
		String nCdEmpresa = "";
		String sDsSenha = "";
		// 04510 Pac , 04014 Sedex
		String nCdServico = tipoServico;
		String sCepOrigem = cepOrigem.replaceAll("\\.|\\-", "");
		String sCepDestino = cepDestino.replaceAll("\\.|\\-", "");
		// String nVlPeso = "0.300";
		String nVlPeso = String.valueOf(peso);
		String nCdFormato = "1";// caixa
		String nVlComprimento = String.valueOf(comprimento);
		String nVlAltura = String.valueOf(altura);
		String nVlLargura = String.valueOf(largura);// largura nao pode ser
													// inferior a 11
		String nVlDiametro = "0";
		String sCdMaoPropria = "n";
		String nVlValorDeclarado = "0";
		String sCdAvisoRecebimento = "n";
		String StrRetorno = "xml";
		// URL do webservice correio para calculo de frete
		String urlString = "http://ws.correios.com.br/calculador/CalcPrecoPrazo.aspx";
		// os parametros a serem enviados
		Properties parameters = new Properties();
		parameters.setProperty("nCdEmpresa", nCdEmpresa);
		parameters.setProperty("sDsSenha", sDsSenha);
		// 04014 - Sedex ou 04510 - PAC
		parameters.setProperty("nCdServico", nCdServico);
		parameters.setProperty("sCepOrigem", sCepOrigem);
		parameters.setProperty("sCepDestino", sCepDestino);
		parameters.setProperty("nVlPeso", nVlPeso);
		parameters.setProperty("nCdFormato", nCdFormato);
		parameters.setProperty("nVlComprimento", nVlComprimento);
		parameters.setProperty("nVlAltura", nVlAltura);
		parameters.setProperty("nVlLargura", nVlLargura);
		parameters.setProperty("nVlDiametro", nVlDiametro);
		parameters.setProperty("sCdMaoPropria", sCdMaoPropria);
		parameters.setProperty("nVlValorDeclarado", nVlValorDeclarado);
		parameters.setProperty("sCdAvisoRecebimento", sCdAvisoRecebimento);
		parameters.setProperty("StrRetorno", StrRetorno);
		// o iterador, para criar a URL

		@SuppressWarnings("rawtypes")
		Iterator i = parameters.keySet().iterator();
		// o contador
		int counter = 0;
		// enquanto ainda existir parametros
		while (i.hasNext()) {
			// pega o nome
			String name = (String) i.next();
			// pega o valor
			String value = parameters.getProperty(name);
			// adiciona com um conector (? ou &)
			// o primeiro é ?, depois são &
			urlString += (++counter == 1 ? "?" : "&") + name + "=" + value;
		}
		String value = "";
		String error = "";
		try {
			// cria o objeto url
			URL url = new URL(urlString);
			// cria o objeto httpurlconnection
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// seta o metodo
			connection.setRequestProperty("Request-Method", "GET");
			// seta a variavel para ler o resultado
			connection.setDoInput(true);
			connection.setDoOutput(false);
			// conecta com a url destino
			connection.connect();
			// abre a conexão pra input
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			// le ate o final
			StringBuffer newData = new StringBuffer();
			String s = "";
			while (null != ((s = br.readLine()))) {
				newData.append(s);
			}
			br.close();
			// Prepara o XML que está em string para executar leitura por nodes
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(newData.toString()));
			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("cServico");
			// Faz a leitura dos nodes
			for (int j = 0; j < nodes.getLength(); j++) {
				Element element = (Element) nodes.item(j);
				NodeList valor = element.getElementsByTagName("Valor");
				Element line = (Element) valor.item(0);
				value = getCharacterDataFromElement(line);
				System.out.println("Valor: " + value);
				NodeList erro = element.getElementsByTagName("Erro");
				line = (Element) erro.item(0);
				error = getCharacterDataFromElement(line);
				System.out.println("Erro: " + error);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!error.isEmpty() && !error.equals("0")) {
			return error;
		}
		return value;
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}
}
