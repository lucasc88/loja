package br.com.centraldaassinatura.loja.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.ZonedDateTime;
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
import com.paypal.api.payments.AgreementStateDescriptor;
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

import br.com.centraldaassinatura.loja.model.Address;
import br.com.centraldaassinatura.loja.model.Announcement;
import br.com.centraldaassinatura.loja.model.Client;

@Model
public class GatewayPayPal {

	public String[] createPlan(Announcement announcement) {
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
		paymentDefinition.setFrequency(findFrequency(announcement.getFrequency()));// DAY,
																	// WEEK,
																	// MONTH,
																	// YEAR
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
		merchantPreferences.setCancelUrl("https://centraldaassinatura.com.br/paymentcanceled.xhtml");
		merchantPreferences.setReturnUrl("https://centraldaassinatura.com.br/paymentsuccess.xhtml");
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
		String[] a = new String[2];
		a[0] = plan.getId();
		a[1] = plan.getPaymentDefinitions().get(0).getChargeModels().get(0).getId();
		return a;
	}

	private String findFrequency(String frequency) {
		String v = "DAY";
		switch (frequency) {
		case "Diária":
			v = "DAY";
			break;
		case "Senamal":
			v = "WEEK";
			break;
		case "Mensal":
			v = "MONTH";
			break;
		case "Anual":
			v = "YEAR";
			break;
		default:
			break;
		}
		return v;
	}

	private String compressTexts(String text) {
		if (text.length() > 123) {
			return text.substring(0, 123) + "...";
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
		List<Patch> patchRequestList = new ArrayList<>();
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
	
	public Plan inactivePlan(APIContext apiContext, Plan plan){
		List<Patch> patchRequestList = new ArrayList<>();
		Map<String, String> value = new HashMap<String, String>();
		value.put("state", "INACTIVE");

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
		return plan;
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
		agreement.setStartDate(startPaymentDate());// date that start payments

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
			Agreement newAgreement = agreement.create(apiContext);
			for (Links links : newAgreement.getLinks()) {
				if ("approval_url".equals(links.getRel())) {
					System.out.println("URL do PayPal: " + links.getHref());
					String urlWithToken = links.getHref();
					System.out.println("link do PayPal p/ pagar: " + urlWithToken);
					String[] urlAndAgreementId = new String[3];
					urlAndAgreementId[0] = urlWithToken;
					urlAndAgreementId[1] = newAgreement.getId();
					urlAndAgreementId[2] = newAgreement.getStartDate();
					return urlAndAgreementId;
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

	private String startPaymentDate() {
		String d = ZonedDateTime.now().plus(2, ChronoUnit.MINUTES).toString();
		return d.substring(0, d.lastIndexOf(".")) + "-0300";//Brazil
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
			System.out.println("Agreement created with ID " + activeAgreement.getId() + ", "
					+ activeAgreement.getState() + ", startDate: " + activeAgreement.getStartDate() + ", Token: "
					+ activeAgreement.getToken());
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
		Response res = client.target(restResource).request(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Accept", "application/json").header("Accept-Language", "en_US")
				.header(authorizationHeaderName, authorizationHeaderValue).post(Entity.form(formParameters));
		String result = res.readEntity(String.class);
		System.out.println(result);
		JSONObject obj = new JSONObject(result);
		return obj.get("access_token").toString();
	}

	public void updateShipping(String agreementId, String value, Address address, String clientId, String clientSecret) {
		APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");
		refreshToken(clientId, clientSecret);
		Agreement newAgreement = getAgreement(agreementId, apiContext);
		
//		Currency shippingValue = new Currency();
//		shippingValue.setCurrency("BRL");
//		shippingValue.setValue(value);
//		OverrideChargeModel frete = new OverrideChargeModel();
//		frete.setChargeId(chargeModelId);
//		frete.setAmount(shippingValue);// frete novo
//		List<OverrideChargeModel> overrideChargeModel = new ArrayList<>();
//		overrideChargeModel.add(frete);
//		newAgreement.setOverrideChargeModels(overrideChargeModel);

//		System.out.println("Antigo valor do Frete: " + newAgreement.getPlan().getPaymentDefinitions().get(0)
//				.getChargeModels().get(1).getAmount().getValue());
//		newAgreement.getPlan().getPaymentDefinitions().get(0).getChargeModels().get(1).getAmount().setValue(value);

		ShippingAddress shipping = new ShippingAddress();
		shipping.setLine1(address.getStreet() + ", " + address.getNumber());
		shipping.setCity(address.getCity());
		shipping.setState(address.getState());
		shipping.setPostalCode(address.getCep().replaceAll("\\.|\\-", ""));
		shipping.setCountryCode("BR");
		newAgreement.setShippingAddress(shipping);

		// build PatchRequest which is an array of Patch
		List<Patch> patchRequestList = new ArrayList<>();
		Map<String, ShippingAddress> map = new HashMap<>();
		map.put("shipping_address", shipping);
		Patch patch = new Patch();
		patch.setPath("/");
		patch.setValue(map);
		patch.setOp("replace");
		patchRequestList.add(patch);
		try {
			newAgreement.update(apiContext, patchRequestList);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
	}
	
	public void cancelSubscription(String clientId, String clientSecret, String agreementId){
		APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");
		refreshToken(clientId, clientSecret);
		Agreement newAgreement = getAgreement(agreementId, apiContext);
		AgreementStateDescriptor agreementStateDescriptor = new AgreementStateDescriptor();
		agreementStateDescriptor.setNote("Cancelando Assinatura " + agreementId);
		try {
			newAgreement.cancel(apiContext, agreementStateDescriptor);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		System.out.println(newAgreement.toString());
	}

	private Agreement getAgreement(String agreementId, APIContext apiContext) {
		Agreement newAgreement = null;
		try {
			newAgreement = Agreement.get(apiContext, agreementId);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return newAgreement;
	}

	public String showAgreementDetails(String clientId, String clientSecret, String agreementId) {
		APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");
		refreshToken(clientId, clientSecret);
		Agreement newAgreement = getAgreement(agreementId, apiContext);
		System.out.println(newAgreement.toString());
		return newAgreement.getState();
	}
	
	private Plan getPlan(String planId, APIContext apiContext) {
		Plan newPlan = null;
		try {
			newPlan = Plan.get(apiContext, planId);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return newPlan;
	}
	
	public String showAnnouncementDetails(String clientId, String clientSecret, String planId) {
		System.out.println("Chamou showAnnouncementDetails");
		APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");
		refreshToken(clientId, clientSecret);
		Plan newPlan = getPlan(planId, apiContext);
		System.out.println(newPlan.toString());
		return newPlan.getState();
	}

	public String cancelPlan(String clientId, String clientSecret, String planId) {
		APIContext apiContext = createAPIContext(clientId, clientSecret, "sandbox");
		refreshToken(clientId, clientSecret);
		Plan newPlan = getPlan(planId, apiContext);
		newPlan = inactivePlan(apiContext, newPlan);
		Plan updatedPlan = getPlan(planId, apiContext);
		System.out.println(updatedPlan.toString());
		return "INACTIVE";
	}

}
