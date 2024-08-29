@contact
Feature: Create contact feature
  Contact creation

  Background: 
    Given I open Sleekflow "v2"
    And I log in using "valid" credential

  @p0 @create
  Scenario Outline: Admin create contact with <case>
    When I create contact with "<case>"
		Then contact is created successfully with "<case>"
		# for cleaning the contact
		#And I delete the created contact 
		
		Examples: 
      | case  						| 
      | phone number only |  
      | email only 				|