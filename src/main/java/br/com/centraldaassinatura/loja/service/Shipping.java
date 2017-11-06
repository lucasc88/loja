package br.com.centraldaassinatura.loja.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Shipping {

	public static void main(String[] args) {
		// Dados pesquisa
		String nCdEmpresa = "";
		String sDsSenha = "";

		// PAC eh mais barato
		// 04014 SEDEX à vista
		// 04065 SEDEX à vista pagamento na entrega
		// 04510 PAC à vista
		// 04707 PAC à vista pagamento na entrega
		// 40169 SEDEX 12 ( à vista e a faturar)*
		// 40215 SEDEX 10 (à vista e a faturar)*
		// 40290 SEDEX Hoje Varejo*
		String nCdServico = "04510";
		String sCepOrigem = "91350070";
		String sCepDestino = "90010350";
		String nVlPeso = "4.000";

		// 1 – Formato caixa/pacote
		// 2 – Formato rolo/prisma
		// 3 - Envelope
		String nCdFormato = "1";
		String nVlComprimento = "30";
		String nVlAltura = "30";
		String nVlLargura = "30";
		String nVlDiametro = "0";// soh se for rolo
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
				System.out.println("Valor: " + getCharacterDataFromElement(line));
				NodeList erro = element.getElementsByTagName("Erro");
				line = (Element) erro.item(0);
				System.out.println("Erro: " + getCharacterDataFromElement(line));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
