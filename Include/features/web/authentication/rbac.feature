@rbac
Feature: Check permission access based on role 

  @p0
  Scenario Outline: Access check main menu for user <role>
    Given I open Sleekflow "v2"
    When I log in using "<user>" credential
    Then the access main menu is correct based on role "<role>"

    Examples: 
      | role  			| user				|
      | admin	 			| admin1		  |
      | team admin 	| teamadmin1	|
      | staff				| staff1			|