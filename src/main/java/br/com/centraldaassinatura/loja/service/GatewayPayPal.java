package br.com.centraldaassinatura.loja.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Model;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.primefaces.json.JSONObject;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.ChargeModels;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.MerchantPreferences;
import com.paypal.api.payments.OverrideChargeModel;
import com.paypal.api.payments.Patch;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.PaymentDefinition;
import com.paypal.api.payments.Plan;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Client;

@Model
public class GatewayPayPal {

	public String createPlan(Announcement announcement) {
		String clientId = announcement.getCompany().getClientId();
		String clientSecret = announcement.getCompany().getClientSecret();
		APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");

		// Build Plan object
		Plan plan = new Plan();
		plan.setName(compressTexts(announcement.getTitle()));
		plan.setDescription(compressTexts(announcement.getDescription()));
		// FIXED. O plano tem um numero fixo de ciclos de pagamento.
		// INFINITE. O plano é infinito, ou tem 0 ciclos de pagamento.
		plan.setType(announcement.getType());

		// payment_definitions: Se quisermos oferecer um teste gratuito ou um
		// teste a um preço com desconto, então configuramos duas definições
		// de pagamento. O primeiro será a definição para o período
		// experimental,
		// e a segunda definição seria o pagamento regular.
		PaymentDefinition paymentDefinition = new PaymentDefinition();
		paymentDefinition.setName("Pagamento Regular");
		paymentDefinition.setType("REGULAR");
		paymentDefinition.setFrequency(announcement.getFrequency());// DAY, WEEK, MONTH, YEAR
		// se configuramos a frequencia para "SEMANA" e o intervalo de
		// freqüência para "1", estamos definindo um pagamento semanal
		paymentDefinition.setFrequencyInterval("1");// SEMPRE SERÁ UM PAGAMENTO
		// numero de ciclos totais
		paymentDefinition.setCycles(String.valueOf(announcement.getCycles()));

		// currency
		Currency currency = new Currency();
		currency.setCurrency("BRL");
		currency.setValue(String.valueOf(announcement.getPrice()));
		paymentDefinition.setAmount(currency);// para cobrar o cliente

		// charge_models (Modelos de cobrança): é especificar o custo de envio e
		// o imposto adicional ao valor do plano
		ChargeModels frete = new ChargeModels();
		frete.setType("SHIPPING");// frete
		Currency c = new Currency();
		c.setCurrency("BRL");
		c.setValue("0");
		frete.setAmount(c);
		List<ChargeModels> chargeModelsList = new ArrayList<ChargeModels>();
		chargeModelsList.add(frete);
		paymentDefinition.setChargeModels(chargeModelsList);

		// payment_definition
		List<PaymentDefinition> paymentDefinitionList = new ArrayList<PaymentDefinition>();
		paymentDefinitionList.add(paymentDefinition);
		plan.setPaymentDefinitions(paymentDefinitionList);

		// Preferencias do Comerciante: especifica preferências, como taxa de
		// instalação,
		// tentativas de falha máxima de um pagamento, URL de retorno, URL de
		// cancelamento,
		// URL de notificação, onde o PayPal redirecionará o usuário após um
		// pagamento
		MerchantPreferences merchantPreferences = new MerchantPreferences();
		merchantPreferences.setSetupFee(c);
		merchantPreferences.setCancelUrl("http://localhost:8080/paymentcanceled.xhtml");
		merchantPreferences.setReturnUrl("http://localhost:8080/paymentsuccess.xhtml");
		// 0 permite inúmeras falhas de cobranca
		merchantPreferences.setMaxFailAttempts("0");
		// Permite a cobrança automática do valor pendente no próximo ciclo
		merchantPreferences.setAutoBillAmount("YES");
		// Action to take if a failure occurs during initial payment
		merchantPreferences.setInitialFailAmountAction("CONTINUE");
		plan.setMerchantPreferences(merchantPreferences);

		try {
			plan = plan.create(apiContext);
			activePlan(apiContext, plan);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return plan.getId() + " " + plan.getPaymentDefinitions().get(0).getChargeModels().get(0).getId();
		// newClient is like the browser to request another URL
		// javax.ws.rs.client.Client newClient = ClientBuilder.newClient();
		// Pagamento pagamento = new Pagamento(total);
		// String target = "https://book-payment.herokuapp.com/payment";
		//// Entity transforma objeto em JSON
		// Entity<Pagamento> entity = Entity.json(pagamento);
		//// establish the target
		// WebTarget webTarget = newClient.target(target);
		//// send request with POST
		// Response request = webTarget.request().post(entity);
		// return request.getStatus();
	}

	private String compressTexts(String text) {
		if (text.length() > 124) {
			return text.substring(0, 124) + "...";
		}
		return text;
	}

	private APIContext createAPIContext(String clientId, String clientSecret, String enviroment) {
		APIContext apiContext = new APIContext(clientId, clientSecret, enviroment);
		return apiContext;
	}

	/**
	 * Ativa o plano
	 * 
	 * @param context
	 * @return
	 * @throws PayPalRESTException
	 * @throws IOException
	 */
	public void activePlan(APIContext apiContext, Plan plan) {
		List<Patch> patchRequestList = new ArrayList<Patch>();
		Map<String, String> value = new HashMap<String, String>();
		value.put("state", "ACTIVE");

		Patch patch = new Patch();
		patch.setPath("/");
		patch.setValue(value);
		patch.setOp("replace");
		patchRequestList.add(patch);

		try {
			plan.update(apiContext, patchRequestList);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public String[] newAgreement(Client c, Announcement announcement, BigDecimal valueShipping) {
		String clientId = announcement.getCompany().getClientId();
		String clientSecret = announcement.getCompany().getClientSecret();
		String planId = announcement.getPlanId();
		String chargeModelId = announcement.getChargeModelIdShipping();

		// Create new agreement
		Agreement agreement = new Agreement();
		agreement.setName(compressTexts(announcement.getTitle()));
		agreement.setDescription(compressTexts(announcement.getDescription()));
		agreement.setStartDate(convertDate());// date that start payments

		// Set plan ID
		Plan plan = new Plan();
		plan.setId(planId);
		agreement.setPlan(plan);

		// Add payer details
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
		PayerInfo payerInfo = new PayerInfo();
		payerInfo.setEmail(c.getEmail());
		payerInfo.setBirthDate(String.valueOf(c.getDateBirth()));
		payerInfo.setFirstName(c.getName());
		payerInfo.setLastName(c.getLastName());

		// Set shipping address information
		ShippingAddress shipping = new ShippingAddress();
		shipping.setLine1(c.getAddress().getStreet() + ", " + c.getAddress().getNumber());
		shipping.setCity(c.getAddress().getCity());
		shipping.setState(c.getAddress().getState());
		shipping.setPostalCode(c.getAddress().getCep().replaceAll("\\.|\\-", ""));
		shipping.setCountryCode("BR");

		payerInfo.setShippingAddress(shipping);
		payer.setPayerInfo(payerInfo);

		agreement.setPayer(payer);
		agreement.setShippingAddress(shipping);

		Currency shippingValue = new Currency();
		shippingValue.setCurrency("BRL");
		// implementar cálculo de frete;
		shippingValue.setValue(String.valueOf(valueShipping.doubleValue()));// atualizar
																			// com
																			// o
																			// valor
																			// do
																			// Frete
		OverrideChargeModel frete = new OverrideChargeModel();
		frete.setChargeId(chargeModelId);// quado se cria um plano o Charge cria
											// um Id proprio
		frete.setAmount(shippingValue);// frete
		List<OverrideChargeModel> overrideChargeModel = new ArrayList<>();
		overrideChargeModel.add(frete);
		agreement.setOverrideChargeModels(overrideChargeModel);

		try {
			APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");
			agreement = agreement.create(apiContext);
			for (Links links : agreement.getLinks()) {
				if ("approval_url".equals(links.getRel())) {
					System.out.println("URL do PayPal: " + links.getHref());
					String urlWithToken = links.getHref();
					System.out.println("link do PayPal p/ pagar: " + urlWithToken);
					String[] urlAndAgreementId = new String[2];
					urlAndAgreementId[0] = urlWithToken;
					urlAndAgreementId[1] = agreement.getId();
					return urlAndAgreementId;
					// PRIMEIRO redirecionar p/ pagar no PayPal, depois do
					// pagamento daí ativa
					// @SuppressWarnings("unused")
					// URL url = new URL(urlWithToken);
					// REDIRECT USER TO url, no fim da url tem o token
					// exemplo:
					// https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-9L961879N4220104M
					// break;
				}
			}
		} catch (PayPalRESTException e) {
			System.err.println(e.getDetails());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new String[0];
	}

	private String convertDate() {
		String d = Instant.now().plus(1, ChronoUnit.DAYS).toString();
		return d.substring(0, d.lastIndexOf(".")) + "Z";
	}

	public String[] activeAgreement(String clientId, String clientSecret, String token) {
		String agreementIdAndState[] = new String[2];
		
		APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");
		refreshToken(clientId, clientSecret);
		
		// Ativa Assinatura depois do pagamento de cliente;
		// token obtained when creating the agreement (following redirect)
		System.out.println("TOKEN da Assinatura: " + token);
		Agreement a = new Agreement();
		a.setToken(token);
		try {
			@SuppressWarnings("static-access")
			Agreement activeAgreement = a.execute(apiContext, a.getToken());
			System.out.println("Agreement created with ID " + activeAgreement.getId()
				+ ", " + activeAgreement.getState()
				+ ", startDate: " + activeAgreement.getStartDate()
				+ ", Token: " + activeAgreement.getToken());
			agreementIdAndState[0] = activeAgreement.getId();
			agreementIdAndState[1] = activeAgreement.getState();
		} catch (PayPalRESTException e) {
			System.err.println(e.getDetails());
		}
		return agreementIdAndState;
	}

	public String extractedTokenFromURL(String urlWithToken) {
		String tokenExtract = urlWithToken.substring(urlWithToken.indexOf("token="), urlWithToken.length());
		String token = tokenExtract.substring(tokenExtract.indexOf("=") + 1, tokenExtract.length());
		return token;
	}

	public String refreshToken(String clientId, String clientSecret) {
		// Define basic authentication credential values
		String usernameAndPassword = clientId + ":" + clientSecret;
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());

		// Build the form for a post request
		MultivaluedMap<String, String> formParameters = new MultivaluedHashMap<>();
		formParameters.add("grant_type", "client_credentials");

		// Perform a post request
		String restResource = "https://api.sandbox.paypal.com/v1/oauth2/token";
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		Response res = client.target(restResource)
				.request(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Accept", "application/json")
				.header("Accept-Language", "en_US")
				.header(authorizationHeaderName, authorizationHeaderValue)
				.post(Entity.form(formParameters));
		String result = res.readEntity(String.class);
		System.out.println(result);
		JSONObject obj = new JSONObject(result);
		return obj.get("access_token").toString();
	}

}
