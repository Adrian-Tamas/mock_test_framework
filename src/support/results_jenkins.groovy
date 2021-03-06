def reportOnTestsForBuild() {
    def build = manager.build
    manager.listener.logger.println("Build Number: ${build.number}")

    manager.listener.logger.println("Manager class: ${manager}")

    def total_tests = manager.build.testResultAction.getTotalCount()
    manager.listener.logger.println("Results: ${total_tests}")

    def failed_count = manager.build.testResultAction.getFailCount();
    def failed = manager.build.testResultAction.getFailedTests()

    def skip_count = manager.build.testResultAction.getSkipCount();
    def passed = total_tests - skip_count - failed_count

    manager.listener.logger.println("Failed Count: ${failed_count}")
    manager.listener.logger.println("Failed Tests: ${failed}")
    manager.listener.logger.println("Skip Count: ${skip_count}")
    manager.listener.logger.println("Pass count: ${passed}")

    def matcher = manager.getLogMatcher(".*Total time: (.*)\$")
    if(matcher?.matches()) {
        manager.listener.logger.println("Duration: ${matcher.group(1)}")
    }

    def matcher2 = manager.getLogMatcher(".*Finished at: (.*)\$")
    if(matcher2?.matches()) {
        manager.listener.logger.println("Execution end: ${matcher2.group(1)}")
    }

    def matcher3 = manager.getLogMatcher(".*Tests run: (.*)\$")
    if(matcher3?.matches()) {
        manager.listener.logger.println("Test run info: ${matcher3.group(1)}")
    }
}

reportOnTestsForBuild()

def sendTestStatistics() {
    def ingestion_service_url = "http://localhost:9090/results"

    // Test execution constants
    def app_name = "App4"
    def bu_name = "UK"
    def region_name = "UK"
    def execution_type = "sanity"

    // gather the test statistics
    def total_tests = manager.build.testResultAction.getTotalCount()
    def failed_count = manager.build.testResultAction.getFailCount()
    def skip_count = manager.build.testResultAction.getSkipCount()
    def passed_count = total_tests - skip_count - failed_count
    def total_time
    def execution_end_time


    // For minutes: Total time:  01:14 min
    // For seconds: Total time:  16.311 s
    def matcher = manager.getLogMatcher(".*Total time: (.*)\$")
    if(matcher?.matches()) {
        total_time = matcher.group(1)
    }

    def matcher2 = manager.getLogMatcher(".*Finished at: (.*)\$")
    if(matcher2?.matches()) {
        execution_end_time = matcher2.group(1)
    }

    // build the statistics payload
    def builder = new groovy.json.JsonBuilder()
    def test_statistics = builder {
        application app_name
        bu bu_name
        region region_name
        executionType execution_type
        numberOfFeatureFiles 0
        numberOfScenarios total_tests
        numberOfPasses passed_count
        numberOfFailures failed_count
        executionEndTime execution_end_time
        executionDuration total_time
    }

    manager.listener.logger.println("json: ${builder.toString()}")

    def post = new URL(ingestion_service_url).openConnection();
    def message = builder.toString()
    post.setRequestMethod("POST")
    post.setDoOutput(true)
    post.setRequestProperty("Content-Type", "application/json")
    post.getOutputStream().write(message.getBytes("UTF-8"));
    def postRC = post.getResponseCode();
    if(!postRC.equals(200)) {
        manager.listener.logger.println("GROOVY POST BUILD SCRIPT: There was a problem doing the request to the Data Ingestion service");
        manager.listener.logger.println("Response status code: ${postRC}");
        manager.addWarningBadge("Groovy Post Build Script failed to send the results")
        manager.createSummary("warning.gif").appendText("<h4>Groovy script failed to send the data to the Data Ingestion Service</h4>",
                false, false, false, "red")
    }
}

sendTestStatistics()