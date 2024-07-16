@contact
Feature: V2 - Create contact
  Contact creation

  Background: 
    Given I open Sleekflow "v2"
    And I log in using "valid" credential

  @p0
  Scenario: Create contact with phone number only
    When I create contact with "phone number only"
		Then contact is created successfully with "phone number only"
