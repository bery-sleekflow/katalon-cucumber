@contact
Feature: V2 - Create contact
  Contact creation

  Background: 
    Given I open Sleekflow "v2"
    And I log in using "valid" credential

  @p0 @create @delete
  Scenario: Admin create contact with phone number only
    When I create contact with "phone number only"
		Then contact is created successfully with "phone number only"
		# for cleaning the contact
		#And I delete the created contact 
	
	@p0 @create @delete
  Scenario: Create contact with email only
    When I create contact with "email only"
		Then contact is created successfully with "email only"
		# for cleaning the contact
		#And I delete the created contact 