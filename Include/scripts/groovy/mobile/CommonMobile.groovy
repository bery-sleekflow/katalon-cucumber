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

import common.CommonStep
import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
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
	CommonStep common = new CommonStep()

	@When("I log in using mobile {string} credential")
	def loginMobile(String user) {
		// check user from data files
		def credential = common.searchUser(user)
		if (credential == null) {
			Mobile.comment("User not found: " + user)
			return
		}
	}

	@When("I open {string} app in mobile")
	def openMobileApp(String appName){
		String appPackage = ''
		if (appName == 'whatsapp') {
			appPackage = 'com.whatsapp'
		} else if (appName == 'instagram') {
			appPackage = 'com.instagram'
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