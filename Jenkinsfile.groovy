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
                            if (!fileExists('utils.zip')) {
                                sh script:'''
                                echo "zip htmls in utils"
                                cd templates && zip -r ../utils.zip *
                                '''
                            }
                        } 
                    }
                }
            stage("ec2"){
                steps{
                    sshagent(credentials : ['ec2_demo3']) {
                        sh script:'''
                        ssh -o StrictHostKeyChecking=no ec2-user@3.109.181.165 '
                        sudo chmod o+w /var/www/html
                        '

                        scp utils.zip ec2-user@3.109.181.165:/var/www/html -y

                        ssh -o StrictHostKeyChecking=no ec2-user@3.109.181.165 '
                        cd /var/www/html
                        unzip utils.zip -d .
                        '

                        '''
                    }
                }
            }
    }
}