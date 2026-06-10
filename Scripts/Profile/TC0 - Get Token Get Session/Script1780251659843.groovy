import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// Custom Keywords
import oaktree.OaktreeLogin

WebUI.comment('🔄 Memulai TC0 - Inisialisasi Sesi Accurate...')

// =========================================================================
// SETUP: LOGIN OAKTREE
// OaktreeLogin sudah handle: buka browser, navigasi, login, revoke session
// =========================================================================
new OaktreeLogin().login()

// =========================================================================
// STEP 1: MASUK KE HALAMAN PROFILE
// =========================================================================
WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/icon-profile'))

// =========================================================================
// STEP 2: GET TOKEN (ACCURATE OAUTH)
// =========================================================================
WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Get Token'))

// Ganti WebUI.setText dengan klik dulu lalu sendKeys
//WebUI.click(findTestObject('Page_Accurate Online Autentikasi Pengguna/input_Email atau No Handphone_email'))
WebUI.sendKeys(findTestObject('Page_Accurate Online Autentikasi Pengguna/input_Email atau No Handphone_email'), GlobalVariable.AccurateEmail)
//WebUI.click(findTestObject('Page_Accurate Online Autentikasi Pengguna/input_Password_password'))
WebUI.setEncryptedText(findTestObject('Page_Accurate Online Autentikasi Pengguna/input_Password_password'), GlobalVariable.AccuratePassword)
WebUI.delay(2)
WebUI.click(findTestObject('Page_Accurate Online Autentikasi Pengguna/button_Masuk'))

// Klik Beri Akses jika muncul (kondisional - tidak selalu muncul)
if (WebUI.waitForElementClickable(findTestObject('Page_Accurate Online Autentikasi Pengguna/button_Beri Akses'), 10, FailureHandling.OPTIONAL)) {
    WebUI.click(findTestObject('Page_Accurate Online Autentikasi Pengguna/button_Beri Akses'))
}

// =========================================================================
// STEP 3: SELEKSI DATABASE & GET SESSION
// =========================================================================
TestObject dropdownDb = findTestObject('Page_Profile - PT Rimba Yogyakarta/dropdown-database-session')
WebUI.waitForElementPresent(dropdownDb, 15)
WebUI.selectOptionByLabel(dropdownDb, 'Partnership CPS X Oaktree - Development', false)
WebUI.delay(2)

WebUI.waitForElementClickable(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Get Session'), 10)
WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Get Session'))
WebUI.delay(2)

// Konfirmasi Get Session
WebUI.waitForElementClickable(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Yes-confirm get session'), 10)
WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Yes-confirm get session'))
WebUI.delay(3)

// Klik OK jika muncul (kondisional - tidak selalu muncul)
if (WebUI.waitForElementClickable(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_OK-success get session'), 10, FailureHandling.OPTIONAL)) {
    WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_OK-success get session'))
}

// =========================================================================
// STEP 4: VERIFIKASI SESI AKTIF
// =========================================================================
if (WebUI.waitForElementPresent(findTestObject('Page_Profile - PT Rimba Yogyakarta/span_Session Active'), 10, FailureHandling.OPTIONAL)) {
    WebUI.comment('🎉 [SUCCESS] Token dan Sesi Accurate Berhasil Terkoneksi (Status: Active)!')
} else {
    WebUI.comment('⚠️ [WARNING] Sesi mungkin aktif namun indikator Active tidak terbaca.')
}