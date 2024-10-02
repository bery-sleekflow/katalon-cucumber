package common

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testdata.CSVData

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.JavascriptExecutor
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent



class CommonStep {
	//web
	WebDriver driver
	WebDriver driver1
	WebDriver driver2
	def credential

	//api
	ResponseObject response
	RequestObject request
	Map<String, Object> bodyFieldsToModify = [:]

	@Given("I open Sleekflow {string}")
	def openSleekflowWeb(String version) {
		//System.setProperty("webdriver.chrome.driver", "/Applications/"+ GlobalVariable.KatalonApp +"/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver")
		//driver = new ChromeDriver()
		//DriverFactory.changeWebDriver(driver)
		

		if (version == 'v2') {
			WebUI.openBrowser(GlobalVariable.v2_staging)
			//WebUI.navigateToUrl(GlobalVariable.v2_staging)
		} else if (version == 'v1') {
			WebUI.openBrowser(GlobalVariable.v1_staging)
			//WebUI.navigateToUrl(GlobalVariable.v1_staging)
		} else {
			throw new IllegalArgumentException("Unknown version: " + version)
		}
		maximizeWindowBrowser()
	}

	def maximizeWindowBrowser() {
		// Maximize the window
		WebUI.delay(2)
		try {
			WebUI.maximizeWindow()
		} catch (Exception e) {
			// If maximizing fails, set the viewport size manually as a fallback
			WebUI.setViewPortSize(1920, 1080)
		}
	}

	@When("I navigate to (.*) page")
	def navigateTo(String page) {
		WebUI.waitForPageLoad(10)
		switch(page) {
			case 'contact':
				WebUI.click(findTestObject('Object Repository/Web/LeftNavBar/ContactsMenuButton'))
				break;
			case 'inbox':
				WebUI.click(findTestObject('Object Repository/Web/LeftNavBar/InboxMenuButton'))
				break;
		}
	}

	def loginInput(String user) {
		// check user from data files
		credential = searchUser(user)
		if (credential == null) {
			WebUI.comment("User not found: " + user)
			return
		}
		// login via web
		try {
			// input username
			WebUI.verifyElementPresent(findTestObject('Object Repository/Web/LoginPage/UsernameField'), 15)
			WebUI.setText(findTestObject('Object Repository/Web/LoginPage/UsernameField'), credential.email)
			WebUI.click(findTestObject('Object Repository/Web/LoginPage/ContinueSignInButton'))
			// input password
			WebUI.verifyElementPresent(findTestObject('Object Repository/Web/LoginPage/PasswordField'), 15)
			WebUI.setText(findTestObject('Object Repository/Web/LoginPage/PasswordField'), credential.password)
			WebUI.click(findTestObject('Object Repository/Web/LoginPage/SignInButton'))
			WebUI.waitForPageLoad(15)
			if(WebUI.verifyMatch(WebUI.getUrl(), '.*' + GlobalVariable.baseUrl + '.*', true)) {
				continueExcedeedDeviceLimit()
			}
		} catch (Exception e) {
			WebUI.comment("An error occurred during the login process: " + e.getMessage())
		}
	}

	@When("I log in using {string} credential")
	def loginWeb(String user) {
		loginInput(user)

		// Get token from local storage browser
		String keyToRetrieve = GlobalVariable.v2ApiTokenSearch
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver
		String localStorageValue = jsExecutor.executeScript("""
		    var items = {};
		    for (var i = 0; i < localStorage.length; i++) {
		        var key = localStorage.key(i);
		        if (key.includes(arguments[0])) {
		            items[key] = localStorage.getItem(key);
		        }
		    }
		    return items[Object.keys(items)[0]];
		""", keyToRetrieve)
		if (localStorageValue != null && !localStorageValue.isEmpty()) {
			// Parse the JSON string
			def jsonSlurper = new JsonSlurper()
			def jsonObject = jsonSlurper.parseText(localStorageValue)
			// Save and Print token
			if (jsonObject.body.access_token) {
				GlobalVariable.bearerToken = jsonObject.body.access_token
				println("Token: " + jsonObject.body.access_token)
			} else {
				println("Token field not found in JSON.")
			}

			// Access other fields as needed
			println("Full JSON Data: " + jsonObject)
		} else {
			println("Local Storage Value for '" + keyToRetrieve + "': " + localStorageValue)
		}
	}

	@Given("I open 2 browser and log in using {string} and {string}")
	def loginMultiple(String user1, String user2) {
		// login as user 1
		//System.setProperty("webdriver.chrome.driver", "/Applications/" + GlobalVariable.KatalonApp + "/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver")
		driver1 = new ChromeDriver()  // Initialize driver1
		if (driver1 != null) {
			DriverFactory.changeWebDriver(driver1)
			WebUI.navigateToUrl(GlobalVariable.v2_staging)
			maximizeWindowBrowser()
			loginInput(user1)
			GlobalVariable.user1 = user1
			GlobalVariable.webDriver1 = driver1
		} else {
			WebUI.comment("Failed to initialize driver1 for user: " + user1)
		}

		// login as user 2
		driver2 = new ChromeDriver()  // Initialize driver2
		if (driver2 != null) {
			DriverFactory.changeWebDriver(driver2)
			WebUI.navigateToUrl(GlobalVariable.v2_staging)
			maximizeWindowBrowser()
			loginInput(user2)
			GlobalVariable.user2 = user2
			GlobalVariable.webDriver2 = driver2
		} else {
			WebUI.comment("Failed to initialize driver2 for user: " + user2)
		}
	}

	def changeWebDriver(user) {
		if (user == GlobalVariable.user1) {
			if (GlobalVariable.webDriver1 != null) {
				// Check if the current WebDriver is already set to webDriver1
				if (DriverFactory.getWebDriver() != GlobalVariable.webDriver1) {
					DriverFactory.changeWebDriver(GlobalVariable.webDriver1)  // Switch to driver1 for user1
					WebUI.comment("Switched to User 1's browser.")
				} else {
					WebUI.comment("Already using User 1's browser.")
				}
			} else {
				WebUI.comment("driver1 is null, cannot switch to User 1's browser.")
			}
		} else if (user == GlobalVariable.user2) {
			if (GlobalVariable.webDriver2 != null) {
				// Check if the current WebDriver is already set to webDriver2
				if (DriverFactory.getWebDriver() != GlobalVariable.webDriver2) {
					DriverFactory.changeWebDriver(GlobalVariable.webDriver2)  // Switch to driver2 for user2
					WebUI.comment("Switched to User 2's browser.")
				} else {
					WebUI.comment("Already using User 2's browser.")
				}
			} else {
				WebUI.comment("driver2 is null, cannot switch to User 2's browser.")
			}
		}
	}

	@Then("I should be on {string} page")
	def verifyCurrentPage(String page) {
		if (page.equals("login")) {
			WebUI.verifyMatch(WebUI.getUrl(), '.*' + GlobalVariable.loginUrl + '.*', true)
		} else {
			WebUI.verifyMatch(WebUI.getUrl(), '.*' + page + '.*', true)
			WebUI.verifyElementPresent(findTestObject('Object Repository/Web/TopNavBar/SettingMenuButton'), 15)
		}
	}

	@When("I log out from Sleekflow web")
	def logoutSleekflowWeb() {
		WebUI.verifyElementPresent(findTestObject('Object Repository/Web/TopNavBar/SettingMenuButton'), 15)
		WebUI.click(findTestObject('Object Repository/Web/TopNavBar/SettingMenuButton'))
		WebUI.click(findTestObject('Object Repository/Web/TopNavBar/SignOutButton'))
	}

	def static clearElementText(TestObject to) {
		WebElement element = WebUiCommonHelper.findWebElement(to,30)
		WebUI.executeJavaScript("arguments[0].value=''", Arrays.asList(element))
		WebUI.delay(2)
	}

	// click continue if exceed limit device
	def continueExcedeedDeviceLimit() {
		WebUI.delay(10)
		if (WebUI.verifyElementPresent(findTestObject('Object Repository/Web/LoginPage/ContinueExceedLimitButton'), 10, FailureHandling.OPTIONAL)) {
			WebUI.click(findTestObject('Object Repository/Web/LoginPage/ContinueExceedLimitButton'))
		} else {
			println "Continue button is not present."
		}
	}

	// search user from CSV
	def searchUser(String user) {
		CSVData data = TestDataFactory.findTestData('Data Files/staging_login')
		for (def row = 1; row <= data.getRowNumbers(); row++) {
			if (data.getValue('user', row) == user) {
				return [name: data.getValue('name', row), email: data.getValue('email', row), password: data.getValue('password', row)]
			}
		}
		return null
	}

	// Generate a random number with a specified number of digits
	def randomNumberGenerator(int numberOfDigits) {
		int min = (int) Math.pow(10, numberOfDigits - 1)  // Minimum value (e.g., 1000 for 4 digits)
		int max = (int) Math.pow(10, numberOfDigits) - 1  // Maximum value (e.g., 9999 for 4 digits)

		Random rand = new Random()
		int randomNumber = rand.nextInt((max - min) + 1) + min

		return randomNumber
	}

	@When("I call {string} to endpoint {string} with body {string}")
	def callSleekflowApi(String method, String endpoint, String jsonFilePath) {
		// validate request method
		if (![
					"POST",
					"PUT",
					"GET",
					"DELETE"
				].contains(method.toUpperCase())) {
			println "API method is invalid"
			return
		}

		// Create the RequestObject
		def fullEndpoint = GlobalVariable.v2ApiBaseUrl + '/' + endpoint
		request = new RequestObject()
		request.setRestUrl(fullEndpoint)
		request.setRestRequestMethod(method)

		// Set Authorization header (Bearer Token)
		request.setHttpHeaderProperties([
			new TestObjectProperty("Authorization", com.kms.katalon.core.testobject.ConditionType.EQUALS, "Bearer " + GlobalVariable.bearerToken),
			new TestObjectProperty("Content-Type", com.kms.katalon.core.testobject.ConditionType.EQUALS, "application/json")
		])

		// Read body content from the JSON file
		if (["POST", "PUT", "DELETE"].contains(method.toUpperCase()) && jsonFilePath != null) {
			// Modify JSON file content and get the updated request body
			String modifiedRequestBody = modifyBodyContentJsonFile(jsonFilePath, bodyFieldsToModify)

			if (modifiedRequestBody != null) {
				request.setBodyContent(new HttpTextBodyContent(modifiedRequestBody, "UTF-8", "application/json"))
			} else {
				println "Failed to read or modify the body content from: " + jsonFilePath
				return
			}
		}

		// Send the request
		response = WS.sendRequest(request)
		if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			println "Success for call " + method + " for endpoint " + fullEndpoint + " with status code " + response.getStatusCode()
		}else {
			println "Failed to call " + method + " for endpoint " + fullEndpoint + " with status code " + response.getStatusCode()
		}

		//println "Response body: " + response.getResponseBodyContent()
	}

	// General function to modify body content from a JSON file
	def modifyBodyContentJsonFile(String jsonFilePath, Map<String, Object> fieldsToModify) {
		try {
			File jsonFile = new File(jsonFilePath)

			if (!jsonFile.exists()) {
				println "JSON file not found at: " + jsonFilePath
				return null
			}

			// Parse the JSON file
			def jsonSlurper = new JsonSlurper()
			def jsonContent = jsonSlurper.parseText(jsonFile.text)

			// Modify the specified fields in the JSON
			fieldsToModify.each { key, value ->
				jsonContent[key] = value
			}

			// Convert the modified map back to a JSON string
			return JsonOutput.toJson(jsonContent)
		} catch (Exception e) {
			println "Error while modifying JSON content: " + e.message
			return null
		}
	}

	WebDriver getDriver1() {
		return driver1
	}

	WebDriver getDriver2() {
		return driver2
	}
}