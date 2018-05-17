package br.com.sesendia.fb;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.*;
import io.restassured.http.Method;
import io.restassured.response.Response;

public class TestNew {

	private String client_id = "989428564554402";
	private String client_secret = "2659fb5f8ef95a0c7c12e16c78ed1edc";
	private String baseURI = "https://graph.facebook.com/v3.0";
	private String URl;
	private String tokenFull = "EAACEdEose0cBANhK4u0KXvU2vbJg3bZAoyPNuaKTGqW4xUbLEwAqzWZAIeI0aJm6pt8tBOMw1SAOZBNydk4JdJMUZAcJ1hcVxM1v4Xkmt5EMtVlBfgMVPaYsvgLsTM43I5DeZCMFvn0cbPk4tgNJ7Ry9PlulTrTyGYgICDCUSeiUc5FoKQl7jDunMJryvk6XautLU1ZAz4gwZDZD";
	private Helper help = new Helper();
	private JSONParser parser = new JSONParser();
	private JSONObject json;
	
	@Test
	public void CT00_AutenticacaoToken() {
		URl = "/oauth/access_token?"
					+ "client_id=" + client_id
					+ "&client_secret=" + client_secret
					+ "&grant_type=client_credentials";
		Response responseBody = help.Request(Method.GET, this.baseURI, URl);
		System.out.println("Response =>  " + responseBody.getBody().asString());
	}
	
	@Test
	public void CT01_Post() throws ParseException {
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Brazil/East"));
		
		URl = "/me?fields=id,name"
				    + "&access_token=" + tokenFull;
		Response responseBody = help.Request(Method.GET, this.baseURI, URl);
		System.out.println("Response =>  " + responseBody.getBody().asString());
		json = (JSONObject) parser.parse(responseBody.getBody().asString());
		
		URl = "/"+json.get("id")
			+ "/accounts"
			+ "?access_token=" + tokenFull;
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
	}
	
	@Test
	public void CT02_AlteracaoPost() throws ParseException {
		if (!json.get("id").toString().matches("\\d{16}_\\d{16}")) {
			CT01_Post();
		}
		URl = "/"+json.get("id")
				+ "?message=Alteração de Post Automatizando FaceBook"
				+ "&access_token=" + tokenFull;
		Response responseBody = help.Request(Method.POST, this.baseURI, URl);
		System.out.println("Response =>  " + responseBody.getBody().asString());
		
	}
	
	public void DeletarPost() {

	}

}