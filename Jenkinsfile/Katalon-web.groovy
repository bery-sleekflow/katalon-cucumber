pipeline {
    agent any
    
    environment {
        KATALON_VERSION = '9.7.2' // Specify the Katalon version
        KATALON_API_KEY = credentials('KatalonApiKey') // API Key stored in Jenkins credentials
        PROJECT_PATH = '/katalon/project/e2e-web.prj' // Path to your Katalon project
        TEST_SUITE_PATH = 'Test Suites/Regression Web' // Katalon Test Suite path
        EXECUTION_PROFILE = 'CICD' // Execution profile
        BROWSER_TYPE = 'Chrome' // Browser type
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Check out your code from your repository
                checkout scm
            }
        }

        stage('Install Katalon') {
            steps {
                // Download and extract Katalon Runtime Engine
                sh """
                curl -L -o katalon.tar.gz https://github.com/katalon-studio/katalon-studio/releases/download/v${KATALON_VERSION}/Katalon_Studio_Engine_MacOS_64-${KATALON_VERSION}.tar.gz
                tar -xzf katalon.tar.gz
                """
            }
        }

        stage('Run Katalon Test Suite') {
            steps {
                // Execute Katalon Test Suite using Katalon Runtime Engine
                sh """
                ./Katalon_Studio_Engine_MacOS_64-${KATALON_VERSION}/katalonc \
                -noSplash -runMode=console \
                -projectPath="${PROJECT_PATH}" \
                -retry=0 \
                -testSuitePath="${TEST_SUITE_PATH}" \
                -executionProfile="${EXECUTION_PROFILE}" \
                -browserType="${BROWSER_TYPE}" \
                -apiKey="${KATALON_API_KEY}" \
                --config -webui.autoUpdateDrivers=true
                """
            }
        }

        stage('Archive Test Reports') {
            steps {
                // Archive test results and reports
                archiveArtifacts artifacts: '**/Reports/**', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            cleanWs() // Clean the workspace after the build completes
        }
    }
}