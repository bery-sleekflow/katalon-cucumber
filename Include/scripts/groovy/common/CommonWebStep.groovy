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
import com.kms.katalon.core.exception.StepFailedException

import internal.GlobalVariable
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
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

class CommonWebStep {
	//web
	WebDriver driver = null
	WebDriver driver1, driver2
	def credential

	@Given("I open Sleekflow {string}")
	def openSleekflowWeb(String version) {
		// Set Chrome options for Docker environment
		ChromeOptions options = new ChromeOptions()
		options.addArguments(GlobalVariable.chromeArgument)
		System.setProperty("webdriver.chrome.driver", GlobalVariable.KatalonApp)
		if (this.driver == null) {
			driver = new ChromeDriver(options)
			DriverFactory.changeWebDriver(driver)
		}
		if (version == 'v2') {
			WebUI.navigateToUrl(GlobalVariable.v2_staging)
		} else if (version == 'v1') {
			WebUI.navigateToUrl(GlobalVariable.v1_staging)
		} else {
			throw new IllegalArgumentException("Unknown version: " + version)
		}

		/*if (version == 'v2') {
			WebUI.openBrowser(GlobalVariable.v2_staging)
		} else if (version == 'v1') {
			WebUI.openBrowser(GlobalVariable.v1_staging)
		} else {
			throw new IllegalArgumentException("Unknown version: " + version)
		}
		driver = DriverFactory.getWebDriver()
		*/
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
		credential = CustomKeywords.'ReadData.getUserLoginData'(user)
		if (credential == null) {
			WebUI.comment("User not found: " + user)
			return
		}
		// login process
		try {
			// input username
			WebUI.verifyElementPresent(findTestObject('Object Repository/Web/LoginPage/UsernameField'), 15)
			WebUI.setText(findTestObject('Object Repository/Web/LoginPage/UsernameField'), credential.email)
			WebUI.click(findTestObject('Object Repository/Web/LoginPage/ContinueSignInButton'))

			// input password
			WebUI.verifyElementPresent(findTestObject('Object Repository/Web/LoginPage/PasswordField'), 15)
			WebUI.setText(findTestObject('Object Repository/Web/LoginPage/PasswordField'), credential.password)
			WebUI.click(findTestObject('Object Repository/Web/LoginPage/SignInButton'))

			// input if otp is enabled
			inputOTP()
			WebUI.waitForPageLoad(15)
			// check exceed device limit and refresh page popup
			continueExcedeedDeviceLimit()
			dismissRefreshPopup()
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

		// Validate and parse token
		if (localStorageValue != null && !localStorageValue.isEmpty()) {
			def jsonSlurper = new JsonSlurper()
			def jsonObject = jsonSlurper.parseText(localStorageValue)
			def accessToken = jsonObject.body?.access_token
			if (accessToken) {
				GlobalVariable.bearerToken = accessToken
				WebUI.comment("Token successfully retrieved and stored.")
			} else {
				WebUI.comment("Token field not found in JSON.")
			}
		} else {
			WebUI.comment("No local storage value found for key: " + keyToRetrieve)
		}
	}

	@Given("I open 2 browser and log in using {string} and {string}")
	def loginMultiple(String user1, String user2) {
		// login as user 1
		System.setProperty("webdriver.chrome.driver", "/Applications/" + GlobalVariable.KatalonApp + "/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver")
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

	// click continue if exceed limit device
	def continueExcedeedDeviceLimit() {
		try {
			WebUI.click(findTestObject('Object Repository/Web/LoginPage/ContinueExceedLimitButton'))
		} catch (WebElementNotFoundException e){
			WebUI.comment("Exceed Limit Device is not present.")
		}
	}

	// dismiss refresh toast popup
	def dismissRefreshPopup() {
		try {
			WebUI.click(findTestObject('Object Repository/Web/CommonObject/refreshPageCloseButton'))
		} catch (WebElementNotFoundException e){
			WebUI.comment("Refresh toast popup is not present.")
		}
	}

	// input otp if otp login is active
	def inputOTP() {
		try {
			String totpCode = CustomKeywords.'ReadMFA.GetMFAToken'(credential.otpsecret)
			WebUI.setText(findTestObject('Object Repository/Web/LoginPage/OTPField'), totpCode)
			WebUI.click(findTestObject('Object Repository/Web/LoginPage/ContinueOTPButton'))
		} catch (StepFailedException e){
			WebUI.comment("OTP field is not present. Continuing without OTP.")
		}
	}
}