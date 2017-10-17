pipeline {
  agent any
  stages {
    stage('maven package') {
      steps {
        sh './mvnw clean package'
      }
    }
    stage('build docker container') {
      steps {
        sh './mvnw docker:build'
      }
    }
  }
}