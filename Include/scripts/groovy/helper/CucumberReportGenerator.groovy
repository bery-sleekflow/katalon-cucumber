package helper
;

@Grab(group='net.masterthought', module='cucumber-reporting', version='5.8.1')
import net.masterthought.cucumber.Configuration
import net.masterthought.cucumber.ReportBuilder

try {
	// Verify the JSON file exists
	File jsonFile = new File("Reports/Cucumber/cucumber.json")
	if (!jsonFile.exists()) {
		println "cucumber.json file does not exist at ${jsonFile.absolutePath}"
		return
	}

	// Report output directory
	File reportOutputDirectory = new File("Reports/Cucumber")
	if (!reportOutputDirectory.exists()) {
		reportOutputDirectory.mkdirs()
	}

	// List of JSON files
	List<String> jsonFiles = new ArrayList<>()
	jsonFiles.add(jsonFile.absolutePath)

	// Configuration settings
	String buildNumber = "1"
	String projectName = "E2E-Web"

	Configuration configuration = new Configuration(reportOutputDirectory, projectName)
	configuration.setBuildNumber(buildNumber)

	// Generate the report
	ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration)
	reportBuilder.generateReports()

	println "Cucumber report generated successfully at ${reportOutputDirectory.absolutePath}"
} catch (Exception e) {
	println "Error generating Cucumber report: ${e.message}"
	e.printStackTrace()
}