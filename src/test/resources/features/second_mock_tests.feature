Feature: Mock tests to show the framework run

  @mocks
  Scenario: Run some mock tests 2
    Given I want to run a test
    When I run the test
    Then I see that the test is passing

  @mocks
  Scenario: Run more mock tests 2
    Given I want to run a second test
    When I run the test
    Then I see that the test is passing

