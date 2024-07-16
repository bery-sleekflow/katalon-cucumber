package authentication
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testdata.CSVData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import cucumber.api.java.en.Then
import internal.GlobalVariable
import common.CommonStep

class Login {
	CommonStep commonStep = new CommonStep()
	
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

	@Given("I open Sleekflow {string}")
	def openSleekflowWeb(String version) {
		if (version == 'v2') {
			WebUI.openBrowser(GlobalVariable.v2_staging)
		} else {
			WebUI.openBrowser(GlobalVariable.v1_staging)
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
		// input username
		WebUI.verifyElementPresent(findTestObject('Object Repository/LoginPage/UsernameField'), 15)
		WebUI.setText(findTestObject('Object Repository/LoginPage/UsernameField'), credential.email)
		WebUI.click(findTestObject('Object Repository/LoginPage/ContinueSignInButton'))
		// input password
		WebUI.verifyElementPresent(findTestObject('Object Repository/LoginPage/PasswordField'), 15)
		WebUI.setText(findTestObject('Object Repository/LoginPage/PasswordField'), credential.password)
		WebUI.click(findTestObject('Object Repository/LoginPage/SignInButton'))
		// click continue if exceed limit device 
		WebUI.waitForPageLoad(10)
		continueExcedeedDeviceLimit()
	}
	
	def continueExcedeedDeviceLimit() {
		if (WebUI.verifyElementPresent(findTestObject('Object Repository/LoginPage/ContinueExceedLimitButton'), 10, FailureHandling.OPTIONAL)) {
			WebUI.click(findTestObject('Object Repository/LoginPage/ContinueExceedLimitButton'))
		} else {
			println "Continue button is not present."
		}
	}

	@Then("I should be on {string} page")
	def verifyAfterLogin(String page) {
		if (page != 'login') {
			WebUI.verifyElementPresent(findTestObject('Object Repository/TopNavBar/SettingMenuButton'), 15)
		}
		commonStep.verifyCurrentPage(page)
	}
}