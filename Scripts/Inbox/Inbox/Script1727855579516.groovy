import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW

CucumberKW.runFeatureFileWithTags('Include/features/web/inbox/inbox.feature', 'not @multiple_user_interaction')