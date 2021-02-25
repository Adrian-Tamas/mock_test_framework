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
    }
}