import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.Keys as Keys
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import java.time.LocalDateTime as LocalDateTime
import oaktree.OaktreeLogin as OaktreeLogin
import oaktree.Select2Input as Select2Input
import oaktree.SweetAlert as SweetAlert
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable

String tanggalHariIni = LocalDate.now().format(DateTimeFormatter.ofPattern('yyyy-MM-dd'))
String waktuSekarang = LocalDateTime.now().format(DateTimeFormatter.ofPattern('yyyy-MM-dd_HH-mm-ss'))

def modal = { String name ->
    findTestObject("Page_Master Jobfile - PT Rimba Yogyakarta/Modal - Add Jobfile/$name")
}

def jf = { String name ->
    findTestObject("Page_Master Jobfile - PT Rimba Yogyakarta/Jobfile - Inside/Page_Jobfile - PT Dummy Rimba Yogyakarta/$name")
}

// =========================================================================
// SETUP: LOGIN || dicomment jika ingin dijalankan di tes Suite
// =========================================================================
//new OaktreeLogin().login()

// =========================================================================
// SCENARIO 1: CREATE NEW JOBFILE
// =========================================================================
WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-operational'))

WebUI.waitForElementVisible(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'), 10)

WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'))

WebUI.click(findTestObject('Page_Master Jobfile - PT Rimba Yogyakarta/button_Create New Jobfile'))

WebUI.click(modal('branch-dropdown'))

WebUI.setText(modal('branch-dropdown-searchfield'), 'Head Office')

WebUI.sendKeys(modal('branch-dropdown-searchfield'), Keys.chord(Keys.ENTER))

WebUI.click(modal('customer-dropdown'))

WebUI.setText(modal('customer-dropdown-searchfield'), 'Act Cargo')

WebUI.sendKeys(modal('customer-dropdown-searchfield'), Keys.chord(Keys.ENTER))

WebUI.click(modal('sales-dropdown'))

WebUI.setText(modal('sales-dropdown-searchfield'), 'zharfan')

WebUI.sendKeys(modal('sales-dropdown-searchfield'), Keys.chord(Keys.ENTER))

WebUI.setText(modal('input_etd'), tanggalHariIni)

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
// SCENARIO 2: BUYING TRANSACTION
// =========================================================================
// Klik tab Buying via JS (bypass topbar fixed yang menghalangi klik biasa)
WebUI.waitForElementVisible(jf('a_Tab Buying'), 15)

WebUI.executeJavaScript('document.querySelector(\'a[href="#buying"]\').click()', null)

// Tunggu loader AJAX hilang, reload jika stuck
if (!(WebUI.waitForElementNotVisible(jf('div_loader_module'), 15, FailureHandling.OPTIONAL))) {
    WebUI.comment('⚠️ Loader stuck, melakukan reload halaman...')

    WebUI.refresh()

    WebUI.waitForPageLoad(15)

    WebUI.waitForElementVisible(jf('a_Tab Buying'), 15)

    WebUI.executeJavaScript('document.querySelector(\'a[href="#buying"]\').click()', null)

    WebUI.waitForElementNotVisible(jf('div_loader_module'), 15)
}

// Buka form transaksi Buying
WebUI.click(jf('button_New Transaction Buying'))

//// Input nomor manual (dikomen jika pakai autonumber)
WebUI.click(jf('toggle-manual-num-buy'))

WebUI.setText(jf('input_Buying_number_text_cost'), 'Buying ' + waktuSekarang)

// Tunggu modal terbuka sebelum isi form
WebUI.waitForElementVisible(jf('input__invoice_no'), 15)

// Tunggu Select2 selesai render setelah modal muncul
// (diperlukan terutama saat fresh login — DOM lebih lambat dari sesi hangat)
WebUI.delay(3)

// Isi form Buying
new Select2Input().input('Vendor', 'Act Cargo')

WebUI.setText(jf('input__invoice_no'), 'Inv Automation' + waktuSekarang)

new Select2Input().input('PPN', 'Non PPN')

new Select2Input().input('Item', 'Hitam')

new Select2Input().input('Tipe Faktur', 'Real')

WebUI.setText(jf('input__qty_cost'), '5')

WebUI.setText(jf('input__pricecost'), '15000')

WebUI.setText(jf('input_Remarks_remarks_cost'), 'Tes Automation Katalon')

WebUI.setText(jf('input_Item Alias_item_alias_cost'), 'Item Automation Katalon')

// Add Item lalu konfirmasi
WebUI.click(jf('button_Add Item'))

new SweetAlert().confirm()

// Save transaksi lalu konfirmasi
WebUI.executeJavaScript('document.querySelector(\'#modal-buying-content #SubmitCost\').click()', null)

new SweetAlert().confirm()

WebUI.comment('🎉 [SUCCESS] Skenario E2E Jobfile hingga Buying Berhasil Dieksekusi!')

WebUI.acceptAlert(FailureHandling.OPTIONAL)

