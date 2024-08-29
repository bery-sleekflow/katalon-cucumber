package contact
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
import org.openqa.selenium.Keys as Keys

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



class ContactCreate {
	CommonStep commonStep = new CommonStep()
	String phoneNumber = ''
	String email = ''

	@When("I create contact with {string}")
	def createContactWithCondition(String condition) {
		// navigate to contact page
		commonStep.navigateTo('contact')
		// create contact
		WebUI.click(findTestObject('Object Repository/Web/Contact/ContactList/CreateNewContactButton'))
		switch (condition) {
			case "phone number only":
				phoneNumber = '62856412' + commonStep.randomNumberGenerator(3).toString()
				WebUI.focus(findTestObject('Object Repository/Web/Contact/ContactForm/PhoneNumberInput'))
				WebUI.clearText(findTestObject('Object Repository/Web/Contact/ContactForm/PhoneNumberInput'))
				WebUI.setText(findTestObject('Object Repository/Web/Contact/ContactForm/PhoneNumberInput'),phoneNumber)
				break;
			case "email only":
				email = 'automation.test+' + commonStep.randomNumberGenerator(4).toString() + '@mail.com'
				WebUI.focus(findTestObject('Object Repository/Web/Contact/ContactForm/EmailInput'))
				WebUI.clearText(findTestObject('Object Repository/Web/Contact/ContactForm/EmailInput'))
				WebUI.setText(findTestObject('Object Repository/Web/Contact/ContactForm/EmailInput'),email)
				break;
			case "other":
				WebUI.setText(findTestObject('Object Repository/Web/Contact/ContactForm/PhoneNumberInput'),'000000000')
				break;
		}
		//save contact
		WebUI.click(findTestObject('Object Repository/Web/Contact/ContactForm/CreateButton'))
	}

	@Then("contact is created successfully with {string}")
	def verifyContactCreatedSuccessfully(String condition) {
		String search = ''
		// Verify redirect to contact list
		commonStep.verifyCurrentPage('contacts')
		WebUI.verifyElementVisible(findTestObject('Object Repository/Web/Contact/ContactList/ContactHeaderText'))
		switch (condition) {
			case "phone number only":
				search = phoneNumber
				break;
			case "email only":
				search = email
				break;
		}
		// Verify contact is displayed in the list
		searchContact(search)
		String actualText = WebUI.getText(findTestObject('Object Repository/Web/Contact/ContactList/TableColumnName'))
		assert actualText.contains(search) : "Expected contact to be part of the text, but it was not found."
	}

	def searchContact(String input) {
		WebUI.setText(findTestObject('Object Repository/Web/Contact/ContactList/SearchContactInput'), input)
		WebUI.sendKeys(findTestObject('Object Repository/Web/Contact/ContactList/SearchContactInput'), Keys.chord(Keys.ENTER))
	}

	@And("I delete the created contact")
	def deleteContact(String searchInput) {
		searchContact(searchInput)
		WebUI.click(findTestObject('Object Repository/Web/Contact/ContactList/FirstTableColumnName'))
	}
}