@inbox @chat
Feature: Inbox - chat
  Inbox feature chat to customer and internal

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
   
  @p0 @filter
  Scenario Outline: User able to search conversation by <search>
    Given I open Sleekflow "v2"
    And I log in using "admin1" credential
    When I search conversation by "<search>" contains "<term>" from "Company Inbox" with group name "All"
    Then I should see the "<search>" is displayed in the "<list>"
    
    Examples: 
      | search       		| term   	| list   	|
      | phone number 		| 628564 	| people 	|
      | name 						| bery		| people	|
      | message content | Hello		| message |

  #@p1 @multiple_user_interaction
  #Scenario: 2 User interaction in inbox internal note
    #Given I open 2 browser and log in using "admin1" and "staff1"
    #When user "admin1" open conversation with "Bery" from "Company Inbox" with group name "All"
    #And user "admin1" send message "Message for interaction" to internal note
    #And user "staff1" open conversation with "Bery" from "My Inbox" with group name "Assigned to me"
    #Then user "staff1" should see the "message" from user "admin1"
    #When user "staff1" send message "Message for interaction" to internal note
    #Then user "admin1" should see the "message" from user "staff1"
    
  @p1 @multiple_user_interaction
  Scenario: 2 User interaction in inbox internal note
    Given I open 2 browser and log in using "admin1" and "teamadmin1"
    When user "admin1" open conversation with "Bery" from "Company Inbox" with group name "All"
    And user "admin1" send message "Message for interaction" to internal note
    And user "teamadmin1" open conversation with "Bery" from "My Inbox" with group name "Collaborations"
    Then user "teamadmin1" should see the "message" from user "admin1"
    When user "teamadmin1" send message "Message for interaction" to internal note
    Then user "admin1" should see the "message" from user "teamadmin1"

  