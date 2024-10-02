@inbox @chat
Feature: Inbox - chat
  Inbox feature chat to customer, internal and interaction between 2 users
    
  @p0
  Scenario: User able to send message to customer
  	Given I open Sleekflow "v2"
    And I log in using "admin1" credential
    Given I open conversation with "Bery" from "Company Inbox" with group name "All"
    When I send message "Message from QA Automation" to customer
    Then I should see the "message" sent to "customer"
    
  @p0
  Scenario: User able to send message to internal note
  	Given I open Sleekflow "v2"
    And I log in using "admin1" credential
    Given I open conversation with "Bery" from "Company Inbox" with group name "All"
    When I send message "Message from QA Automation" to internal note
    Then I should see the "message" sent to "internal"
  
  @p0 @multiple_user_interaction
  Scenario: 2 User interaction in inbox internal note
  	Given I open 2 browser and log in using "admin1" and "staff1"
  	When user "admin1" open conversation with "Bery" from "Company Inbox" with group name "All"
  	And user "admin1" send message "Message for interaction" to internal note
  	And user "staff1" open conversation with "Bery" from "My Inbox" with group name "Assigned to me"
  	Then user "staff1" should see the "message" from user "admin1"
  	When user "staff1" send message "Message for interaction" to internal note
  	Then user "admin1" should see the "message" from user "staff1" 