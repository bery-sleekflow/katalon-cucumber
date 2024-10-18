pipeline {
    agent any

    environment {
        KATALON_VERSION = '9.7.2' // Katalon version
        KATALON_API_KEY = credentials('KatalonApiKey') // Jenkins credentials for Katalon API Key
        PROJECT_PATH = '/katalon/project/e2e-web.prj' // Path to Katalon project
        TEST_SUITE_PATH = 'Test Suites/Regression Web' // Test suite to run
        EXECUTION_PROFILE = 'CICD' // Katalon execution profile
        BROWSER_TYPE = 'Chrome' // Browser type
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Checkout the code from repository
                checkout scm
            }
        }

        stage('Run Katalon in Docker') {
            steps {
                // Run Katalon tests inside a Docker container
                script {
                    docker.image("katalonstudio/katalon:${KATALON_VERSION}").inside {
                        sh """
                        katalonc.sh \
                        -noSplash \
                        -runMode=console \
                        -projectPath=${PROJECT_PATH} \
                        -retry=0 \
                        -testSuitePath=${TEST_SUITE_PATH} \
                        -executionProfile=${EXECUTION_PROFILE} \
                        -browserType=${BROWSER_TYPE} \
                        -apiKey=${KATALON_API_KEY} \
                        --config -webui.autoUpdateDrivers=true
                        """
                    }
                }
            }
        }

        stage('Archive Test Results') {
            steps {
                // Archive Katalon reports
                archiveArtifacts artifacts: '**/Reports/**', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            // Clean workspace after build
            cleanWs()
        }
    }
}