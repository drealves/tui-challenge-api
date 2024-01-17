pipeline {
    agent any

    tools {
        maven 'Maven' // Replace 'Maven' with your Maven installation name
        jdk 'Java'    // Replace 'Java' with your JDK installation name
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/drealves/tui-challenge-api.git', branch: 'main'
            }
        }

        stage('Build | test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    // Building the Docker image
                    sh 'docker build -t tui-challenge-api:latest .'
                }
            }
        }

        stage('Docker Run') {
            steps {
                script {
                    // Running the Docker container
                    sh 'docker run -d -p 8080:8080 --name tui-challenge-api tui-challenge-api:latest'
                }
            }
        }

    }

    post {
        always {
            echo 'Pipeline completed.'
            // Clean up: Stop and remove the Docker container
            sh 'docker stop tui-challenge-api || true'
            sh 'docker rm tui-challenge-api || true'
        }

        success {
            echo 'Pipeline succeeded.'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}