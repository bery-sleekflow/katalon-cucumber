package commerce
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

import CustomKeywords
import common.CommonWebStep
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

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import inbox.Inbox



class CommerceStep {
	Inbox inbox = new Inbox()
	CommonWebStep common = new CommonWebStep()

	@When("I generate {string} payment link with {string} product")
	def generatePaymentLink(String type, String product) {
		inbox.replyButtonChecker("enter message")
		if (type == 'Custom') {
			WebUI.click(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLinkButton"))
			inputCustomPaymentLink(product)
			WebUI.click(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/GeneratePaymentLinkButton"))
			WebUI.click(findTestObject("Object Repository/Web/Inbox/Commerce/GeneratedLinkAddtochatButton"))
		}
	}

	def inputCustomPaymentLink(String product) {
		WebUI.setText(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/ProductNameInput", [('id') : 0]), 'Test from automation 1')
		WebUI.focus(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/AmountInput", [('sequence') : 1]))
		CustomKeywords.'WebHelper.clearElementText'(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/AmountInput", [('sequence') : 1]))
		WebUI.setText(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/AmountInput", [('sequence') : 1]), '1000')
		if (product != 'single') {
			WebUI.click(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/AddItemButton"))
			WebUI.setText(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/ProductNameInput", [('id') : 1]), 'Test from automation 2')
			CustomKeywords.'WebHelper.clearElementText'(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/AmountInput", [('sequence') : 2]))
			WebUI.focus(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/AmountInput", [('sequence') : 2]))
			WebUI.setText(findTestObject("Object Repository/Web/Inbox/Commerce/CustomPaymentLink/AmountInput", [('sequence') : 2]), '1500')
		}
	}

	@Then("I verify the (.*) in step")
	def I_verify_the_status_in_step(String status) {
		println status
	}
}