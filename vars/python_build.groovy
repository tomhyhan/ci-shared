def call(dockerRepoName, imageName, app) {
    pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh "pip install -r ${app}/requirements.txt"
            }
        }
        stage('Python Lint') {
            steps {
                echo "hello"
                sh "pylint-fail-under --fail_under 5.0 ${app}/app.py"
            }
        }
        stage('Package') {
            steps {
                withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                    sh "docker login -u tomhyhan -p '$TOKEN' docker.io"
                    sh "docker build -t ${app}/${dockerRepoName}:latest --tag tomhyhan/${dockerRepoName}:${imageName} ./${app}/."
                    sh "docker push tomhyhan/${dockerRepoName}:${imageName}"
                }
            }
        }
        stage('Scan') {
            steps {
                withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                    sh "docker login -u tomhyhan -p '$TOKEN'"
                    sh "docker scan --accept-license tomhyhan/${dockerRepoName}:${imageName}  > ${app}.txt"
                    sh "zip app.zip ${app}.txt"
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'app.zip', 
                    onlyIfSuccessful: true
                }
            }
        }
    }
    }
}