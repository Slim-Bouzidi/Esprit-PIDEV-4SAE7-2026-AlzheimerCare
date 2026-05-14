pipeline {
    agent any
    
    options {
        disableConcurrentBuilds()
    }

    triggers {
        pollSCM('* * * * *') // Trigered every minute to detect pushes on local VM
    }

    stages {
        stage('Detect & Trigger') {
            steps {
                script {
                    // Get changed files in last commit
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim().split('\n')
                    echo "Changed files detected: ${changedFiles}"
                    
                    def backendChanged = false
                    def frontendChanged = false
                    def keycloakChanged = false
                    def k8sChanged = false

                    for (file in changedFiles) {
                        if (file.startsWith('backend/') || file == 'Jenkinsfile.backend-ci') backendChanged = true
                        if (file.startsWith('frontend/') || file == 'Jenkinsfile.frontend') frontendChanged = true
                        if (file.startsWith('keycloak/')) keycloakChanged = true
                        if (file.startsWith('k8s/')) k8sChanged = true
                    }

                    if (frontendChanged) {
                        echo ">>> TRIGGERING FRONTEND PIPELINE"
                        build job: 'alzheimer-frontend', wait: false
                    }

                    if (backendChanged || keycloakChanged) {
                        echo ">>> TRIGGERING BACKEND CI PIPELINE"
                        build job: 'alzheimer-backend-ci', wait: false
                    }

                    if (k8sChanged && !backendChanged && !frontendChanged) {
                        echo ">>> TRIGGERING DEPLOYMENT (CD) PIPELINE"
                        build job: 'alzheimer-backend-cd', wait: false
                    }
                }
            }
        }
    }
}
