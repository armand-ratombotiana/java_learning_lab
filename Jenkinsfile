pipeline {
    agent any
    stages {
        stage('Data Validation') {
            steps { sh 'java -cp . com.mlops.lab09.DataValidationLab' }
        }
        stage('Feature Engineering') {
            steps { sh 'java -cp . com.mlops.lab04.FeatureStoreLab' }
        }
        stage('Model Training') {
            steps { sh 'java -cp . com.mlops.lab01.MLOpsPipelineOrchestrationLab' }
        }
        stage('Model Evaluation') {
            steps {
                script {
                    def accuracy = sh(script: 'echo 0.947', returnStdout: true).trim()
                    def champion = sh(script: 'echo 0.935', returnStdout: true).trim()
                    if (accuracy.toDouble() < champion.toDouble()) {
                        error "Model accuracy ${accuracy} below champion ${champion}"
                    }
                }
            }
        }
        stage('Deploy to Staging') {
            steps { sh 'echo "Deploying..."' }
        }
        stage('Integration Tests') {
            steps { sh 'echo "Running shadow tests..."' }
        }
        stage('Deploy to Production') {
            when { branch 'main' }
            steps { sh 'echo "Promoting to production..."' }
        }
    }
    post {
        failure { sh 'echo "Pipeline failed — notifying team"' }
        success { sh 'echo "Pipeline succeeded"' }
    }
}
