@authentication
Feature: Authentication for mobile and web
  This is using shared step for mobile and web

  @p0
  Scenario Outline: Login and logout for <platform>
    Given I log in on "<platform>" using "admin1"
    Then I should see "inbox" page
    When I log out from sleekflow
    Then I should see "login" page

    Examples: 
      | platform |
      | web      |
      | mobile   |
