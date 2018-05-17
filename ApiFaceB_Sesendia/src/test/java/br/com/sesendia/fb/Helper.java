package br.com.sesendia.fb;

import java.util.Iterator;
import org.json.simple.JSONArray;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Helper {
	
	public Response Request(Method tipoReq, String baseURI, String URl) {
		RestAssured.baseURI = baseURI;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(tipoReq, URl);
		return response;
	}
	
	public Iterator<?> getIteratorJson(Object json) {
		JSONArray ja = (JSONArray) json;
		return ja.iterator();
	}
	
}
