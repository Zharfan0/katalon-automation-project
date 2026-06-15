import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.testobject.TestObjectProperty as TestObjectProperty
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

import oaktree.OaktreeLogin
import oaktree.Select2Input
import oaktree.SweetAlert

// Helper tutupSweetAlert
void closeSweetAlertIfPresent() {
	if (WebUI.waitForElementVisible(xpath("//div[contains(@class,'sweet-alert')]"), 2, FailureHandling.OPTIONAL)) {
		WebUI.comment("🍬 Menutup SweetAlert yang tersisa...")
		WebUI.executeJavaScript("document.querySelector('.sweet-alert .confirm')?.click()", null)
		WebUI.delay(1)
	}
	// Backup: tutup overlay manual jika masih ada
	if (WebUI.waitForElementVisible(xpath("//div[contains(@class,'sweet-overlay')]"), 2, FailureHandling.OPTIONAL)) {
		WebUI.executeJavaScript("document.querySelector('.sweet-overlay')?.remove()", null)
	}
}
A
// =========================================================================
// HELPER: Buat TestObject dinamis dari xpath — tanpa butuh OR entry
// =========================================================================
TestObject xpath(String xpathValue) {
    TestObject obj = new TestObject('dynamic')
    obj.addProperty('xpath', ConditionType.EQUALS, xpathValue)
    return obj
}

// =========================================================================
// HELPER: Cek apakah popup Token & Session Expired sedang tampil
// =========================================================================
boolean isTokenExpiredPopupVisible() {
    return WebUI.waitForElementVisible(
        xpath("//div[contains(@class,'sweet-alert')]//h2[contains(text(),'Token')]"),
        3, FailureHandling.OPTIONAL
    )
}

// =========================================================================
// HELPER: Handle TC0 — Get Token + Get Session
// Dipanggil di awal, dan sebagai recovery jika token expire di tengah jalan
// =========================================================================
void runTC0() {
    WebUI.comment("🔄 [TC0] Menjalankan Get Token + Get Session...")

    WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/icon-profile'))
    WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Get Token'))

    WebUI.sendKeys(findTestObject('Page_Accurate Online Autentikasi Pengguna/input_Email atau No Handphone_email'), GlobalVariable.AccurateEmail)
    WebUI.setEncryptedText(findTestObject('Page_Accurate Online Autentikasi Pengguna/input_Password_password'), GlobalVariable.AccuratePassword)
    WebUI.delay(2)
    WebUI.click(findTestObject('Page_Accurate Online Autentikasi Pengguna/button_Masuk'))

    if (WebUI.waitForElementClickable(findTestObject('Page_Accurate Online Autentikasi Pengguna/button_Beri Akses'), 10, FailureHandling.OPTIONAL)) {
        WebUI.click(findTestObject('Page_Accurate Online Autentikasi Pengguna/button_Beri Akses'))
    }

    TestObject dropdownDb = findTestObject('Page_Profile - PT Rimba Yogyakarta/dropdown-database-session')
    WebUI.waitForElementPresent(dropdownDb, 15)
    WebUI.selectOptionByLabel(dropdownDb, 'Partnership CPS X Oaktree - Development', false)
    WebUI.delay(2)

    WebUI.waitForElementClickable(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Get Session'), 10)
    WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Get Session'))
    WebUI.delay(2)

    WebUI.waitForElementClickable(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Yes-confirm get session'), 10)
    WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_Yes-confirm get session'))
    WebUI.delay(3)

    if (WebUI.waitForElementClickable(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_OK-success get session'), 10, FailureHandling.OPTIONAL)) {
        WebUI.click(findTestObject('Page_Profile - PT Rimba Yogyakarta/button_OK-success get session'))
    }

    WebUI.comment("✅ [TC0] Selesai — kembali ke alur TC2")
}

// =========================================================================
// SHORTHAND Object Repository
// =========================================================================
def modal = { String name -> findTestObject("Page_Master Jobfile - PT Rimba Yogyakarta/Modal - Add Jobfile/${name}") }
def jf    = { String name -> findTestObject("Page_Master Jobfile - PT Rimba Yogyakarta/Jobfile - Inside/Page_Jobfile - PT Dummy Rimba Yogyakarta/${name}") }

// Skip iterasi jika TC_Enabled = false
if (!['true','1','yes'].contains(TC_Enabled?.trim()?.toLowerCase())) {
	WebUI.comment("⏭️  [SKIP] ${TC_Description}")
	return
}


// =========================================================================
// BACA VARIABEL DARI DATA BINDING KATALON
// Katalon inject nilai per kolom Excel secara otomatis ke variabel ini
// Semua variabel harus didaftarkan di tab Variables pada Test Case
// =========================================================================
// String TC_Enabled    — dikontrol Katalon (baris dengan false dilewati otomatis)
// String TC_Description
// String Vendor
// String PPN
// String Item
// String TipeFaktur
// String DocumentCode
// String TaxType
// String Qty
// String Price
// String Remarks
// String ItemAlias
// String UseManualNum

// =========================================================================
// GENERATE nilai unik per iterasi
// =========================================================================
String ts         = new Date().format("yyyyMMdd-HHmmss")
String invoiceNo  = "INV-${ts}"
boolean useManual = ['true','1','yes'].contains(UseManualNum?.trim()?.toLowerCase())
String manualNum  = useManual ? "BUY-${ts}" : ""

// Tentukan apakah DocumentCode dan TaxType perlu diisi
boolean isPpnActive      = PPN?.trim()?.toLowerCase() != 'non ppn'
boolean needDocumentCode = isPpnActive && DocumentCode?.trim()
boolean needTaxType      = needDocumentCode && DocumentCode?.trim() != 'Digunggung' && TaxType?.trim()

WebUI.comment("▶️  [START] ${TC_Description}")
WebUI.comment("📋 [DATA] PPN=${PPN} | DocCode=${DocumentCode ?: '-'} | TaxType=${TaxType ?: '-'} | TipeFaktur=${TipeFaktur} | Qty=${Qty} | Price=${Price} | UseManualNum=${useManual}")
closeSweetAlertIfPresent()


// =========================================================================
// NAVIGASI KE JOBFILE
// =========================================================================
WebUI.comment("🎉 [SUCCESS]")   // jika memang ingin menandai, bisa dihapus
WebUI.waitForElementClickable(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-operational'), 15)
WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-operational'))
WebUI.waitForElementVisible(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'), 10)
WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'))
WebUI.click(findTestObject('Page_Master Jobfile - PT Rimba Yogyakarta/button_Create New Jobfile'))

// =========================================================================
// DETEKSI TOKEN EXPIRE — setelah klik Create New Jobfile
// Popup bisa muncul di sini jika token sudah habis
// =========================================================================
if (isTokenExpiredPopupVisible()) {
    WebUI.comment("⚠️  [TOKEN EXPIRED] Popup terdeteksi — menjalankan TC0 recovery...")
    WebUI.executeJavaScript("document.querySelector('.sweet-alert .confirm').click()", null)
    WebUI.delay(1)
    runTC0()
    // Navigasi ulang ke Jobfile setelah recovery
    WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-operational'))
    WebUI.waitForElementVisible(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'), 10)
    WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'))
    WebUI.click(findTestObject('Page_Master Jobfile - PT Rimba Yogyakarta/button_Create New Jobfile'))
}

// =========================================================================
// ISI MODAL CREATE JOBFILE
// =========================================================================
WebUI.click(modal('branch-dropdown'))
WebUI.setText(modal('branch-dropdown-searchfield'), 'Head Office')
WebUI.sendKeys(modal('branch-dropdown-searchfield'), Keys.chord(Keys.ENTER))

WebUI.click(modal('customer-dropdown'))
WebUI.setText(modal('customer-dropdown-searchfield'), 'Act Cargo')
WebUI.sendKeys(modal('customer-dropdown-searchfield'), Keys.chord(Keys.ENTER))

WebUI.click(modal('sales-dropdown'))
WebUI.setText(modal('sales-dropdown-searchfield'), 'zharfan')
WebUI.sendKeys(modal('sales-dropdown-searchfield'), Keys.chord(Keys.ENTER))

WebUI.setText(modal('input_etd'), new Date().format('yyyy-MM-dd'))
WebUI.sendKeys(modal('input_etd'), Keys.chord(Keys.ENTER))

WebUI.click(modal('label_Nomination'))
WebUI.click(modal('label_Export'))
WebUI.click(modal('label_Ocean Freight'))
WebUI.click(modal('label_FCL'))

WebUI.click(modal('button_Save'))
WebUI.waitForPageLoad(10)

WebUI.click(jf('a_Next GI'))
WebUI.click(jf('a_Submit GI'))

// =========================================================================
// BUKA TAB BUYING
// =========================================================================
WebUI.waitForElementVisible(jf('a_Tab Buying'), 15)
WebUI.executeJavaScript("document.querySelector('a[href=\"#buying\"]').click()", null)

if (!WebUI.waitForElementNotVisible(jf('div_loader_module'), 15, FailureHandling.OPTIONAL)) {
    WebUI.comment('⚠️ Loader stuck, melakukan reload...')
    WebUI.refresh()
    WebUI.waitForPageLoad(15)
    WebUI.waitForElementVisible(jf('a_Tab Buying'), 15)
    WebUI.executeJavaScript("document.querySelector('a[href=\"#buying\"]').click()", null)
    WebUI.waitForElementNotVisible(jf('div_loader_module'), 15)
}

// =========================================================================
// BUKA FORM TRANSAKSI BUYING
// =========================================================================
WebUI.click(jf('button_New Transaction Buying'))

// Toggle manual number SETELAH modal terbuka
if (useManual) {
    WebUI.click(jf('toggle-manual-num-buy'))
    WebUI.comment("🔢 [MANUAL NUM] Nomor manual: ${manualNum}")
}

// Tunggu form siap
WebUI.waitForElementVisible(jf('input__invoice_no'), 15)
WebUI.delay(3)

// =========================================================================
// DETEKSI TOKEN EXPIRE — bisa muncul saat membuka form Buying
// =========================================================================
if (isTokenExpiredPopupVisible()) {
    WebUI.comment("⚠️  [TOKEN EXPIRED] Popup terdeteksi di form Buying — menjalankan TC0 recovery...")
    WebUI.executeJavaScript("document.querySelector('.sweet-alert .confirm').click()", null)
    WebUI.delay(1)
    runTC0()
    // Navigasi ulang ke tab Buying
    WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-operational'))
    WebUI.waitForElementVisible(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'), 10)
    WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'))
    WebUI.waitForElementVisible(jf('a_Tab Buying'), 15)
    WebUI.executeJavaScript("document.querySelector('a[href=\"#buying\"]').click()", null)
    WebUI.waitForElementNotVisible(jf('div_loader_module'), 15)
    WebUI.click(jf('button_New Transaction Buying'))
    if (useManual) {
        WebUI.click(jf('toggle-manual-num-buy'))
    }
    WebUI.waitForElementVisible(jf('input__invoice_no'), 15)
    WebUI.delay(3)
}

// =========================================================================
// ISI FORM BUYING
// =========================================================================
new Select2Input().input('Vendor', Vendor)
WebUI.setText(jf('input__invoice_no'), invoiceNo)
new Select2Input().input('PPN', PPN)

// DocumentCode & TaxType — kondisional
if (needDocumentCode) {
    WebUI.comment("📄 [DOC CODE] Mengisi Document Code: ${DocumentCode}")
    new Select2Input().input('Document Code', DocumentCode)

    if (needTaxType) {
        WebUI.comment("🏷️  [TAX TYPE] Mengisi Tax Type: ${TaxType}")
        new Select2Input().input('Tax Type', TaxType)
    } else {
        WebUI.comment("ℹ️  [TAX TYPE] Dilewati — DocumentCode=${DocumentCode}")
    }
} else {
    WebUI.comment("ℹ️  [DOC CODE] Dilewati — PPN=${PPN}")
}

new Select2Input().input('Item', Item)
new Select2Input().input('Tipe Faktur', TipeFaktur)

WebUI.setText(jf('input__qty_cost'),                  Qty)
WebUI.setText(jf('input__pricecost'),                 Price)
WebUI.setText(jf('input_Remarks_remarks_cost'),       Remarks)
WebUI.setText(jf('input_Item Alias_item_alias_cost'), ItemAlias)

// =========================================================================
// ADD ITEM → KONFIRMASI → SAVE → KONFIRMASI
// =========================================================================
WebUI.click(jf('button_Add Item'))
new SweetAlert().confirm()

WebUI.executeJavaScript("document.querySelector('#modal-buying-content #SubmitCost').click()", null)
new SweetAlert().confirm()

// Tutup popup "Success with some Error" jika muncul
// Popup ini muncul saat data tersimpan di Oaktree tapi ada error sync ke Accurate
if (WebUI.waitForElementVisible(
        xpath("//div[contains(@class,'sweet-alert')]//p[contains(text(),'some error')]"),
        5, FailureHandling.OPTIONAL)) {
    WebUI.comment("⚠️  [ACCURATE SYNC] Popup 'Success with some Error' terdeteksi — menutup dan lanjut")
    WebUI.executeJavaScript("document.querySelector('.sweet-alert .confirm').click()", null)
    WebUI.delay(1)
}

WebUI.comment("🎉 [SUCCESS] ${TC_Description} | InvoiceNo=${invoiceNo}${useManual ? ' | ManualNum=' + manualNum : ''}")
closeSweetAlertIfPresent()
WebUI.refresh()  // opsional, bersihkan state halaman
WebUI.delay(2)