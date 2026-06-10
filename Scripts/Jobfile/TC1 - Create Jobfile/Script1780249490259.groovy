import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.Keys as Keys
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter

// Custom Keywords
import oaktree.OaktreeLogin

String tanggalHariIni = LocalDate.now().format(DateTimeFormatter.ofPattern('yyyy-MM-dd'))

// Shorthand path Modal Add Jobfile
def modal = { String name -> findTestObject("Page_Master Jobfile - PT Rimba Yogyakarta/Modal - Add Jobfile/${name}") }

// =========================================================================
// SETUP: LOGIN OAKTREE
// =========================================================================
new OaktreeLogin().login()

// =========================================================================
// SCENARIO: CREATE NEW JOBFILE
// =========================================================================
WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-operational'))
WebUI.waitForElementVisible(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'), 10)
WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/Icon_Jobfile'))
WebUI.click(findTestObject('Page_Master Jobfile - PT Rimba Yogyakarta/button_Create New Jobfile'))

// Isi Branch
WebUI.click(modal('branch-dropdown'))
WebUI.setText(modal('branch-dropdown-searchfield'), 'Head Office')
WebUI.sendKeys(modal('branch-dropdown-searchfield'), Keys.chord(Keys.ENTER))

// Isi Customer
WebUI.click(modal('customer-dropdown'))
WebUI.setText(modal('customer-dropdown-searchfield'), 'Act Cargo')
WebUI.sendKeys(modal('customer-dropdown-searchfield'), Keys.chord(Keys.ENTER))

// Isi Sales
WebUI.click(modal('sales-dropdown'))
WebUI.setText(modal('sales-dropdown-searchfield'), 'zharfan')
WebUI.sendKeys(modal('sales-dropdown-searchfield'), Keys.chord(Keys.ENTER))

// Isi ETD dengan tanggal hari ini
WebUI.setText(modal('input_etd'), tanggalHariIni)
WebUI.sendKeys(modal('input_etd'), Keys.chord(Keys.ENTER))

// Uncomment jika ingin pakai nomor manual
// WebUI.click(modal('jobfileno-toggle'))
// WebUI.setText(modal('jobfileno-dropdown-manual-inputfield'), 'NOMOR MANUAL')

// Pilih tipe jobfile
WebUI.click(modal('label_Nomination'))
WebUI.click(modal('label_Export'))
WebUI.click(modal('label_Ocean Freight'))
WebUI.click(modal('label_FCL'))

WebUI.click(modal('button_Save'))

WebUI.comment('🎉 [SUCCESS] Jobfile Berhasil Dibuat!')