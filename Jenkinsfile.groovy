import java.nio.file.Files
import java.util.zip.*

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
                        zipper()
                        script{
                            sh "cd utils"
                            sh "ls"
                            sh "cd .."
                        }
                        
                    }
                }
            stage("ec2"){
                steps{
                    unzipper()
                    script{
                        sh "ls -al"
                    }
                }
            }
    }
}

def zipper(){
    String zipFileName = "utils.zip"
    String inputDir = "utils"
    ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zipFileName))  
    new File(inputDir).eachFile() { file -> 
    //check if file
    if (file.isFile()){
        zipFile.putNextEntry(new ZipEntry(file.name))
        def buffer = new byte[file.size()]  
        file.withInputStream { 
        zipFile.write(buffer, 0, it.read(buffer))  
        }  
        zipFile.closeEntry()
    }
    }  
    zipFile.close()  
}

def unzipper(){
    String zipFileName = "utils.zip"  
    def outputDir = "zip"
    byte[] buffer = new byte[1024]
    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileName))
    ZipEntry zipEntry = zis.getNextEntry()
    while (zipEntry != null) {
        File newFile = new File(outputDir+ File.separator, zipEntry.name)
        if (zipEntry.isDirectory()) {
            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                throw new IOException("Failed to create directory " + newFile)
            }
        } else {
            // fix for Windows-created archives
            File parent = newFile.parentFile
            if (!parent.isDirectory() && !parent.mkdirs()) {
                throw new IOException("Failed to create directory " + parent)
            }
            // write file content
            FileOutputStream fos = new FileOutputStream(newFile)
            int len = 0
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len)
            }
            fos.close()
        }
    zipEntry = zis.getNextEntry()
    }
    zis.closeEntry()
    zis.close()
}