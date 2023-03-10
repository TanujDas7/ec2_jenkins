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
                        scripts{echo "zip pem and html"
                        sh "mkdir utils"
                        sh "mv home.html utils"
                        sh "mv mumbai.pem utils"}
                        zipper()
                    }
                }
            stage("ec2"){
                steps{
                    unzipper()
                    scripts{
                        sh "ls -al"
                    }
                }
            }
    }
}

def zipper(){
    String zipFileName = "utils.zip"
    String inputDir = "utils"

    ZipOutputStream output = new ZipOutputStream(new FileOutputStream(zipFileName))
    new File(inputDir).eachFile() { file ->
        if (!file.isFile()) {
            return
        }

        output.putNextEntry(new ZipEntry(file.name.toString())) // Create the name of the entry in the ZIP

        InputStream input = new FileInputStream(file);

        // Stream the document data to the ZIP
        Files.copy(input, output);
        output.closeEntry(); // End of current document in ZIP
        input.close()
    }
    output.close();
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