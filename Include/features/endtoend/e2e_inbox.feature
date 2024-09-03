@e2e
Feature: End to end testing for inbox feature
  This feature end to end sending message from web and receive it via mobile

  Background: 
    Given I open Sleekflow "v2"
    And I log in using "valid" credential

  @p0
  Scenario: User able to send message and customer able receive it
    Given I open conversation with "Bery" from "Company Inbox" with group name "All"
    When I send message "Message from QA Automation" to customer
    Then I should see the "message" sent to customer
    When I open "whatsapp" app in mobile
    Then I should see message in whatsapp app mobile from channel "SleekFlow Demo 5" is received
