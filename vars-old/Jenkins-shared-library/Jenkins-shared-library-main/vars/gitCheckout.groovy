def call(project) {
    
    withCredentials([usernamePassword(
    credentialsId: "github-ID",
    usernameVariable: "USER",
    passwordVariable: "PASS"
     )])
    checkout([
        $class: 'GitSCM',
        branches: [[name: "${BRANCH}" ]],
        userRemoteConfigs: [[ url: "${GIT_URL}" , credentialsId: "${GITHUB_CREDENTIAL}"]]
    ])
  }
  
