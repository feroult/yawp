import java.nio.file.Path
import java.nio.file.Paths

Boolean kotlin = System.properties['lang'] != null && System.properties['lang'].equals("kotlin")
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
    deleteFile('pom-java.xml')
    renameFile('pom-kotlin.xml', 'pom.xml')
}

def javaSetup = {
    println "Java archetype selected"
    deleteDir('src/main/kotlin')
    deleteDir('src/test/kotlin')
    deleteFile('pom-kotlin.xml')
}

if (kotlin) {
    kotlinSetup()
} else {
    javaSetup()
}
