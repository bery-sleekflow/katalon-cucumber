package helper
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

import internal.GlobalVariable

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

import groovy.json.JsonSlurper
import org.jboss.aerogear.security.otp.Totp
import com.kms.katalon.core.webui.common.WebUiCommonHelper

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When



class Helper {

	def randomNumberGenerator(int numberOfDigits) {
		int min = (int) Math.pow(10, numberOfDigits - 1)  // Minimum value (e.g., 1000 for 4 digits)
		int max = (int) Math.pow(10, numberOfDigits) - 1  // Maximum value (e.g., 9999 for 4 digits)

		Random rand = new Random()
		int randomNumber = rand.nextInt((max - min) + 1) + min

		return randomNumber
	}

	def getUserLoginData(String username) {
		// Define the path to the JSON file
		String jsonFilePath = "Data Files/api json file/user.json"

		// Load and parse the JSON file
		def jsonSlurper = new JsonSlurper()
		def jsonFile = new File(jsonFilePath)
		def parsedJson = jsonSlurper.parse(jsonFile)

		// Find the user based on the username
		def user = parsedJson.users.find { it.user == username }

		if (user == null) {
			throw new Exception("User not found: " + username)
		}

		// Return the user object
		return user
	}

	def getChannelData(String channelCategory, String name) {
		def result
		// Define the path to the JSON file
		String jsonFilePath = "Data Files/api json file/channel_list.json"

		// Load and parse the JSON file
		def jsonSlurper = new JsonSlurper()
		def jsonFile = new File(jsonFilePath)
		def parsedJson = jsonSlurper.parse(jsonFile)

		// Access the channelCategory dynamically
		def channels = parsedJson.channels[channelCategory]
		if (channels == null) {
			throw new Exception("Channel category " + channelCategory + " is not found")
		}

		// Find the specific channel by name
		result = channels.find { it.name == name }
		if (result == null) {
			throw new Exception("Channel " + channelCategory + " with name " + name + " is not found")
		}

		// Return the channel object
		return result
	}

	def GetMFAToken(token){
		Totp totp = new Totp(token)
		return totp.now()
	}

	def clearElementText(TestObject to) {
		WebElement element = WebUiCommonHelper.findWebElement(to,15)
		WebUI.executeJavaScript("arguments[0].value=''", Arrays.asList(element))
		WebUI.delay(2)
	}
}