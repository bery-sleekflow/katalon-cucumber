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

import common.CommonWebStep
import internal.GlobalVariable
import helper.Helper

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
	Helper helper = new Helper()
	CommonWebStep commonStep = new CommonWebStep()
	String messageToInternal, searchCategory, searchValue

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

		// Change filter status to "All"
		filterConversation("All")

		// search & click specific contact in inbox
		WebUI.setText(findTestObject("Object Repository/Web/Inbox/SearchInput"), name)
		TestObject result = findTestObject("Object Repository/Web/Inbox/SearchResultListContact", [('sequence') : 1])
		WebUI.waitForElementVisible(result, 5)
		boolean isVisible = WebUI.verifyElementVisible(result, FailureHandling.OPTIONAL)
		if(isVisible) {
			WebUI.click(result)
		} else {
			WebUI.click(findTestObject("Object Repository/Web/Inbox/SectionMessageButton"))
			WebUI.click(findTestObject("Object Repository/Web/Inbox/SearchResultListMessage", [('sequence') : 1]))
		}
	}


	@When("user {string} open conversation with {string} from {string} with group name {string}")
	def openConversationByUser(String user, String customerName, String location, String groupName) {
		commonStep.changeWebDriver(user)
		openConversation(customerName, location, groupName)
	}

	@When("I search conversation by {string} contains {string} from {string} with group name {string}")
	def searchConversation(String searchBy, String searchTerm, String location, String groupName) {
		searchCategory = searchBy
		searchValue = searchTerm
		openConversation(searchTerm, location, groupName)
	}

	@When("I change the filter inbox status to {string}")
	def filterConversation(String status) {
		WebUI.click(findTestObject("Object Repository/Web/Inbox/FilterStatus"))
		WebUI.click(findTestObject("Object Repository/Web/Inbox/FilterStatusOption", [('status') : status]))
	}

	@Then("I should see the {string} is displayed in the {string}")
	def verifySearch(String filter, String list) {
		// click section people or message
		if (list == 'message') {
			WebUI.click(findTestObject("Object Repository/Web/Inbox/SectionMessageButton"))
		}else {
			WebUI.click(findTestObject("Object Repository/Web/Inbox/SectionPeopleButton"))
		}

		// assertion for verify search result
		switch(filter) {
			case 'phone number':
				String inputValue = WebUI.getAttribute(findTestObject("Object Repository/Web/Inbox/SidePanel/ContactPhoneNumberOrEmail", [('sequence') : 1]), 'value').replaceAll("\\s", "")
				assert inputValue.contains(searchValue)
				break;
			case 'name':
				String inputValue = WebUI.getText(findTestObject("Object Repository/Web/Inbox/SidePanel/ContactName")).toLowerCase()
				assert inputValue.contains(searchValue)
				break;
			case 'message content':
				WebUI.verifyElementVisible(findTestObject("Object Repository/Web/Inbox/SearchResultContentMessage", [('content') : searchValue]))
				break;
		}
	}

	@Then("I should see text {string} in textbox chat")
	def verifyTextInTextbox(String expectedText) {
		String actualText = WebUI.getAttribute(findTestObject('Object Repository/Web/Inbox/ChatboxTextArea'), 'value')
		assert actualText.contains(expectedText) : "Text in the text area does not contain the expected text. Expected: '" + expectedText + "' but found: '" + actualText + "'"
		GlobalVariable.messageSentToCustomer = expectedText
	}

	@When("I send message {string} to customer")
	def enterMessageToCustomer(String message) {
		// click enter message button if visible
		replyButtonChecker('enter message')
		//  adding unique number for identifier
		if (message == 'Message from QA Automation') {
			message = message + helper.randomNumberGenerator(4)
		}
		GlobalVariable.messageSentToCustomer = message
		// enter message and send to customer
		WebUI.setText(findTestObject('Object Repository/Web/Inbox/ChatboxTextArea'), message)
		verifyTextInTextbox(message)
		sendMessage()
	}

	@When("I send message {string} to internal note")
	def enterMessageToInternal(String message) {
		// click internal note button
		WebUI.click(findTestObject('Object Repository/Web/Inbox/ChatboxInternalnotButton'))
		// adding unique number for identifier
		message = message + helper.randomNumberGenerator(4)
		messageToInternal = message
		// enter message and send to internal note
		WebUI.setText(findTestObject('Object Repository/Web/Inbox/ChatboxTextArea'), message)
		verifyTextInTextbox(message)
		sendMessage()
	}

	@When("user {string} send message {string} to internal note")
	def enterMessageToInternalByUser(String user, String message) {
		commonStep.changeWebDriver(user)
		enterMessageToInternal(message)
	}

	@And("I am able to send the message")
	def sendMessage() {
		if (GlobalVariable.messageSentToCustomer != '' || messageToInternal != '') {
			WebUI.click(findTestObject('Object Repository/Web/Inbox/ChatboxSendMessageButton'))
		}
	}

	@Then("I should see the {string} sent to {string}")
	def verifyMessageSent(String type, String target) {
		// Waiting for message to be sent
		WebUI.delay(2)

		String dynamicXpath = getDynamicXpath(type, target)
		String expectedMessage = (target == 'customer') ? GlobalVariable.messageSentToCustomer : messageToInternal

		// Validate for both customer and internal messages
		validateMessage(dynamicXpath, expectedMessage)

		// Special validation for internal notes if target is internal
		if (target == 'internal') {
			validateMessage('/descendant::p[2]', "Internal note")
		}
	}

	@Then("user {string} should see the {string} from user {string}")
	def verifyMessageSentByUser(String user1, String type, String user2) {
		commonStep.changeWebDriver(user1)
		def credential = helper.getUserLoginData(user2)
		// Waiting for message to be sent
		WebUI.delay(2)

		if (type == 'message') {
			// Validate the internal message sequence and the sender's name
			validateMessage('/descendant::p[1]', messageToInternal)
			validateMessage('/descendant::p[2]', "Internal note")
			validateMessage('/descendant::p[3]', credential.name)
		}
	}

	// General function to fetch the correct XPath based on type and target
	private String getDynamicXpath(String type, String target) {
		if (target == 'customer') {
			return (type == 'payment link') ? '/descendant::p[3]' : '/descendant::p[1]'
		} else if (target == 'internal') {
			return '/descendant::p[1]'  // Default XPath for internal messages
		}
		return null
	}

	// General validation function for message content
	private void validateMessage(String xpath, String expectedMessage) {
		Map<String, String> params = [
			('sequence') : 'last()',
			('additionalXpath') : xpath
		]
		TestObject dynamicObject = findTestObject("Object Repository/Web/Inbox/MessageSent", params)
		String actualText = WebUI.getText(dynamicObject)
		assert actualText.contains(expectedMessage) : "Expected message not found. Expected to contain: '" + expectedMessage + "' but found: '" + actualText + "'"
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