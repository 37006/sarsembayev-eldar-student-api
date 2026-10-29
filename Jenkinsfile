pipeline {
    agent any
    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }
    triggers { pollSCM('* * * * *') }
    environment {
        IMAGE = 'sarsembayev-eldar-student-api:v1.0-sarsembayev-eldar-37006'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'printf "Student: Eldar Sarsembayev | IT2-2301 | 37006\\n"; git rev-parse HEAD; java -version; docker version --format "{{.Server.Version}}"'
            }
        }
        stage('Build') {
            steps { sh './gradlew --no-daemon clean classes testClasses bootJar' }
        }
        stage('Test') {
            steps { sh './gradlew --no-daemon test' }
            post { always { archiveArtifacts allowEmptyArchive: true, artifacts: 'build/test-results/test/*.xml' } }
        }
        stage('Docker Build') {
            steps { sh 'docker build -t "$IMAGE" .' }
        }
        stage('Verify') {
            steps {
                sh '''
                    set -eu
                    docker image inspect "$IMAGE" --format 'Image={{.Id}} Size={{.Size}} Created={{.Created}}'
                    printf 'PASS: personalized Docker image exists\\n'
                '''
            }
        }
    }
    post {
        success { echo 'PASS: Eldar Sarsembayev Student API build, tests and image verification' }
        failure { echo 'FAIL: inspect the first failing stage; later stages are skipped' }
    }
}
