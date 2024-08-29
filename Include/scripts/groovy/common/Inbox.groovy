package common
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


class Inbox {
	CommonStep commonStep = new CommonStep()
	String messageToCustomer = ''

	@Given("I open conversation with {string} from {string} with name {string}")
	def openConversation(String name, String location, String groupName) {
		// navigate to inbox page
		commonStep.navigateTo('inbox')
		// Determine xpath params
		if (location == 'company inbox') {
			location = 'COMPANY INBOX'
		} else {
			location = 'My inbox'
		}
		Map<String, String> params = [
			('subHeader') : location,
			('groupName') : groupName
		]
		// Click the conversation group
		TestObject dynamicObject = findTestObject("Object Repository/Inbox/SubMenuInbox", params)
		WebUI.click(dynamicObject)

		// search & click specific contact in inbox
		WebUI.setText(findTestObject("Object Repository/Inbox/SearchInput"), name)
		WebUI.click(findTestObject("Object Repository/Inbox/TabPanelContactFirstItem"))
	}

	@Then("I should see text {string} in textbox chat")
	def verifyTextInTextbox(String expectedText) {
		String actualText = WebUI.getAttribute(findTestObject('Object Repository/Inbox/ChatTextArea'), 'value')
		assert actualText.contains(expectedText) : "Text in the text area does not contain the expected text. Expected: '" + expectedText + "' but found: '" + actualText + "'"
		messageToCustomer = expectedText
	}
	
	@And("I am able to send the message to customer")
	def sendMessageToCustomer() {
		if (messageToCustomer != '') {
			WebUI.click(findTestObject('Object Repository/Inbox/SendMessageButton'))
		}
	}
	
	@Then("I should see the {string} sent to customer")
	def verifyMessageSentToCustomer(String type) {
		if (messageToCustomer != '') {
			if (type == 'payment link') {
				Map<String, String> params = [
					('sequence') : 'last()',
					('additionalXpath') : '/descendant::p[3]'
				]
				TestObject dynamicObject = findTestObject("Object Repository/Inbox/MessageSent", params)
				String actualText = WebUI.getText(dynamicObject)
				assert actualText.contains(messageToCustomer) : "Expected message not found. Expected to contain: '" + messageToCustomer + "' but found: '" + actualText + "'"
			}
		}
	}
	
	def replyButtonChecker(String action){
		String testObject = ''
		if (action == 'enter message') {
			testObject = 'Object Repository/Inbox/EnterMessageChatButton'
		} else {
			testObject = 'Object Repository/Inbox/ChooseTemplateChatButton'
		}
		if (WebUI.verifyElementPresent(findTestObject(testObject), 10, FailureHandling.OPTIONAL)) {
			WebUI.click(findTestObject(testObject))
		} else {
			println "Enter message button is not present."
		}
	}
}