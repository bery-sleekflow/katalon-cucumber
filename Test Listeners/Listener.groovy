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
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

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

@CucumberOptions(
	features = "Include/features",
	//glue = "authentication",
	plugin = ["pretty", "json:Reports/Cucumber/cucumber.json"],
	tags = "@p0 and @p1"
)

class Listener {	
	/**
	 * Add the GLUE option for Cucumber to locate the step definition files.
	 * @param testCaseContext related information of the executed test case.
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		CucumberKW.GLUE = ['authentication','common','contact']
	}
	
	@AfterTestCase
    def afterTestCase(TestCaseContext testCaseContext) {
        String testCaseId = testCaseContext.getTestCaseId()
        if (!failedTestCases.containsKey(testCaseId)) {
            failedTestCases[testCaseId] = 0
        }
		
        if (testCaseContext.getTestCaseStatus() == 'FAILED') {
            
            try {
                // Capture screenshot on failure
                String screenshotPath = WebUI.takeScreenshot()
                println "Screenshot captured: " + screenshotPath
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        WebUI.closeBrowser()
    }
	
	@Test
	void runCucumberTest() {
	  CucumberKW.runWithCucumberRunner(Listener.class)
	  WebUI.maximizeWindow();
	}
}