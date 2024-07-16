@login
Feature: V2 - Login Feature
  Login and Logout Feature using v2

  Background: 
    Given I open Sleekflow "v2"

  Scenario Outline: Log in using <user> credential
    When I log in using "<user>" credential
    Then I should be on "<page>" page

    @positive @p0
    Examples: 
      | user  | page  |
      | valid | inbox |

    @negative @p1
    Examples: 
      | user    | page  |
      | invalid | login |
