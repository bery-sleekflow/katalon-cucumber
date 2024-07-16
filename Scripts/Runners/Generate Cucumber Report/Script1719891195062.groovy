import groovy.lang.GroovyShell
import java.nio.file.Files
import java.nio.file.Paths

// Define the path to the Groovy script
String scriptPath = "Include/scripts/groovy/helper/CucumberReportGenerator.groovy"

// Check if the script file exists
if (!Files.exists(Paths.get(scriptPath))) {
    throw new FileNotFoundException("Groovy script not found: " + scriptPath)
}

// Read the Groovy script
String script = new String(Files.readAllBytes(Paths.get(scriptPath)))

// Create a new GroovyShell and evaluate the script
GroovyShell shell = new GroovyShell()
shell.evaluate(script)