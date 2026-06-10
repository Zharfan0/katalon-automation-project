package oaktree

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.annotation.Keyword
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

class Select2Input {

	@Keyword
	def input(String labelName, String textToSearch, String fallbackXpath = "", String strategy = "sibling") {
		def driver = DriverFactory.getWebDriver()

		String xpath
		if (!fallbackXpath.isEmpty()) {
			// Xpath custom untuk label ambigu atau struktur khusus
			xpath = fallbackXpath
		} else if (strategy == "class") {
			// Form Customer: cari span select2 berdasarkan select class name
			// labelName diisi dengan class select2 elemen (bukan teks label)
			xpath = "//select[contains(@class,'${labelName}')]/following-sibling::span//span[contains(@class,'select2-selection')]"
		} else if (strategy == "parent") {
			// Fallback: naik ke col-md-3 lalu cari span select2
			xpath = "//label[contains(text(), '${labelName}')]/ancestor::div[contains(@class,'col-md-3')]//span[contains(@class,'select2-selection')]"
		} else {
			// Form Buying: label dan span select2 adalah siblings
			xpath = "//label[contains(text(), '${labelName}')]/following-sibling::span//span[contains(@class,'select2-selection')]"
		}

		driver.findElement(By.xpath(xpath)).click()
		WebUI.delay(2)

		if (driver.findElements(By.cssSelector("input.select2-search__field")).size() == 0) {
			driver.findElement(By.xpath(xpath)).click()
			WebUI.delay(2)
		}

		driver.findElement(By.cssSelector("input.select2-search__field")).sendKeys(textToSearch)
		WebUI.delay(2)
		driver.findElement(By.cssSelector("input.select2-search__field")).sendKeys(Keys.ENTER)
		WebUI.delay(1)
	}
}