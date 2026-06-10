package oaktree

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.annotation.Keyword

class SweetAlert {

	// Klik tombol Yes/Confirm di SweetAlert
	// Dipakai setelah Add Item dan Save Transaksi di form Buying
	@Keyword
	def confirm() {
		WebUI.delay(3) // Tunggu animasi SweetAlert muncul
		WebUI.executeJavaScript("document.querySelector('.sa-confirm-button-container .confirm').click()", null)
		WebUI.delay(2)
	}
}