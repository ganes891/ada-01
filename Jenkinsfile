@Library('Jenkins-shared-library') _

pipeline {

    agent any
    parameters
    {   
        choice(name: 'environment', choices: 'AWS\nAZURE\nOnPrem', description: 'choose the cloud platform')
        choice(name: 'action', choices: 'create\ndeploy-k8s\niaccreate', description: 'choose create/Destroy')
        string(name: 'aws_account_id', description: " AWS Account ID", defaultValue: '599646583608')
        string(name: 'Region', description: "Region of ECR", defaultValue: 'ap-southeast-1')
        string(name: 'cluster', description: "name of the EKS Cluster", defaultValue: 'SAP-dev-eksdemo')
    }
 
    environment{
        JOB_NAME = 'dev-proj1'
        IMAGE_TAG = 'v2.3'
        BRANCH = 'main'
        AWS_ACCOUNT_ID= '599646583608'
        AWS_DEFAULT_REGION= 'ap-southeast-1'
        IMAGE_NAMESPACE='ada01'
        EKS_CLUSTER_NAME = 'SAP-dev-eksdemo'
        PYTHON_BE_01 = 'service-a'
        JAVA_BE_01 = 'service-c'
        EKS_TF_DIR = 'infra/eks-admin-tf/01-ekscluster-terraform-manifests'
        EKS_APP_MANIFEST_DIR = 'application/manifests'
        GITHUB_CREDENTIAL = 'github-ID' //'9db7a662-10fb-49ba-8b48-b9adcd66236d'
        GIT_URL = 'https://github.com/ganes891/ada-01.git'
    }

    stages{
       stage('Quality Gate Sonarqube'){
               when{expression{params.action == "create"}}      
            steps{
               script {
                    def microservices = [
                        'service-c': './service-c', 
                    ]
                        microservices.each { serviceName, serviceDir ->
                        def imageName = "${serviceName}"
                        echo "🚀 Building and pushing image for ${serviceName} from directory ${serviceDir}"
                        dir(serviceDir) {
                   def SonarQubecredentialsId = 'SonarQubeapi'
                   staticCodeAnalysis(SonarQubecredentialsId)
               }
            }
          }
        }
       }
      stage('Java-Build and Push Microservices') {
         when{expression{params.action == "create"}} 
            steps {
                script {
                    // List of microservices with their corresponding directories
                    def microservices = [
                        'service-c': './service-c', 
                        //'service-b': './service-b'   
                    ]
                        microservices.each { serviceName, serviceDir ->
                        def imageName = "${serviceName}"
                        
                        echo "🚀 Building and pushing image for ${serviceName} from directory ${serviceDir}"
                        
                        dir(serviceDir) {
                            def dockerfileDir = "."
                            //staticCodeAnalysis(imageName)
                            mvnBuild()
                            dockerBuild(imageName, dockerfileDir)
                            dockerImagePushEcr(imageName)
                            //dockerImagePush(imageName)
                            dockerImageClean(imageName)

                        }
                    }
                }
            }
      }

      stage('Python-Build Push Microservices') {
         when{expression{params.action == "create"}} 
            steps {
                script {
                    // List of microservices with their corresponding directories
                    def microservices = [
                        'service-a': './service-a', 
                        'service-b': './service-b'   
                    ]
                    // Loop through each microservice
                    microservices.each { serviceName, serviceDir ->
                        // Construct the image name based on the registry and service name
                        def imageName = "${serviceName}"

                        // Switch to the directory of each microservice and build the Docker image
                        echo "🚀 Building and pushing image for ${serviceName} from directory ${serviceDir}"

                        // Using `dir()` to switch to the respective directory
                        dir(serviceDir) {
                            // Call the dockerBuild function
                            def dockerfileDir = "."
                            dockerBuild(imageName, dockerfileDir)
                            dockerImagePushEcr(imageName)
                            //dockerImagePush(imageName)
                            dockerImageClean(imageName)
                           
                        }
                    }
                }
            }
      }
      stage('IAC create aws-k8s Terraform'){
         when{expression{params.action == "iaccreate"}}       
            steps{
               script{
                   dir("${EKS_TF_DIR}")
                   { 
                    createInfraAws(IMAGE_TAG)
               }   
            }
        }
      }

      stage('Connect EKS cluster:Terraform'){
         when{expression{params.action == "iaccreate"}}       
            steps{
               script{  
                    //dockerImagePush("${params.ImageName}","${params.ImageTag}","${params.DockerHubUser}")
                    connectAws(IMAGE_TAG)
               }
            }
      }
    
      stage('Deploy AWS K8s'){
         when{expression{params.action == "deploy-k8s" && params.environment == "AWS"}}       
         steps{
               script{
                dir("${EKS_APP_MANIFEST_DIR}") {
                  deployInK8S1(IMAGE_TAG) 

                }
               }   
            }
       }
      stage('Deployment of On prem infra'){
         when{expression{params.action == "deploy-k8s" && params.environment == "OnPrem"}}       
         steps{
               script{
               sh "sudo podman rm -f service-a"
               sh "sleep 1"
               sh "sudo podman rm -f service-b"
               sh "sleep 1"
               sh "sudo podman run -dit -p 5001:5000 --name  service-a quay.io/ganesan_kandasamy/ada01/service-a:${IMAGE_TAG}"
               sh "sudo podman run -dit -p 5002:5000 --name  service-b quay.io/ganesan_kandasamy/ada01/service-b:${IMAGE_TAG}"
               sh "sudo podman rm -f service-c"
               sh "sudo podman run -dit -p 5003:8080 --name  service-c quay.io/ganesan_kandasamy/ada01/service-c:${IMAGE_TAG}"

               }
         }
      }
        

    stage('cleanup workspace'){
       steps{
        cleanWs()
        }
      }

   }
}