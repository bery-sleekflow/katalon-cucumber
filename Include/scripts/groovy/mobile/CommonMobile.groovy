package mobile
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
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

import common.CommonWebStep
import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver

import static org.junit.Assert.assertArrayEquals

import org.openqa.selenium.By
import com.kms.katalon.core.configuration.RunConfiguration

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



class CommonMobile {
	CommonWebStep common = new CommonWebStep()
	def credential

	@When("I log in using mobile {string} credential")
	def loginSleekflowMobile(String user) {
		// check user from data files
		credential = CustomKeywords.'ReadData.getUserLoginData'(user)
		if (credential == null) {
			Mobile.comment("User not found: " + user)
			return
		}
		if (isLoginPageDisplayed()) {
			KeywordUtil.logInfo("Currently on login page, proceeding with login")
			loginInputSleekflowMobile()
			continueExcedeedDeviceLimit()
		} else {
			/*if (Mobile.verifyElementVisible(findTestObject("Object Repository/Mobile/SleekFlow/LoginPage/Exceed Limit - Continue here button"), 5)) {
			 KeywordUtil.logInfo("Currently on exceed limit page, tap continue")
			 continueExcedeedDeviceLimit()
			 } else {*/
			if (Mobile.verifyElementVisible(findTestObject("Object Repository/Mobile/SleekFlow/NavBar/My Profile"), 5)){
				KeywordUtil.logInfo("Currently not on login page, logging out first")
				logoutSleekflowMobile()
				loginInputSleekflowMobile()
				continueExcedeedDeviceLimit()
			} else {
				KeywordUtil.logInfo("Cannot find condition")
				return
			}
			//}
		}
	}

	def loginInputSleekflowMobile() {
		KeywordUtil.logInfo("Performing login process")
		// input email
		Mobile.tap(findTestObject("Object Repository/Mobile/SleekFlow/LoginPage/Continue to Sleekflow button"), 5)
		Mobile.setText(findTestObject('Object Repository/Mobile/SleekFlow/LoginPage/Username input'), credential.email, 5)
		Mobile.tap(findTestObject("Object Repository/Mobile/SleekFlow/LoginPage/Continue Login"), 5)
		Mobile.delay(5)

		// input password
		Mobile.setText(findTestObject('Object Repository/Mobile/SleekFlow/LoginPage/Password input'), credential.password, 5)
		Mobile.tap(findTestObject('Object Repository/Mobile/SleekFlow/LoginPage/Sign in button'), 5)
		Mobile.delay(3)
	}

	@When("I log out from sleekflow mobile")
	def logoutSleekflowMobile() {
		KeywordUtil.logInfo("Performing logout process")
		Mobile.tap(findTestObject("Object Repository/Mobile/SleekFlow/NavBar/My Profile"), 5)
		Mobile.scrollToText('Sign out', FailureHandling.OPTIONAL)
		Mobile.tap(findTestObject("Object Repository/Mobile/SleekFlow/MyProfilePage/Sign out button"), 5)
		Mobile.delay(3)
	}

	// click continue if exceed limit device
	def continueExcedeedDeviceLimit() {
		if (Mobile.verifyElementExist(findTestObject('Object Repository/Mobile/SleekFlow/LoginPage/Exceed Limit - Continue here button'), 10, FailureHandling.OPTIONAL)) {
			Mobile.tap(findTestObject('Object Repository/Mobile/SleekFlow/LoginPage/Exceed Limit - Continue here button'), 5)
		} else {
			KeywordUtil.logInfo("Continue button is not present.")
		}
	}

	// Function to check if the login page is displayed
	boolean isLoginPageDisplayed() {
		Mobile.delay(5)
		try {
			return Mobile.verifyElementVisible(findTestObject("Object Repository/Mobile/SleekFlow/LoginPage/Continue to Sleekflow button"), 5)
		} catch (Exception e) {
			return false
		}
	}

	@Then("I should be ON {string} mobile page")
	def verifyMobilePage(String page) {
		if (page == 'inbox'){
			Mobile.verifyElementVisible(findTestObject("Object Repository/Mobile/SleekFlow/InboxPage/Assigned to me"), 0)
		}else if (page == 'login'){
			assert isLoginPageDisplayed() : "Currently not on login page"
		}
	}

	@When("I open {string} app in mobile")
	def openMobileApp(String appName){
		String appPackage = ''
		if (appName == 'whatsapp') {
			appPackage = 'com.whatsapp'
		} else if (appName == 'instagram') {
			appPackage = 'com.instagram'
		} else if (appName == 'sleekflow') {
			appPackage = 'io.sleekflow.sleekflow'
		}
		Mobile.startExistingApplication(appPackage, FailureHandling.STOP_ON_FAILURE)
	}

	@When("I close all opened app in mobile")
	def closeAllMobileApp(){
		MobileDriverFactory.getDriver().executeScript("mobile: shell", ["command":"input", "args":[
				"keyevent",
				"KEYCODE_APP_SWITCH"
			]])
		MobileDriverFactory.getDriver().executeScript("mobile: shell", ["command":"input", "args":[
				"swipe",
				"300",
				"1000",
				"300",
				"100"
			]])
	}

	@Then("I should see message in whatsapp app mobile from channel {string} is received")
	def verifyMessageWhatsApp(String channel){
		// Add a delay for making sure the message is received
		Mobile.delay(10)

		Mobile.tap(findTestObject('Object Repository/Mobile/Whatsapp/whatsappContactName', [('channel') : channel]), 3)
		Mobile.verifyElementExist(findTestObject('Object Repository/Mobile/Whatsapp/messageFromAdmin', [('message') : GlobalVariable.messageSentToCustomer]), 3)
	}
}