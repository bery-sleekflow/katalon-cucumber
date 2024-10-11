@channel
Feature: Channel Feature
	
	Background: 
    Given I open Sleekflow "v2"
    And I log in using "admin1" credential
    
  @p0
  Scenario Outline: Rename channel <channel>
    When I rename channel "<channel>" with name "<name1>" to "<name2>"
    Then channel name succesfully change to "<name2>"

    Examples: 
      | channel  						| name1 										| name2    					 											|
      | Facebook Messenger 	| SF V2 Second Testing Page | Fb messenger from automated regression 	|
      | WhatsApp 360dialog 	| 360 dialog 								| 360 from automated regression 					|
      