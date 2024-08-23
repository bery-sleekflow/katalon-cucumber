@mobile @login
Feature: Mobile - Login Feature
  Login and Logout Feature using Mobile app v2

  Background: 
    Given I open Sleekflow "Mobile"

  Scenario Outline: Log in using mobile <user> credential
    When I log in using mobile "<user>" credential
    Then I should be on mobile "<page>" page

    @positive @p0
    Examples: 
      | user  | page  |
      | valid | inbox |

    #@negative @p1
    #Examples: 
    #  | user    | page  |
    #  | invalid | login |
