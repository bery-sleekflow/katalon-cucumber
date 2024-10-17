package channel
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
import common.CommonApiStep
import helper.Helper

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



class ChannelStep {
	CommonApiStep commonApi = new CommonApiStep()
	Helper helper = new Helper()
	def selectedChannel
	def originalChannelName
	def newChannelName

	@When("I rename channel {string} with name {string} to {string}")
	def renameChannel(String channel, String originalName, String newName) {
		selectedChannel = channel
		originalChannelName = originalName
		newChannelName = newName

		// open channel category
		WebUI.click(findTestObject("Object Repository/Web/LeftNavBar/ChannelMenuButton"))
		WebUI.click(findTestObject("Object Repository/Web/Channel/ChannelList", [('channelCategory') : channel]))

		// click rename button
		WebUI.click(findTestObject("Object Repository/Web/Channel/TableListThreeDotButton", [('channelName') : originalName]))
		WebUI.click(findTestObject("Object Repository/Web/Channel/TableListRenameButton"))

		// clear channel name
		WebUI.waitForElementVisible(findTestObject("Object Repository/Web/Channel/RenameChannelInput"), 5)
		WebUI.focus(findTestObject("Object Repository/Web/Channel/RenameChannelInput"))
		helper.clearElementText(findTestObject("Object Repository/Web/Channel/RenameChannelInput"))

		// rename channel
		WebUI.setText(findTestObject("Object Repository/Web/Channel/RenameChannelInput"),newName)
		WebUI.click(findTestObject("Object Repository/Web/Channel/RenameChannelSaveButton"))
	}

	@Then("channel name succesfully change to {string}")
	def verifyRenameChannel(String name) {
		WebUI.verifyElementVisible(findTestObject("Object Repository/Web/Channel/TableListThreeDotButton", [('channelName') : name]))
		renameChannelToOriginal()
	}

	// api call to change the name back to original
	def renameChannelToOriginal() {
		def result = helper.getChannelData(selectedChannel, originalChannelName)
		switch(selectedChannel) {
			case 'Facebook Messenger':
				commonApi.bodyFieldsToModify["name"] = originalChannelName
				commonApi.callSleekflowApi("POST", "Company/Facebook/rename/" + result.id , "Data Files/api json file/rename_channel.json")
				break;
			case 'WhatsApp 360dialog':
				commonApi.fieldsToRemove = ["name"]
				commonApi.bodyFieldsToModify["channelName"] = originalChannelName
				commonApi.callSleekflowApi("PUT", "company/whatsapp/360dialog/" + result.id , "Data Files/api json file/rename_channel.json")
				break;
			case 'Telegram':
				commonApi.fieldsToRemove = ["name"]
				commonApi.bodyFieldsToModify = [
					"telegramChannelId": result.id,
					"displayName": originalChannelName
				]
				commonApi.callSleekflowApi("PUT", "Company/telegram", "Data Files/api json file/rename_channel.json")
				break;
		}

		assert commonApi.response.getStatusCode() == 200
	}
}