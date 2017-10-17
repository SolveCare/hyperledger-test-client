pipeline {
  agent any
  stages {
    stage('maven package') {
      steps {
        sh './mvnw clean package'
      }
    }
  }
}