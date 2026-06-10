package oaktree

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable as GlobalVariable

class OaktreeLogin {

	@Keyword
	def login() {
		// Buka browser jika belum terbuka
		try {
			if (DriverFactory.getWebDriver() == null) {
				WebUI.openBrowser('')
				WebUI.maximizeWindow()
			}
		} catch (Exception e) {
			WebUI.openBrowser('')
			WebUI.maximizeWindow()
		}

		WebUI.navigateToUrl('https://dummy.appoaktree.id/admin')
		WebUI.setText(findTestObject('Page_Login Panel/input_email_user'), GlobalVariable.OaktreeEmail)
		WebUI.setEncryptedText(findTestObject('Page_Login Panel/input_password'), GlobalVariable.OaktreePassword)
		WebUI.click(findTestObject('Page_Login Panel/a_Sign in'))

		// Handle revoke session jika akun aktif di tempat lain
		if (WebUI.waitForElementVisible(findTestObject('Page_Login Panel/btn_Revoke Session'), 4, FailureHandling.OPTIONAL)) {
			WebUI.comment('🚨 [REVOKE DETECTED] Menendang sesi aktif lain...')
			WebUI.click(findTestObject('Page_Login Panel/btn_Revoke Session'))
			WebUI.click(findTestObject('Page_Login Panel/a_Sign in'))
		}
	}
}