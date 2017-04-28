pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh './gradlew assembleDebug'
      }
    }
    stage('') {
      steps {
        sh './run'
      }
    }
  }
}