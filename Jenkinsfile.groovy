pipeline{
    agent { node { label "${env.AGENT_LABEL}"}}
    stages{
        stage('code checkout') {
            steps{
                echo "git clone"
                    git branch: 'master', credentialsId: 'd9571958-53a3-4fc3-b83a-d9807504e93d', url: 'https://github.com/TanujDas7/job.git'
            }
                }
            stage("package"){
                steps{
                        script{echo "zip pem and html"
                        sh "mkdir -p utils"
                        sh "mv home.html utils"
                        sh "mv mumbai.pem utils"}  
                    }
                }
            stage("ec2"){
                steps{
                    echo "unzip files from utils"
                    script{
                        sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                        sh 'unzip awscliv2.zip'
                        sh "./aws/install"
                        sh "aws configure --help"
                        // sh "chmod 400 mumbai.pem"
                        // sh "ssh -i mumbai.pem ec2-user@"
                    }
                }
            }
    }
}