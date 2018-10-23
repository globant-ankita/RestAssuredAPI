package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import actions.ActionsClass;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Random;


public class PostExamples extends ActionsClass{
	static Random rand = new Random();

	static int  n = rand.nextInt(50) + 1;
	@Test(description = "Send a different method type and verify the result")
	public void test_1() {
		loadRegistrationUrl();
		given().request().get("/register").then()
		.assertThat().statusCode(405)
		.body("FaultId",equalTo("Invalid method type found in Request."))
		.body("fault", equalTo("FAULT_INVALID_METHOD_TYPE_IN_REQUEST"));
		
	}
	
	@Test(description = "Send method type as post without passing any data and verify the result")
	public void test_2() {
		loadRegistrationUrl();
		given().request().post("/register").then()
		.assertThat().statusCode(400)
		.body("FaultId",equalTo("Invalid post data, please correct the request"))
		.body("fault", equalTo("FAULT_INVALID_POST_REQUEST"));
	}
	@Test(description =" POST Example creating a new user")
	public void test_3() {
		loadRegistrationUrl();
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject requestParams = new JSONObject();
		requestParams.put("FirstName", "RestAPI"+n);
		requestParams.put("LastName", "RestAPI"+n);
		requestParams.put("UserName", "RestAPI"+n);
		requestParams.put("Email", "RestAPI"+n+"@test.com");
		requestParams.put("Password", "1234567890");
		httpRequest.header("Content-Type","application/json");
		httpRequest.body(requestParams.toJSONString());
		Response response = httpRequest.post("/register");
		
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 201);
		String successCode = response.jsonPath().get("SuccessCode");
		Assert.assertEquals(successCode, "OPERATION_SUCCESS");
	}
	
	@Test(description = "Try to create a duplicate user and verify the result")
	public void test_4() {
		loadRegistrationUrl();
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject requestParams = new JSONObject();
		requestParams.put("FirstName", "RestAPI"+n);
		requestParams.put("LastName", "RestAPI"+n);
		requestParams.put("UserName", "RestAPI"+n);
		requestParams.put("Email", "RestAPI"+n+"@test.com");
		requestParams.put("Password", "1234567890");
		httpRequest.header("Content-Type","application/json");
		httpRequest.body(requestParams.toJSONString());
		Response response = httpRequest.post("/register");
		
		int statusCode = response.getStatusCode();
		String faultId = response.jsonPath().get("FaultId");
		String fault = response.jsonPath().get("fault");
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(faultId, "User already exists");
		Assert.assertEquals(fault, "FAULT_USER_ALREADY_EXISTS");
	}
}
