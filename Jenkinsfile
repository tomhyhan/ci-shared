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
                    sh "docker build -t ${dockerRepoName}:latest --tag tomhyhan/${dockerRepoName}:${imageName} ."
                    sh "docker push tomhyhan/${dockerRepoName}:${imageName}"
                }
            }
        }

    }
    }
}