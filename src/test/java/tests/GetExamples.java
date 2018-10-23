package tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.with;
import java.io.File;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import actions.ActionsClass;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class GetExamples extends ActionsClass {

	@Test(description = "Get users list and validate the result")
	public void test_01() {
		loadApiUrl();
		Response response = given().request(Method.GET, "/users");
		JsonPath jsonPathEvaluator = getJsonPath(response);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.then().contentType(ContentType.JSON).extract().path("[0].address.suite"),
				"Apt. 556");
		Assert.assertEquals(jsonPathEvaluator.get("[1].address.suite"), "Suite 879");
		Assert.assertEquals(jsonPathEvaluator.get("[0].company.catchPhrase"), "Multi-layered client-server neural-net");
	}

	@Test(description = "Get posts list and validate the result")
	public void test_02() {
		loadApiUrl();
		Response response = given().request(Method.GET, "/posts");
		Assert.assertEquals(response.getStatusCode(), 200);
		given().get("/posts").then().body("[0].title",
				equalTo("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"));
	}

	@Test(description = "Get Comments list and check if it has the email")
	public void test_03() {
		loadApiUrl();
		Response response = given().request(Method.GET,"/comments");
		List<String> emailList = with(response.body().asString()).get("email");
		assertThat(emailList, hasItem("Eliseo@gardner.biz"));
	}

	@Test(description = "Send invalid request and validate the status code")
	public void test_04() {
		loadApiUrl();
		Response response = given().request(Method.GET, "/album");
		Assert.assertEquals(response.getStatusCode(), 404);
	}

	@Test(priority = 8, dataProvider = "md5hashes", description = "Validate the status code")
	public void test_05(String originalText, String md5Text) {
		loadMD5JsonUrl();
		given().param("text", originalText).when().get("http://md5.jsontest.com").then()
		.assertThat().statusCode(200)
		.body("md5", equalTo(md5Text));
	}

	@Test(description = "Validate the data for weather API")
	public void test_06() {
		loadWeatherUrl();
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.param("q", "pune,india").param("appid", "c091bc8766e51e4acddc89ddd4cb4b6d");
		Response response = httpRequest.request(Method.GET);
		JsonPath jsonPathEvaluator = getJsonPath(response);
		Assert.assertEquals(jsonPathEvaluator.get("coord.lon"), Float.parseFloat("73.85"));
	}

	@Test(description = "Using params() to fetch data and validating the response and JSON schema")
	public void test_07() {
		String currentDir = System.getProperty("user.dir");
		File fs = new File(currentDir+"\\src\\test\\resources\\json-schema.json");
		loadWeatherUrl();
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.params("q", "nashik,india", "appid", "c091bc8766e51e4acddc89ddd4cb4b6d");
		Response response = httpRequest.request(Method.GET);
		JsonPath jsonPathEvaluator = getJsonPath(response);
		httpRequest.get().then().assertThat().body(matchesJsonSchema(fs));
		Assert.assertEquals(jsonPathEvaluator.get("weather[0].description"), "clear sky");
	}

	@Test(description = "Using pathParams to fetch data and validating the country name", dataProvider = "circuitLocations")
	public void test_08(String circuitId, String location) {
		loadCircuitUrl();
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.pathParam("circuitId", circuitId);
		Response response = httpRequest.request(Method.GET,"/{circuitId}.json");
		String countryName = with(response.body().asString()).get("MRData.CircuitTable.Circuits[0].Location.country").toString();
		Assert.assertEquals(countryName, location);
	}
	
	@Test(description = "Validate response message")
	public void test_09() {
		loadWeatherUrl();
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.params("q", "nashik,india", "appid","c091bc8766e51e4acddc89ddd4cb4b6i");
		Response response = httpRequest.request(Method.GET);
		JsonPath jsonPathEvaluator = getJsonPath(response);
		Assert.assertEquals(response.getStatusCode(), 401);
		Assert.assertEquals(jsonPathEvaluator.get("message"), "Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.");
	}
	
	@Test(description = "Send invalid data and check if it returns correct response or not")
	public void test_10() {
		loadWeatherUrl();
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.params("q", "abc,india", "appid","c091bc8766e51e4acddc89ddd4cb4b6d");
		Response response = httpRequest.request(Method.GET);
		JsonPath jsonPathEvaluator = getJsonPath(response);
		Assert.assertEquals(response.getStatusCode(), 404);
		Assert.assertEquals(jsonPathEvaluator.get("message"), "city not found");
	}
	
	@Test(description = "Try using the invalid method type")
	public void test_11() {
		loadWeatherUrl();
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.params("q", "abc,india", "appid","c091bc8766e51e4acddc89ddd4cb4b6d");
		Response response = httpRequest.request(Method.PUT);
		JsonPath jsonPathEvaluator = getJsonPath(response);
		Assert.assertEquals(response.getStatusCode(), 405);
		Assert.assertEquals(jsonPathEvaluator.get("message"), "Internal error");
	}
	
	@DataProvider(name = "md5hashes")
	public String[][] createMD5TestData() {
		return new String[][] { { "testcaseOne", "4ff1c9b1d1f23c6def53f957b1ed827f" },
				{ "testcaseTwo", "39738347fb533d798aca9ae0f56ca126" },
				{ "testcaseThree", "db6b151bb4bde46fddb361043bc3e2d9" } };
	}

	@DataProvider(name = "circuitLocations")
	public String[][] createCircuitTestData() {
		return new String[][] { { "adelaide", "Australia" }, { "detroit", "USA" }, { "george", "South Africa" } };
	}
}
