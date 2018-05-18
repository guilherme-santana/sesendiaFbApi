package br.com.sesendia.fb;

import static org.testng.Assert.assertTrue;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.*;
import io.restassured.http.Method;
import io.restassured.response.Response;

public class TesteAPI {

	private String baseURI = "https://graph.facebook.com/v3.0";
	private String client_id = "989428564554402";
	private String client_secret = "2659fb5f8ef95a0c7c12e16c78ed1edc";
	private String tokenFull_Temporal = "EAACEdEose0cBAGJE9wGv9ZBSOYSOiZCJ6OaUIE6OqAUjAc1MgiJ2ZB6Y04Ittpv7wVAQRAPmz3HuZAeIomZBa6kkHa7yREFHc6pynt9vvu1iy4oudlXv3Y1u3O6UQbZCmbTuyzUSqjeXJ5wkRBPr2ZCGZBRnSEECZAB6CfLpZBUthHKQgvXq4Et72y6L0gAfwfECJM3LbU8q1sewZDZD";
	private Helper help = new Helper();
	private JSONParser parser = new JSONParser();
	private JSONObject json;
	private Response responseBody;
	private String URl;

	@Test
	public void CT00_AutenticacaoToken_ClienteIDAusente() throws ParseException {
		URl = "/oauth/access_token?"
				+ "client_id="
				+ "&client_secret=" + client_secret
				+ "&grant_type=client_credentials";
		responseBody = help.Request(Method.GET, this.baseURI, URl);
		json = (JSONObject) parser.parse(responseBody.getBody().asString());
		System.out.println("Response =>  " + responseBody.getBody().asString());
		json = (JSONObject) json.get("error");

		assertTrue(json.get("message").equals("Missing client_id parameter.")
				&& json.get("code").toString().equalsIgnoreCase("101"), "Validação te ausencia de client_id");
	}

	@Test
	public void CT01_AutenticacaoToken_ClienteSecretInvalido() throws ParseException {
		URl = "/oauth/access_token?"
				+ "client_id=" + client_id
				+ "&client_secret="
				+ "&grant_type=client_credentials";
		responseBody = help.Request(Method.GET, this.baseURI, URl);
		json = (JSONObject) parser.parse(responseBody.getBody().asString());
		System.out.println("Response =>  " + responseBody.getBody().asString());
		json = (JSONObject) json.get("error");
		assertTrue(json.get("message").equals("Error validating client secret.") 
				&& json.get("code").toString().equalsIgnoreCase("1"), "Validação da ausencia de client_secret");
	}

	@Test
	public void CT02_AutenticacaoToken_AgrantInvalido() throws ParseException {
		URl = "/oauth/access_token?"
				+ "client_id=" + client_id
				+ "&client_secret=" + client_secret
				+ "&grant_type=%$#";
		responseBody = help.Request(Method.GET, this.baseURI, URl);
		json = (JSONObject) parser.parse(responseBody.getBody().asString());
		System.out.println("Response =>  " + responseBody.getBody().asString());
		json = (JSONObject) json.get("error");
		assertTrue(json.get("message").equals("Missing redirect_uri parameter.") 
				&& json.get("code").toString().equalsIgnoreCase("191"), "Validação da ausencia de agrant_type");
	}

	@Test
	public void CT03_AutenticacaoToken() throws ParseException {
		URl = "/oauth/access_token?"
				+ "client_id=" + client_id
				+ "&client_secret=" + client_secret
				+ "&grant_type=client_credentials";
		responseBody = help.Request(Method.GET, this.baseURI, URl);
		json = (JSONObject) parser.parse(responseBody.getBody().asString());
		System.out.println("Response =>  " + responseBody.getBody().asString());
		json.keySet();
		assertTrue(json.containsKey("access_token") && json.containsKey("token_type"), "Verificação do token recebido");
	}

	@Test
	public void CT04_Post() throws ParseException {

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Brazil/East"));

		URl = "/me?fields=id,name"
				+ "&access_token=" + tokenFull_Temporal;
		responseBody = help.Request(Method.GET, this.baseURI, URl);
		System.out.println("Response =>  " + responseBody.getBody().asString());
		json = (JSONObject) parser.parse(responseBody.getBody().asString());

		URl = "/"+json.get("id")
			+ "/accounts"
			+ "?access_token=" + tokenFull_Temporal;
		responseBody = help.Request(Method.GET, this.baseURI, URl);
		json = (JSONObject) parser.parse(responseBody.getBody().asString());
		Iterator<?> j = help.getIteratorJson(json.get("data"));
		json = (JSONObject) j.next();

		URl = "/"+json.get("id")
			+ "/feed"
			+ "?message=Inclusão de Post Automatizando o FaceBook "+calendar.getTimeInMillis()
			+ "&access_token=" + json.get("access_token").toString();
		responseBody = help.Request(Method.POST, this.baseURI, URl);
		json = (JSONObject) parser.parse(responseBody.getBody().asString());
		System.out.println("Response =>  " + responseBody.getBody().asString());
		assertTrue(json.containsKey("id"), "Verificação do se a resposta possui 'ID'");
	}

	@Test
	public void CT05_PostDuplicado() throws ParseException {
		responseBody = help.Request(Method.POST, this.baseURI, URl);
		System.out.println("Response =>  " + responseBody.getBody().asString());
		JSONObject json = (JSONObject) parser.parse(responseBody.getBody().asString());
		json = (JSONObject) json.get("error");
		assertTrue(json.get("message").equals("Duplicate status message") 
				&& json.get("error_user_msg").equals("This status update is identical to the last one you posted. Try posting something different, or delete your previous update.")
				&& json.get("code").toString().equalsIgnoreCase("506"), "Validação de mensagem quando post está duplicado");
	}

	@Test
	public void CT06_AlteracaoPost() throws ParseException {
		if (!json.get("id").toString().matches("\\d{16}_\\d{16}")) {
			CT04_Post();
		}
		URl = "/"+json.get("id")
			+ "?message=Alteração de Post Automatizando FaceBook"
			+ "&access_token=" + tokenFull_Temporal;
		responseBody = help.Request(Method.POST, this.baseURI, URl);
		System.out.println("Response =>  " + responseBody.getBody().asString());
		JSONObject json = (JSONObject) parser.parse(responseBody.getBody().asString());
		assertTrue(json.get("success").toString().equalsIgnoreCase("true"), "Validação se foi obtido sucesso na atualização do post"); 
	}

}