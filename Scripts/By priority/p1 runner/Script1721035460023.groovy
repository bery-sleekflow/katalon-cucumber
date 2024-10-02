import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW

CucumberKW.runFeatureFileWithTags('Include/features/web/authentication/authentication.feature', '@p1')

CucumberKW.runFeatureFileWithTags('Include/features/web/contact/contact_create.feature', '@p1')

CucumberKW.runFeatureFileWithTags('Include/features/web/commerce/stripe.feature', '@p1')

CucumberKW.runFeatureFileWithTags('Include/features/web/inbox/inbox.feature', '@p1')