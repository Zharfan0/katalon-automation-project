import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.Keys as Keys

import oaktree.OaktreeLogin

// =========================================================================
// SETUP: LOGIN OAKTREE
// =========================================================================
new OaktreeLogin().login()

// =========================================================================
// SELECT2 ENGINE - VERSI LAMA YANG SUDAH TERBUKTI WORK UNTUK FORM CUSTOMER
// =========================================================================
def select2DropdownInput = { String labelName, String textToSearch, String fallbackXpath = "" ->
    def driver = DriverFactory.getWebDriver()
    String xpathDropdown = ""

    if (labelName.equalsIgnoreCase("Doc Code") && !fallbackXpath.isEmpty()) {
        xpathDropdown = fallbackXpath
    } else if (labelName.equalsIgnoreCase("Term") || labelName.equalsIgnoreCase("Warehouse")) {
        xpathDropdown = "//label[contains(text(), '${labelName}')]/parent::div//span[contains(@class, 'select2-selection')]"
    } else {
        xpathDropdown = "//label[contains(text(), '${labelName}')]/following-sibling::div//span[contains(@class, 'select2-selection')]"
    }

    int retries = 0
    while (driver.findElements(By.xpath(xpathDropdown)).size() == 0 && retries < 10) {
        WebUI.delay(1)
        retries++
    }

    WebElement elementDropdown = driver.findElement(By.xpath(xpathDropdown))
    elementDropdown.click()
    WebUI.delay(1)

    TestObject strictSearchField = new TestObject('strict_search_field_' + labelName)
    strictSearchField.addProperty('xpath', com.kms.katalon.core.testobject.ConditionType.EQUALS,
        "//span[contains(@class, 'select2-container--open')]//input[@type='search' or @class='select2-search__field']", true)

    WebUI.waitForElementVisible(strictSearchField, 10)
    WebUI.setText(strictSearchField, textToSearch)
    WebUI.delay(1)
    WebUI.sendKeys(strictSearchField, Keys.chord(Keys.ENTER))
    WebUI.delay(1)
}

// =========================================================================
// BUKA FORM CREATE CUSTOMER
// =========================================================================
def c = { String name -> findTestObject("Page_Create Customer - PT Dummy Rimba Yogyakarta/${name}") }

WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/sidebar-master'))
WebUI.click(findTestObject('Page_Dashboard - PT Rimba Yogyakarta/icon_Customer'))
WebUI.click(findTestObject('Page_Master Customer - PT Dummy Rimba Yogyakarta/btn_Create New customer'))

// =========================================================================
// TAB 1: GENERAL INFO
// =========================================================================
WebUI.setText(c('input_name customer-general'), 'katalon auto')
WebUI.setText(c('input_Email_customer-general'), 'katalon@mail.com')

select2DropdownInput('Branch', 'All')

WebUI.setText(c('input__phone_number'),       '081215941111')
WebUI.setText(c('input_Mobile Phone_mobile'), '81212341233')
WebUI.setText(c('input_Faximile_faximile'),   '00000')
WebUI.setText(c('input_Website_website'),     'automation.com')

select2DropdownInput('Country',  'indonesia')
select2DropdownInput('Currency', 'idr')

WebUI.setText(c('input__province'),          'Jakarta DKI')
WebUI.setText(c('input__city'),              'Jakarta Pusat')
WebUI.setText(c('input_Post Code_postcode'), '55141')

select2DropdownInput('Term',      'net 10')
select2DropdownInput('Warehouse', 'gudang')

WebUI.setText(c('textarea__address'), 'jalan mt haryonoo jakpus')
WebUI.click(c('a_Next'))

// =========================================================================
// TAB 2: PIC INFO
// =========================================================================
WebUI.setText(c('input_Nama_pic_name'),                     'pik nama')
WebUI.setText(c('input_Email_pic_email'),                   'pik@mail.com')
WebUI.setText(c('input_Position_pic_position'),             'striker')
WebUI.setText(c('input_Division_pic_division'),             'pededeh')
WebUI.setText(c('input_Remarks_pic_remarks'),               'ini adlah remarks')
WebUI.setText(c('input_Phone Number_pic_mobile'),           '081212341234')
WebUI.setText(c('input_Business Contact Number_pic_phone'), '00008888')
WebUI.click(c('a_Next'))

// =========================================================================
// TAB 3: LEGALITY INFO
// =========================================================================
WebUI.setText(c('input_Name_legality_name'),       'name lgalitas')
WebUI.setText(c('input_Remarks_legality_remarks'), 'remarks legalitas')
WebUI.click(c('a_Next'))

// =========================================================================
// TAB 4: SHIPPING INFO
// =========================================================================
WebUI.click(c('label_Same as Billing Address'))
WebUI.click(c('a_Next'))

// =========================================================================
// TAB 5: TAX INFO
// =========================================================================
WebUI.click(c('label_Same as Billing Address_1'))
WebUI.setText(c('input_Tax Payer Name_tax_name'), 'bapaknya prengki')
WebUI.setText(c('input_NIK_nik'),                 '000000000000000')
WebUI.setText(c('input__npwp'),                   '0000000000000000')

select2DropdownInput('Doc Code', 'digunggung', "//section[@id='form-p-4']/div/div[3]/div/div[2]/div[2]/div/span/span/span")

// =========================================================================
// SUBMIT
// =========================================================================
WebUI.click(c('a_Submit'))
WebUI.click(c('button_OK-alert popup'))

WebUI.comment('🎉 [SUCCESS] Customer Berhasil Dibuat!')
WebUI.closeBrowser()