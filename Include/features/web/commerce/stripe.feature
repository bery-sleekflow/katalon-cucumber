@commerce @stripe
Feature: Commerce - Stripe
  Stripe payment

  Background: 
    Given I open Sleekflow "v2"
    And I log in using "valid" credential

  @p0
  Scenario: User able to generate Custom Payment Link
    Given I open conversation with "Bery" from "Company Inbox" with group name "All"
    When I generate "Custom" payment link with "single" product
    Then I should see text "Hey, Check out with this payment link !" in textbox chat
    And I am able to send the message to customer 
		Then I should see the "payment link" sent to customer