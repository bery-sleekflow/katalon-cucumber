import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber

import org.junit.Test
import org.junit.runner.RunWith
import com.kms.katalon.core.util.KeywordUtil

/*@CucumberOptions(
 features = "Include/features",
 //glue = "authentication",
 plugin = ["pretty", "json:Reports/Cucumber/cucumber.json"],
 tags = "@p0 and @p1"
 )*/

class Listener {
	/**
	 * Add the GLUE option for Cucumber to locate the step definition files.
	 * @param testCaseContext related information of the executed test case.
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		CucumberKW.GLUE = ['common','contact','commerce','inbox','mobile']
	}

	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		if (testCaseContext.getTestCaseStatus() == 'FAILED') {
			String screenshotPath = ''
			// Try to capture a screenshot for mobile first, if fail then capture web
			//if (MobileDriverFactory.getDriver() != null) {
				try {
					screenshotPath = Mobile.takeScreenshot()
					KeywordUtil.logInfo("Mobile screenshot captured: " + screenshotPath)
				} catch (Exception e) {
					KeywordUtil.logInfo("Unable to capture mobile screenshot: " + e.message)
					// Attempt to capture a web screenshot if mobile screenshot fails
					try {
						screenshotPath = WebUI.takeScreenshot()
						KeywordUtil.logInfo("Web screenshot captured: " + screenshotPath)
					} catch (Exception ex) {
						KeywordUtil.logInfo("Unable to capture web screenshot: " + ex.message)
					}
				}
			//} else {
				// If mobile driver is not available, attempt to capture a web screenshot directly
				/*try {
					screenshotPath = WebUI.takeScreenshot()
					KeywordUtil.logInfo("Web screenshot captured: " + screenshotPath)
				} catch (Exception ex) {
					KeywordUtil.logInfo("Unable to capture web screenshot: " + ex.message)
				}*/
			//}


		}

		/*// Check if the mobile driver session is still active before closing the app
		 try {
		 if (MobileDriverFactory.getDriver() != null) {
		 Mobile.closeApplication()
		 }
		 } catch (Exception e) {
		 println("Failed to close the mobile application: " + e.message)
		 }
		 // Close the browser session
		 try {
		 WebUI.closeBrowser()
		 } catch (Exception e) {
		 println("Failed to close the browser: " + e.message)
		 }*/
	}

	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		try {
			// Log the completion of the test suite
			KeywordUtil.logInfo("Test Suite has completed.")

			try {
				if (MobileDriverFactory.getDriver() != null) {
					Mobile.closeApplication()
				}
				KeywordUtil.logInfo("Mobile application closed successfully.")
			} catch (Exception e) {
				KeywordUtil.logInfo("No mobile application was open or an error occurred while closing the application: " + e.message)
			}


			try {
				WebUI.closeBrowser()
				KeywordUtil.logInfo("Browser closed successfully.")
			} catch (Exception e) {
				KeywordUtil.logInfo("No web browser was open or an error occurred while closing the browser: " + e.message)
			}



		} catch (Exception e) {
			KeywordUtil.markFailed("Exception occurred in afterTestSuite: " + e.message)
		}
	}

	@Test
	void runCucumberTest() {
		CucumberKW.runWithCucumberRunner(Listener.class)
	}
}