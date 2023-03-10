agent = "${env.AGENT_LABEL}";

pipeline{
    agent{ node{label "${agent}"}}
    stages{
        stage("code checkout"){
            steps{
                echo "git clone"
                git branch: 'master', credentialsId: 'd9571958-53a3-4fc3-b83a-d9807504e93d', url: 'https://github.com/TanujDas7/job.git'
            }
        }
        stage("package"){
            steps{
                echo "html file content"
                html_builder()
                sh "cat index.html"

            }
        }
        stage("ec2"){
            steps{
                echo "write into ec2"
                sh "sh scripts.sh"
            }
        }
    }
}

def html_builder = new groovy.xml.MarkupBuilder()
index.html {     
  head {         
    title"Home"     
  }     
  body {
    h1"home page"     
    p"lorem ipsum"
  } 
}​