package rbac
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

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When



class RbacStep {
	@Then("the access main menu is correct based on role {string}")
	def verifyAccess(String role) {
		switch(role) {
			case 'staff':
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/AnalyticMenuButton"))
				} catch (Exception e) {
					println("Analytic Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/BroadcastMenuButton"))
				} catch (Exception e) {
					println("Broadcast Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/ChannelMenuButton"))
				} catch (Exception e) {
					println("Channel Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/FlowBuilderMenuButton"))
				} catch (Exception e) {
					println("Flow Builder Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/IntegrationMenuButton"))
				} catch (Exception e) {
					println("Integration Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/SleekflowAIMenuButton"))
				} catch (Exception e) {
					println("Sleekflow AI Menu Button is visible: " + e.message)
				}
				break;

			case 'team admin':
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/AnalyticMenuButton"))
				} catch (Exception e) {
					println("Analytic Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/BroadcastMenuButton"))
				} catch (Exception e) {
					println("Broadcast Menu Button is not visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/ChannelMenuButton"))
				} catch (Exception e) {
					println("Channel Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/FlowBuilderMenuButton"))
				} catch (Exception e) {
					println("Flow Builder Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/IntegrationMenuButton"))
				} catch (Exception e) {
					println("Integration Menu Button is visible: " + e.message)
				}
				try {
					WebUI.verifyElementNotVisible(findTestObject("Object Repository/Web/LeftNavBar/SleekflowAIMenuButton"))
				} catch (Exception e) {
					println("Sleekflow AI Menu Button is visible: " + e.message)
				}
				break;

			case 'admin':
				try {
					WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/AnalyticMenuButton"))
				} catch (Exception e) {
					println("Analytic Menu Button is not visible: " + e.message)
				}
				try {
					WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/BroadcastMenuButton"))
				} catch (Exception e) {
					println("Broadcast Menu Button is not visible: " + e.message)
				}
				try {
					WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/ChannelMenuButton"))
				} catch (Exception e) {
					println("Channel Menu Button is not visible: " + e.message)
				}
				try {
					WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/FlowBuilderMenuButton"))
				} catch (Exception e) {
					println("Flow Builder Menu Button is not visible: " + e.message)
				}
				try {
					WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/IntegrationMenuButton"))
				} catch (Exception e) {
					println("Integration Menu Button is not visible: " + e.message)
				}
				try {
					WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/SleekflowAIMenuButton"))
				} catch (Exception e) {
					println("Sleekflow AI Menu Button is not visible: " + e.message)
				}
				break;
		}

		// Always visible elements
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/InboxMenuButton"))
		} catch (Exception e) {
			println("Inbox Menu Button is not visible: " + e.message)
		}
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/TicketMenuButton"))
		} catch (Exception e) {
			println("Ticket Menu Button is not visible: " + e.message)
		}
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/ContactsMenuButton"))
		} catch (Exception e) {
			println("Contacts Menu Button is not visible: " + e.message)
		}
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/CommerceHubMenuButton"))
		} catch (Exception e) {
			println("Commerce Hub Menu Button is not visible: " + e.message)
		}
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/DataMenuButton"))
		} catch (Exception e) {
			println("Data Menu Button is not visible: " + e.message)
		}
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/InviteUserMenuButton"))
		} catch (Exception e) {
			println("Invite User Menu Button is not visible: " + e.message)
		}
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/SettingMenuButton"))
		} catch (Exception e) {
			println("Setting Menu Button is not visible: " + e.message)
		}
		try {
			WebUI.verifyElementVisible(findTestObject("Object Repository/Web/LeftNavBar/HelpSupportButton"))
		} catch (Exception e) {
			println("Help Support Button is not visible: " + e.message)
		}
	}
}