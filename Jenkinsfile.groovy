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
                    withCredentials([[
                        $class:'AmazonWebServicesCredentialsBinding',
                        credentialsId: 'aws',
                        accessKeyVariable:'AWS_ACCESS_KEY_ID',
                        sectKeyVariable:'AWS_SECRET_ACCESS_KEY',
                    ]]){
                        sh script:'''
                        cd utils
                        chmod 400 chmod.pem
                        ls -al
                        ssh -i chmod.pem ec2-user@3.109.59.247 -y
                        cd /var/www/html
                        mv home.html index.html
                        scp -i chmod.pem index.html ec2-user@3.109.59.247/var/www/html/
                        '''
                    }
                }
            }
    }
}