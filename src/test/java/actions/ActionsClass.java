package actions;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ActionsClass {
	public void loadApiUrl() {
		RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";
	}
	
	public JsonPath getJsonPath(Response response) {
		return response.jsonPath();
	}
	
	public void loadMD5JsonUrl() {
		RestAssured.baseURI = "http://md5.jsontest.com";
	}
	
	public void loadWeatherUrl() {
		RestAssured.baseURI = "http://api.openweathermap.org/data/2.5/weather/";
	}
	
	public void loadCircuitUrl() {
		RestAssured.baseURI = "http://ergast.com/api/f1/circuits/";
	}
	
	public void loadRegistrationUrl() {
		RestAssured.baseURI = "http://restapi.demoqa.com/customer";
	}
}
