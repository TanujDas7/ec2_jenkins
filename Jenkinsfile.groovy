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
                        sh script:''' 
                        echo "zip htmls in utils"
                        mkdir -p utils
                        zip zipfile: 'utils.zip', archive: false, dir: 'templates'
                        ''' 
                    }
                }
            stage("ec2"){
                steps{
                    sshagent(credentials : ['ec2_demo3']) {
                        sh script:'''
                        ssh -o StrictHostKeyChecking=no ec2-user@65.2.4.132 '
                        chmod 777 /var/www/html
                        '
                        scp utils.zip ec2-user@65.2.4.132:/var/www/html
                        ssh -o StrictHostKeyChecking=no ec2-user@65.2.4.132 '
                        chmod 777 /var/www/html
                        cd /var/www/html
                        unzip -q utils.zip -d .
                        '
                        '''
                    }
                }
            }
    }
}