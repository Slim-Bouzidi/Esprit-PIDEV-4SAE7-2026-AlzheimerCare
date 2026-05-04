pipeline {
    agent any
    
    options {
        disableConcurrentBuilds()
    }

    triggers {
        pollSCM('* * * * *') 
    }

    stages {
        stage('Detect & Trigger') {
            steps {
                script {
                    // Check which files changed in the last commit
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim()
                    echo "Changed files detected: ${changedFiles}"
                    
                    def backendChanged = changedFiles.contains('backend/')
                    def frontendChanged = changedFiles.contains('frontend/')
                    def keycloakChanged = changedFiles.contains('keycloak/')
                    def k8sChanged = changedFiles.contains('k8s/')

                    // Trigger Frontend Pipeline
                    if (frontendChanged) {
                        echo "Triggering Frontend Pipeline..."
                        build job: 'alzheimer-frontend', wait: false
                    }

                    // Trigger Backend CI Pipeline
                    if (backendChanged || keycloakChanged) {
                        echo "Triggering Backend CI Pipeline..."
                        build job: 'alzheimer-backend-ci', wait: false
                    }

                    // Trigger CD Pipeline if only K8s changed
                    // (Note: Backend/Frontend pipelines also trigger CD at their end)
                    if (k8sChanged && !backendChanged && !frontendChanged) {
                        echo "Triggering Deployment (CD) Pipeline..."
                        build job: 'alzheimer-backend-cd', wait: false
                    }
                }
            }
        }
    }
}
