import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

import oaktree.OaktreeLogin
import oaktree.Select2Input
import oaktree.SweetAlert

// =========================================================================
// HELPER: Generate unique suffix berbasis timestamp
// =========================================================================
String timestamp() {
    return new Date().format("yyyyMMdd-HHmmss")
}

// =========================================================================
// HELPER: Baca TC_Enabled secara toleran — true / TRUE / 1 / yes
// =========================================================================
boolean isEnabled(String val) {
    return ['true', '1', 'yes'].contains(val?.trim()?.toLowerCase())
}

// =========================================================================
// SHORTHAND Object Repository
// =========================================================================
def modal = { String name -> findTestObject("Page_Master Jobfile - PT Rimba Yogyakarta/Modal - Add Jobfile/${name}") }
def jf    = { String name -> findTestObject("Page_Master Jobfile - PT Rimba Yogyakarta/Jobfile - Inside/Page_Jobfile - PT Dummy Rimba Yogyakarta/${name}") }

// =========================================================================
// LOAD TEST DATA
// =========================================================================
def td        = TestDataFactory.findTestData('TC2_Buying')
int totalRows = td.getRowNumbers()

WebUI.comment("📂 [DDT] Total baris ditemukan: ${totalRows}")

// =========================================================================
// DDT LOOP
// =========================================================================
for (int i = 1; i <= totalRows; i++) {

    // ── 1. Cek TC_Enabled ─────────────────────────────────────────────────
    String enabled = td.getValue('TC_Enabled', i)
    if (!isEnabled(enabled)) {
        WebUI.comment("⏭️  [SKIP] Row ${i}: ${td.getValue('TC_Description', i)}")
        continue
    }

    // ── 2. Baca semua kolom DDT ───────────────────────────────────────────
    String desc         = td.getValue('TC_Description', i)
    String vendor       = td.getValue('Vendor', i)
    String ppn          = td.getValue('PPN', i)
    String item         = td.getValue('Item', i)
    String tipeFaktur   = td.getValue('TipeFaktur', i)
    String documentCode = td.getValue('DocumentCode', i)?.trim() ?: ''
    String taxType      = td.getValue('TaxType', i)?.trim() ?: ''
    String qty          = td.getValue('Qty', i)
    String price        = td.getValue('Price', i)
    String remarks      = td.getValue('Remarks', i)
    String itemAlias    = td.getValue('ItemAlias', i)
    boolean useManual   = isEnabled(td.getValue('UseManualNum', i))

    // InvoiceNo dan ManualNum di-generate script — unik setiap run
    String invoiceNo = "INV-${timestamp()}"
    String manualNum = useManual ? "BUY-${timestamp()}" : ""

    // Tentukan apakah DocumentCode dan TaxType perlu diisi
    boolean isPpnActive      = ppn?.trim()?.toLowerCase() != 'non ppn'
    boolean needDocumentCode = isPpnActive && !documentCode.isEmpty()
    boolean needTaxType      = needDocumentCode && documentCode != 'Digunggung' && !taxType.isEmpty()

    WebUI.comment("▶️  [START] Row ${i}: ${desc}")
    WebUI.comment("📋 [DATA] PPN=${ppn} | DocCode=${documentCode ?: '-'} | TaxType=${taxType ?: '-'} | TipeFaktur=${tipeFaktur} | Qty=${qty} | Price=${price} | UseManualNum=${useManual}")

    // ── 3. TC0 — Login + Get Token + Get Session ──────────────────────────
    WebUI.comment("🔄 [TC0] Memulai sesi untuk row ${i}: ${desc}")

    new OaktreeLogin().login()

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

    if (WebUI.waitForElementPresent(findTestObject('Page_Profile - PT Rimba Yogyakarta/span_Session Active'), 10, FailureHandling.OPTIONAL)) {
        WebUI.comment("✅ [TC0] Sesi Accurate aktif — lanjut ke TC2")
    } else {
        WebUI.comment("⚠️  [TC0] Indikator sesi tidak terbaca, lanjut tetapi perhatikan hasil transaksi")
    }

    // ── 4. Navigasi ke Jobfile ────────────────────────────────────────────
    WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-operational'))
    WebUI.waitForElementVisible(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'), 10)
    WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'))
    WebUI.click(findTestObject('Page_Master Jobfile - PT Rimba Yogyakarta/button_Create New Jobfile'))

    // ── 5. Isi modal Create Jobfile ───────────────────────────────────────
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

    // ── 6. Buka tab Buying ────────────────────────────────────────────────
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


    // ── 7. Buka form transaksi Buying ─────────────────────────────────────
    WebUI.click(jf('button_New Transaction Buying'))

    // Toggle manual number SETELAH New Transaction diklik — modal harus terbuka dulu
    if (useManual) {
        WebUI.click(jf('toggle-manual-num-buy'))
        WebUI.comment("🔢 [MANUAL NUM] Nomor manual: ${manualNum}")
    }

    // ── 8. Tunggu form siap ───────────────────────────────────────────────
    WebUI.waitForElementVisible(jf('input__invoice_no'), 15)

    // Tunggu Select2 selesai render — diperlukan saat fresh login per iterasi
    WebUI.delay(3)

    // ── 9. Isi field utama form ───────────────────────────────────────────
    new Select2Input().input('Vendor', vendor)
    WebUI.setText(jf('input__invoice_no'), invoiceNo)
    new Select2Input().input('PPN', ppn)

    // ── 10. DocumentCode & TaxType — kondisional berdasarkan nilai PPN ────
    if (needDocumentCode) {
        WebUI.comment("📄 [DOC CODE] Mengisi Document Code: ${documentCode}")
        new Select2Input().input('Document Code', documentCode)

        if (needTaxType) {
            WebUI.comment("🏷️  [TAX TYPE] Mengisi Tax Type: ${taxType}")
            new Select2Input().input('Tax Type', taxType)
        } else {
            WebUI.comment("ℹ️  [TAX TYPE] Dilewati — DocumentCode=${documentCode} tidak memerlukan Tax Type")
        }
    } else {
        WebUI.comment("ℹ️  [DOC CODE] Dilewati — PPN=${ppn} tidak memerlukan Document Code")
    }

    // ── 11. Lanjut isi sisa form ──────────────────────────────────────────
    new Select2Input().input('Item', item)
    new Select2Input().input('Tipe Faktur', tipeFaktur)

    WebUI.setText(jf('input__qty_cost'),                  qty)
    WebUI.setText(jf('input__pricecost'),                 price)
    WebUI.setText(jf('input_Remarks_remarks_cost'),       remarks)
    WebUI.setText(jf('input_Item Alias_item_alias_cost'), itemAlias)

    // ── 12. Add Item → konfirmasi → Save → konfirmasi ─────────────────────
    WebUI.click(jf('button_Add Item'))
    new SweetAlert().confirm()

    WebUI.executeJavaScript("document.querySelector('#modal-buying-content #SubmitCost').click()", null)
    new SweetAlert().confirm()

    WebUI.comment("🎉 [SUCCESS] Row ${i} selesai: ${desc} | InvoiceNo=${invoiceNo}${useManual ? ' | ManualNum=' + manualNum : ''}")

    // ── 13. Tutup browser — TC0 butuh fresh session di iterasi berikutnya ─
    WebUI.closeBrowser()
}

WebUI.comment("✅ [DONE] Semua iterasi DDT TC2 selesai dieksekusi.")