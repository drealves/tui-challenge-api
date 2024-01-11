pipeline {
    agent any

    environment {
        // Define environment variables for AWS and Docker credentials
        AWS_DEFAULT_REGION = 'us-west-2'
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        DOCKER_REGISTRY = 'your-account-id.dkr.ecr.your-region.amazonaws.com'
        REPOSITORY_NAME = 'my-app'
        IMAGE_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                // Get the latest source code from SCM (e.g., Git)
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                // Build the application using Maven
                sh 'mvn clean package'
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    // Log in to AWS ECR
                    sh 'aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $DOCKER_REGISTRY'
                    // Build the Docker image
                    sh 'docker build -t $REPOSITORY_NAME:$IMAGE_TAG .'
                    // Tag the Docker image
                    sh 'docker tag $REPOSITORY_NAME:$IMAGE_TAG $DOCKER_REGISTRY/$REPOSITORY_NAME:$IMAGE_TAG'
                    // Push the Docker image to ECR
                    sh 'docker push $DOCKER_REGISTRY/$REPOSITORY_NAME:$IMAGE_TAG'
                }
            }
        }

        stage('Deploy to AWS') {
            steps {
                script {
                    // Deploy the CloudFormation stack for ECS/Fargate
                    sh 'aws cloudformation deploy --template-file ecs-fargate-cf.yml --stack-name my-ecs-stack --capabilities CAPABILITY_IAM'
                    // Deploy the CloudFormation stack for API Gateway
                    sh 'aws cloudformation deploy --template-file apigateway-cf.yml --stack-name my-api-stack --capabilities CAPABILITY_IAM'
                }
            }
        }
    }

    post {
        success {
            // Actions to perform if the pipeline succeeds
        }
        failure {
            // Actions to perform if the pipeline fails
        }
    }
}
