#!groovy

pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                bat 'mvn clean test'
            }
        }

        stage('Generate HTML report') {
            steps {
                cucumber buildStatus: 'UNSTABLE',
                        reportTitle: 'My report',
                        fileIncludePattern: '**/*.json'
            }
        }

        stage('Send results to the Data ingestion service') {
            steps {
                script {
                    testResults()
                }
            }
        }
    }
}

def testResults() {
    def summary = junit testResults: '/target/*-reports/TEST-*.xml'
    test(summary)
}

@NonCPS
def test(summary) {
    def ingestion_service_url = "http://localhost:9090/results"

    // Test execution constants
    def app_name = "App4"
    def bu_name = "UK"
    def region_name = "UK"
    def execution_type = "sanity"

    // gather the test statistics
    def total_tests = summary.totalCount
    def failed_count = summary.failCount
    def passed_count = summary.passCount
    def total_time
    def execution_end_time


    // For hours:  Total time:  01:10 h
    // For minutes: Total time:  01:14 min
    // For seconds: Total time:  16.311 s
    def duration =  currentBuild.getDuration()
    def minutes = (int)(duration / 60000)
    def seconds = (int)(duration / 1000)
    total_time = "${minutes}:${seconds} min"
    echo 'Duration: '+ total_time

    def start_time = currentBuild.getStartTimeInMillis()
    def date = new Date(start_time).format("yyyy-MM-dd'T'HH:mm:ssXXX")
    echo 'Start Time ' + date

    // build the statistics payload
    def builder = new groovy.json.JsonBuilder()
    def test_statistics = builder {
        'application' app_name
        'bu' bu_name
        'region' region_name
        'executionType' execution_type
        'numberOfFeatureFiles' 0
        'numberOfScenarios' total_tests
        'numberOfPasses' passed_count
        'numberOfFailures' failed_count
        'executionEndTime' date
        'executionDuration' total_time
    }

    echo "json: ${builder.toString()}"

    def post = new URL(ingestion_service_url).openConnection();
    def message = builder.toString()
    post.setRequestMethod("POST")
    post.setDoOutput(true)
    post.setRequestProperty("Content-Type", "application/json")
    post.getOutputStream().write(message.getBytes("UTF-8"));
    def postRC = post.getResponseCode();
}
