pipeline {
    agent any
    triggers { pollSCM('* * * * *') }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/fmorenohealth/jgsu-spring-petclinic.git', branch: 'main'
                //git url: 'https://github.com/g0t4/jgsu-spring-petclinic.git', branch: 'main'
            }            
        }
        stage('Compile') {
            steps {
            sh './mvnw clean compile'
            }
        }
        stage('Test') {
            steps {
           sh './mvnw test'
            }
        }
        stage('Package') {
            steps {
                sh './mvnw clean package'
                //sh 'false'    // true   // add comment2
            }
        
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                //}
                //changed {
                    emailext subject: "Job \'${JOB_NAME}\' (build ${BUILD_NUMBER}) ${currentBuild.result}",
                        body: "Please go to ${BUILD_URL} and verify the build", 
                        attachLog: true, 
                        compressLog: true, 
                        to: "test@jenkins",
                        recipientProviders: [upstreamDevelopers(), requestor()]
                }
            }
        }
    }
}
