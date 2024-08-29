@inbox @chat
Feature: Inbox - chat
  Inbox feature chat to customer
	
	Background: 
    Given I open Sleekflow "v2"
    And I log in using "valid" credential
    
  @p0
  Scenario: User able to send message to customer
    Given I open conversation with "Bery" from "Company Inbox" with group name "All"
    When I send message "Message from QA Automation" to customer
    Then I should see the "message" sent to customer