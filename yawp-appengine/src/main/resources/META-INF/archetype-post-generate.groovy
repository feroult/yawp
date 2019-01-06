import java.nio.file.Path
import java.nio.file.Paths

Boolean kotlin = System.properties['kotlin'].toBoolean()
Path projectPath = Paths.get(request.outputDirectory, request.artifactId)

def deleteDir = { String path -> projectPath.resolve(path).toFile().deleteDir() }
def deleteFile = { String path -> projectPath.resolve(path).toFile().delete() }
def renameFile = { String from, String to ->
    projectPath.resolve(from).toFile().renameTo(projectPath.resolve(to).toFile())
}

def kotlinSetup = {
    println "Kotlin archetype selected"
    deleteDir('src/main/java')
    deleteDir('src/test/java')
    deleteFile('pom.xml')
    renameFile('pom-kotlin.xml', 'pom.xml')
}

if (kotlin) {
    kotlinSetup()
}
