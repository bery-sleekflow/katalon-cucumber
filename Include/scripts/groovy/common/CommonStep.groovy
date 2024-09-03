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



class CommonStep {

	@Given("I open Sleekflow {string}")
	def openSleekflowWeb(String version) {
		System.setProperty("webdriver.chrome.driver", "/Applications/"+ GlobalVariable.KatalonApp +"/Contents/Eclipse/configuration/resources/drivers/chromedriver_mac/chromedriver")
		WebDriver driver = new ChromeDriver()
		DriverFactory.changeWebDriver(driver)
		
		if (version == 'v2') {
			WebUI.navigateToUrl(GlobalVariable.v2_staging)
		} else if (version == 'v1') {
			WebUI.navigateToUrl(GlobalVariable.v1_staging)
		} else {
			throw new IllegalArgumentException("Unknown version: " + version)
		}
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

	@When("I log in using {string} credential")
	def loginWeb(String user) {
		// check user from data files
		def credential = searchUser(user)
		if (credential == null) {
			WebUI.comment("User not found: " + user)
			return
		}
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
				return [email: data.getValue('email', row), password: data.getValue('password', row)]
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
}