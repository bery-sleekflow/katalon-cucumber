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



class CreateContact {
	CommonStep commonStep = new CommonStep()
	String phoneNumber = '62856412'

	@When("I create contact with {string}")
	def createContactWithCondition(String condition) {
		// navigate to contact page
		commonStep.navigateTo('contact')
		// create contact 
		WebUI.click(findTestObject('Object Repository/Contact/ContactListPage/CreateNewContactButton'))
		switch (condition) {
			case "phone number only":
				WebUI.focus(findTestObject('Object Repository/Contact/ContactFormPage/PhoneNumberInputText'))
				WebUI.clearText(findTestObject('Object Repository/Contact/ContactFormPage/PhoneNumberInputText'))
				WebUI.setText(findTestObject('Object Repository/Contact/ContactFormPage/PhoneNumberInputText'),phoneNumber)
				break;
			case "other":
				WebUI.setText(findTestObject('Object Repository/Contact/ContactFormPage/PhoneNumberInputText'),'000000000')
				break;
		}
		//save contact
		WebUI.click(findTestObject('Object Repository/Contact/ContactFormPage/CreateButton'))
		
	}

	@Then("contact is created successfully with {string}")
	def verifyContactCreatedSuccessfully(String condition) {
	    // Verify redirect to contact list
	    commonStep.verifyCurrentPage('contacts')
	    WebUI.verifyElementVisible(findTestObject('Object Repository/Contact/ContactListPage/ContactHeaderText'))
	    
	    // Verify contact is displayed in the list
	    searchContact(phoneNumber)
	    String actualText = WebUI.getText(findTestObject('Object Repository/Contact/ContactListPage/TableColumnName'))
	    assert actualText.contains(phoneNumber) : "Expected phone number to be part of the text, but it was not found."
	    
		}
	
	def searchContact(String input) {
		WebUI.setText(findTestObject('Object Repository/Contact/ContactListPage/SearchContactInputText'), input)
		WebUI.sendKeys(findTestObject('Object Repository/Contact/ContactListPage/SearchContactInputText'), Keys.chord(Keys.ENTER))
	}
	
}