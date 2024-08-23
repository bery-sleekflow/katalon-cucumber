@authentication
Feature: V2 - Authentication Feature
  Login and Logout Feature using v2

  Background: 
    Given I open Sleekflow "v2"
	
	@positive @p0
  Scenario: Login and logout using valid credential
    When I log in using "valid" credential
    Then I should be on "inbox" page
    When I log out from Sleekflow web
    Then I should be on "login" page

  @negative @p1
  Scenario: Login using invalid credential
    When I log in using "invalid" credential
    Then I should be on "login" page