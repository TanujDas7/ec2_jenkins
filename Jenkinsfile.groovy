pipeline{
    agent { node { label "${env.AGENT_LABEL}"}}
    stages{
        stage('code checkout') {
            steps{
                    echo "git clone"
                    git branch: 'master', credentialsId: 'd9571958-53a3-4fc3-b83a-d9807504e93d', url: 'https://github.com/TanujDas7/ec2_jenkins.git'
            }
        }
            stage("package"){
                steps{
                        script {
                                sh script:'''
                                echo "zip htmls in utils"
                                cd templates && zip -r ../utils.zip *
                                '''
                        } 
                    }
                }
            stage("ec2"){
                steps{
                    sshagent(credentials : ['ec2_demo3']) {
                        sh script:'''
                        ssh -o StrictHostKeyChecking=no ec2-user@43.205.128.135 '
                        sudo su
                        sudo yum update -y
                        sudo yum install httpd -y
                        sudo service httpd start
                        sudo chkconfig httpd on
                        sudo chmod o+w /var/www/html
                        '

                        scp utils.zip ec2-user@43.205.128.135:/var/www/html

                        ssh -o StrictHostKeyChecking=no ec2-user@43.205.128.135 '
                        cd /var/www/html
                        unzip -o utils.zip -d .
                        sudo service httpd restart
                        '

                        '''
                    }
                }
            }
    }
}