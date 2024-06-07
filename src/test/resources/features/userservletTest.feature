
Feature: User Servlet Api
  
	Scenario: Insert new user
		Given the user accesses the insert form
		And the user enters valid data
		When the user submit the form
		Then the user is inserted into the database
		And the user is redirected to the list page
  		 
  		
