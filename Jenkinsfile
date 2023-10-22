pipeline {

    agent any

    tools{
        maven 'maven'
    }

    stages {
        stage('Build') {
            steps {
                git 'https://github.com/VaidasVa/MovieRecommendationService.git'
                sh 'maven clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'maven test'
            }
        }
    }
}