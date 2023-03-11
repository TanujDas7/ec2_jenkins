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
                    sh script:'''
                    cd utils
                    chmod 400 mumbai.pem
                    ls -al
                    ssh -i mumbai.pem ec2-user@52.66.210.187 -y
                    cd /var/www/html
                    mv home.html index.html
                    scp -i mumbai.pem index.html ec2-user@52.66.210.187/var/www/html/
                    '''
                }
            }
    }
}