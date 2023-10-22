pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "maven"
    }

    stages {
        stage('Build') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/dev']],
                doGenerateSubmoduleConfigurations: false,
                extensions: [], submoduleCfg: [],
                userRemoteConfigs: [[url: 'https://github.com/VaidasVa/MovieRecommendationService.git']]])

                bat "mvn clean install -DskipTests=true"
            }
        }
        stage('Test'){
            steps{
                bat "mvn clean install -DskipTests=true"
            }
        }
    }
}
