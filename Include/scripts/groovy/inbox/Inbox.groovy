package inbox
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
	
	@When("user {string} open conversation with {string} from {string} with group name {string}")
	def openConversationUser(String user, String customerName, String location, String groupName) {
		if (user == GlobalVariable.user1) {
			if (GlobalVariable.webDriver1() != null) {
			DriverFactory.changeWebDriver(GlobalVariable.webDriver1())  // Switch to driver1 for user1
			} else {
				WebUI.comment("driver1 is null, cannot switch to User 1's browser")
			}
		}else if (user == GlobalVariable.user2) {
			if (GlobalVariable.webDriver2() != null) {
				DriverFactory.changeWebDriver(GlobalVariable.webDriver2())  // Switch to driver2 for user2
			} else {
				WebUI.comment("driver2 is null, cannot switch to User 2's browser")
			}
		}
		openConversation(customerName, location, groupName)
	}
	
	@Given("I open conversation with {string} from {string} with group name {string}")
	def openConversation(String name, String location, String groupName) {
		// navigate to inbox page
		commonStep.navigateTo('inbox')
		// determine xpath params
		if (location == 'Company Inbox') {
			location = 'COMPANY INBOX'
		} else {
			location = 'My inbox'
		}
		Map<String, String> params = [
			('subHeader') : location,
			('groupName') : groupName
		]
		// Click the conversation group
		TestObject dynamicObject = findTestObject("Object Repository/Web/Inbox/SubMenuInbox", params)
		WebUI.click(dynamicObject)

		// search & click specific contact in inbox
		WebUI.setText(findTestObject("Object Repository/Web/Inbox/SearchInput"), name)
		WebUI.click(findTestObject("Object Repository/Web/Inbox/TabPanelContactFirstItem"))
	}
	

	@Then("I should see text {string} in textbox chat")
	def verifyTextInTextbox(String expectedText) {
		String actualText = WebUI.getAttribute(findTestObject('Object Repository/Web/Inbox/ChatTextArea'), 'value')
		assert actualText.contains(expectedText) : "Text in the text area does not contain the expected text. Expected: '" + expectedText + "' but found: '" + actualText + "'"
		GlobalVariable.messageSentToCustomer = expectedText
	}

	@When("I send message {string} to customer")
	def enterMessageChat(String message) {
		// click enter message button if visible
		replyButtonChecker('enter message')
		//  adding unique number for identifier
		if (message == 'Message from QA Automation') {
			message = message + commonStep.randomNumberGenerator(4)
		}
		GlobalVariable.messageSentToCustomer = message
		// enter message and send to customer
		WebUI.setText(findTestObject('Object Repository/Web/Inbox/ChatTextArea'), message)
		verifyTextInTextbox(message)
		sendMessageToCustomer()
	}

	@And("I am able to send the message to customer")
	def sendMessageToCustomer() {
		if (GlobalVariable.messageSentToCustomer != '') {
			WebUI.click(findTestObject('Object Repository/Web/Inbox/SendMessageButton'))
		}
	}

	@Then("I should see the {string} sent to customer")
	def verifyMessageSentToCustomer(String type) {
		// waiting for message to be sent
		WebUI.delay(2)

		Map<String, String> params = [:]
		if (GlobalVariable.messageSentToCustomer != '') {
			if (type == 'payment link') {
				params = [
					('sequence') : 'last()',
					('additionalXpath') : '/descendant::p[3]'
				]
			} else {
				params = [
					('sequence') : 'last()',
					('additionalXpath') : '/descendant::p[1]'
				]
			}
			TestObject dynamicObject = findTestObject("Object Repository/Web/Inbox/MessageSent", params)
			String actualText = WebUI.getText(dynamicObject)
			assert actualText.contains(GlobalVariable.messageSentToCustomer) : "Expected message not found. Expected to contain: '" + GlobalVariable.messageSentToCustomer + "' but found: '" + actualText + "'"
		}
	}

	def replyButtonChecker(String action){
		String testObject = ''
		if (action == 'enter message') {
			testObject = 'Object Repository/Web/Inbox/EnterMessageChatButton'
		} else {
			testObject = 'Object Repository/Web/Inbox/ChooseTemplateChatButton'
		}
		if (WebUI.verifyElementPresent(findTestObject(testObject), 2, FailureHandling.OPTIONAL)) {
			WebUI.click(findTestObject(testObject))
		} else {
			println "Enter message button is not present."
		}
	}
	
	
}