@inbox @chat
Feature: Inbox - chat
  Inbox feature chat to customer
    
  @p0
  Scenario: User able to send message to customer
  	#Given I open Sleekflow "v2"
    #And I log in using "valid" credential
    #Given I open conversation with "Bery" from "Company Inbox" with group name "All"
    #When I send message "Message from QA Automation" to customer
    #Then I should see the "message" sent to customer
  
  @p0
  Scenario: 2 User interaction in inbox internal note
  	Given I open 2 browser and log in using "valid" and "staff1"
  	When user "valid" open conversation with "Bery" from "Company Inbox" with group name "All"
  	#And user "valid" send message "Message for interaction" to internal team
  	And user "staff1" open conversation with "Bery" from "Company Inbox" with group name "All"
  	#Then user "staff1" should see the "message" from user "valid"
  	#When user "staff1" send message "Message for interaction" to internal team
  	#Then user "valid" should see the "message" from user "staff1" 