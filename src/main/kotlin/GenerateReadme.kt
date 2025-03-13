
import java.io.File

fun main() {
    // Define the directory of the GitHub project
    val projectDir = File("/Users/arpanpathak/Projects/data_science/CrackGoogle/src/main/kotlin")

    // Ensure the directory exists
    if (!projectDir.exists() || !projectDir.isDirectory) {
        println("Invalid project directory")
        return
    }

    // Create or overwrite the README.md file
    val readmeFile = File(projectDir, "README.md")

    // Start writing the README content
    val content = StringBuilder()
    content.append("# Project Index\n\n")
    content.append("## Files and Directories\n\n")

    // Recursively list all files and directories
    fun listFiles(dir: File, indent: String = "") {
        val files = dir.listFiles()?.sorted() ?: return
        for (file in files) {
            if (file.isDirectory) {
                content.append("$indent- **${file.name}/**\n")
                listFiles(file, "$indent  ")
            } else {
                content.append("$indent- ${file.name}\n")
            }
        }
    }

    listFiles(projectDir)

    // Write the content to the README.md file
    readmeFile.writeText(content.toString())

    println("README.md generated successfully!")
}
