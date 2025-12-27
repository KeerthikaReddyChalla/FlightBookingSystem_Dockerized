pipeline {
    agent any

    tools {
        maven 'M3'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/KeerthikaReddyChalla/FlightBookingSystem_Dockerized.git'
            }
        }

        stage('Build JARs') {
            steps {
                bat 'mvn -version'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Images') {
            steps {
                bat '''
                docker build -t flight-api-gateway ./api-gateway
                docker build -t flight-auth-service ./auth-service
                docker build -t flight-booking-service ./booking-service1
                docker build -t flight-flight-service ./flight-service
                docker build -t flight-notification-service ./notification-service1
                docker build -t flight-config-server ./config-server1
                docker build -t flight-eureka-server ./eureka-server
                '''
            }
        }
    }
}
