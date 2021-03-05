package com.example.mock_framework.bdd;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import static java.lang.Thread.sleep;

@SpringBootTest
public class MockSteps {

    @Given("I want to run a test")
    public void iWantToRunATest() {

    }

    @Given("I want to run a second test")
    public void iWantToRunASecondTest() {
    }

    @When("I run the test")
    public void iRunTheTest() {
    }

    @Then("I see that the test is passing")
    public void theTestPasses() {
        Assert.assertTrue(true);
    }

}
