pipeline{
    agent { node { label "${env.AGENT_LABEL}"}}
    stages{
        stage('code checkout') {
                    echo "git clone"
                    git branch: 'master', credentialsId: 'd9571958-53a3-4fc3-b83a-d9807504e93d', url: 'https://github.com/TanujDas7/job.git'
                }
            stage("package"){
                    echo "html file content"
                        echo "$pwd"
                        echo "$WORKSPACE"
                    }
            stage("ec2"){
                    echo "write into ec2"
                    // sh "sh scripts.sh"
                }
    }
}