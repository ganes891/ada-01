@Library('Jenkins-shared-library') _

pipeline {

    agent any
    parameters
    {   
        choice(name: 'CloudName', choices: 'AWS\nAZURE', description: 'choose the cloud platform')
        choice(name: 'action', choices: 'create\ndelete\nbuildonly', description: 'choose create/Destroy')
        string(name: 'aws_account_id', description: " AWS Account ID", defaultValue: '599646583608')
        string(name: 'Region', description: "Region of ECR", defaultValue: 'ap-southeast-1')
        string(name: 'ImageName', description: "name of the docker build", defaultValue: 'myapp02')
        string(name: 'ImageTag', description: "tag of the docker build", defaultValue: 'v2.1')
        string(name: 'cluster', description: "name of the EKS Cluster", defaultValue: 'SAP-dev-eksdemo')
    }
 
    environment{
        DOCKER_IMAGE = 'myapp01'
        PROJECT = '${ImageName}'
        IMAGE_TAG = '${ImageTag}'
        BRANCH = 'main'
        AWS_ACCOUNT_ID= '599646583608'
        AWS_DEFAULT_REGION= 'ap-southeast-1'
        IMAGE_REPO_NAME= 'dev-project'
        CLUSTER_NAME = 'xyz'
        PYTHON_BE_01 = 'python-app-be-01'
        JAVA_BE_01 = 'java-app-be-01'
        EKS_TF_DIR = 'infra/eks-admin-tf/01-ekscluster-terraform-manifests'
        GITHUB_CREDENTIAL = 'github-ID' //'9db7a662-10fb-49ba-8b48-b9adcd66236d'
        //APP_REPO = 'ganes891/jenklib.git'
        GIT_URL = 'https://github.com/ganes891/ada-01.git'
    }
   
    stages{
      stage('Git Checkout'){
         when{expression{params.action == "create"}}    
            steps{
              script{
               // git branch: "${BRANCH}", credentialsId: "${GITHUB_CREDENTIALS}", url: "${GIT_URL}"
                //gitCheckout(PROJECT)
                sh "pwd"
                
              }
            }
       }
         /* 
       stage('Unit Test maven'){
               when{expression{params.action == "create"}}      
            steps{
               script{
                   
                   mvnTest()
               }
            }
        }*/
       
       /* stage('Integration Test maven'){
              when{expression{params.action == "create"}}       
            steps{
               script{
                   
                   mvnIntegrationTest()
               }
            }
        }*/
        
        
    /*stage('Static Code Analysis: Sonarqube'){
               when{expression{params.action == "create"}}      
            steps{
               script{
                   def SonarQubecredentialsId = 'SonarQubeapi'
                   staticCodeAnalysis(SonarQubecredentialsId)
               }
            }
        }
       
      stage('Quality Gate status check: Sonarqube'){
               when{expression{params.action == "create"}}      
            steps{
               script{
                   def SonarQubecredentialsId = 'SonarQubeapi'
                   staticCodeAnalysis(SonarQubecredentialsId)
               }
            }
        }*/
        
      stage('Maven build: maven'){
         when{expression{params.action == "create"}}       
            steps{
               script{
                   
                    mvnBuild()
                    //sh "pwd"
               }
            }
        }
        
         
      stage('Java BE docker build'){
         when{expression{params.action == "create"}}       
            steps{
               script{
                   
                   // dockerBuild("${params.ImageName}","${params.ImageTag}","${params.DockerHubUser}")
                    def imageName = params.JAVA_BE_01
                    def dockerfileDir = "./${params.JAVA_BE_01}"
                    dockerBuild(imageName, dockerfileDir)     
               }
            }
      }
    
      stage('Docker Image Push'){
          when{expression{params.action == "create"}}       
            steps{
               script{
                   
                    //dockerImagePush("${params.ImageName}","${params.ImageTag}","${params.DockerHubUser}")
                    dockerImagePushEcr(PROJECT)
               }
            }
      }
    
      stage('Python BE docker build'){
         when{expression{params.action == "create"}}       
            steps{
               script{
                   //dir("${PYTHON_BE_01}")
                  def imageName = params.PYTHON_BE_01
                  def dockerfileDir = "./${params.PYTHON_BE_01}"
                  dockerBuild(imageName, dockerfileDir)
            }
        }
      }

      stage('Python BE docker Push'){
         when{expression{params.action == "create"}}       
            steps{
               script{
                   
                  //dockerImagePush("${params.ImageName}","${params.ImageTag}","${params.DockerHubUser}")
                  dockerImagePushEcr(PROJECT)
               }
            }
      }

      stage('Connect to EKS cluster: Terraform'){
         when{expression{params.action == "create"}}       
            steps{
               script{
                   
                    //dockerImagePush("${params.ImageName}","${params.ImageTag}","${params.DockerHubUser}")
                    connectAws(PROJECT)
               }
            }
      }

      stage('Create EKS cluster using IAAC: Terraform'){
         when{expression{params.action == "create"}}       
            steps{
               script{
                   dir("${EKS_TF_DIR}")
                   { 
                    createInfraAws(PROJECT)
               }   
            }
        }
      }
    
       /* stage('Connect to EKS cluster: Terraform'){
              when{expression{params.action == "create"}}       
            steps{
               script{
                  sh """
                  aws configure set aws_access_key_id "$ACCESS_KEY"
                  aws configure set aws_secret_access_key "$SECRET_KEY"
                  aws configure set region "${params.Region}"
                  aws eks --region ${params.Region} update-kubeconfig --name ${params.cluster}
                  """
               }   
            }
        }*/

      stage('Deployment of EKS cluster: Terraform'){
         when{expression{params.action == "delete"}}       
         steps{
               script{
                    
                  def apply = false

                  try{
                    input message: 'please confirm to deploy on eks', ok: 'Ready to apply the config ?'
                    apply = true
                  }catch(err){
                    apply= false
                    currentBuild.result  = 'UNSTABLE'
                  }
                  if(apply){

                    sh """
                      kubectl apply -f .
                    """
                  }
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