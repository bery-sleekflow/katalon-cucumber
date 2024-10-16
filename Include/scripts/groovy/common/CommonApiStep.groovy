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

import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import groovy.json.JsonSlurper
import groovy.json.JsonOutput


class CommonApiStep {
	ResponseObject response
	RequestObject request
	Map<String, Object> bodyFieldsToModify = [:]
	List<String> fieldsToRemove = null

	@When("I call {string} to endpoint {string} with body {string}")
	def callSleekflowApi(String method, String endpoint, String jsonFilePath) {
		// validate request method
		if (![
					"POST",
					"PUT",
					"GET",
					"DELETE"
				].contains(method.toUpperCase())) {
			KeywordUtil.logInfo("API method is invalid")
			return
		}

		// Create the RequestObject
		def fullEndpoint = GlobalVariable.v2ApiBaseUrl + '/' + endpoint
		request = new RequestObject()
		request.setRestUrl(fullEndpoint)
		request.setRestRequestMethod(method)

		// Set Authorization header (Bearer Token)
		request.setHttpHeaderProperties([
			new TestObjectProperty("Authorization", com.kms.katalon.core.testobject.ConditionType.EQUALS, "Bearer " + GlobalVariable.bearerToken),
			new TestObjectProperty("Content-Type", com.kms.katalon.core.testobject.ConditionType.EQUALS, "application/json")
		])

		// Read body content from the JSON file
		if (["POST", "PUT", "DELETE"].contains(method.toUpperCase()) && jsonFilePath != null) {
			// Modify JSON file content and get the updated request body
			String modifiedRequestBody = modifyOrRemoveBodyField(jsonFilePath, bodyFieldsToModify, fieldsToRemove)

			if (modifiedRequestBody != null) {
				request.setBodyContent(new HttpTextBodyContent(modifiedRequestBody, "UTF-8", "application/json"))
			} else {
				println "Failed to read or modify the body content from: " + jsonFilePath
				return
			}
		}

		// Send the request
		response = WS.sendRequest(request)
		if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			KeywordUtil.logInfo("Success for call " + method + " for endpoint " + fullEndpoint + " with status code " + response.getStatusCode())
			return response.getResponseBodyContent()
		}else {
			KeywordUtil.logInfo("Failed to call " + method + " for endpoint " + fullEndpoint + " with status code " + response.getStatusCode())
		}
	}

	// General function to modify or remove fields from a JSON file content
	def modifyOrRemoveBodyField(String jsonFilePath, Map<String, Object> fieldsToModify, List<String> fieldsToRemove = null) {
		try {
			File jsonFile = new File(jsonFilePath)

			if (!jsonFile.exists()) {
				println "JSON file not found at: " + jsonFilePath
				return null
			}

			// Parse the JSON file
			def jsonSlurper = new JsonSlurper()
			def jsonContent = jsonSlurper.parseText(jsonFile.text)

			// Modify the specified fields in the JSON
			fieldsToModify.each { key, value ->
				jsonContent[key] = value
			}

			// Remove the specified fields from the JSON if fieldsToRemove is not null and contains values
			if (fieldsToRemove != null) {
				fieldsToRemove.each { fieldName ->
					if (jsonContent.containsKey(fieldName)) {
						jsonContent.remove(fieldName)
					} else {
						println "Field '${fieldName}' not found in the JSON content."
					}
				}
			}

			// Convert the modified map back to a JSON string
			return JsonOutput.toJson(jsonContent)
		} catch (Exception e) {
			println "Error while modifying/removing JSON content: " + e.message
			return null
		}
	}
}