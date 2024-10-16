@mobile @login
Feature: Mobile - Login Feature
  Login and Logout Feature using Mobile app v2

  Background: 
    Given I open "sleekflow" app in mobile
	
	@p0
  Scenario: Log in using mobile valid credential
    Given I log in using mobile "admin1" credential
    Then I should be ON "inbox" mobile page
    When I log out from sleekflow mobile
    Then I should be ON "login" mobile page
