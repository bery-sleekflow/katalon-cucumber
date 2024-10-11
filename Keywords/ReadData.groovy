import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import groovy.json.JsonSlurper
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable

public class ReadData {
	@Keyword
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

	@Keyword
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
}
