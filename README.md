![Katalon Studio](https://img.shields.io/badge/Katalon_Studio-9.x-blue)
![Language](https://img.shields.io/badge/Language-Groovy-green)
[![Video Demo - Automation Testing Oaktree ERP](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://drive.google.com/file/d/1DKe3fGh5OrwXwy7nPFEAoYw9rO5w5C0l/view?usp=sharing)
![Status](https://img.shields.io/badge/Status-Active-brightgreen)

## Demo
link: _In Progress_

# Oak-Test-Automation

Automation test suite untuk Oaktree ERP - sistem freight forwarding terintegrasi dengan Accurate Online. Dibangun menggunakan Katalon Studio sebagai bagian dari pekerjaan Quality Assurance di PT. Rimba Ananta Vikasa Indonesia.

## Konteks

Oaktree ERP adalah sistem internal yang mengelola alur bisnis freight forwarding, mulai dari pembuatan jobfile hingga invoicing. Sistem ini terintegrasi dengan Accurate Online melalui OAuth 2.0. Automation di repo ini mencakup 4 alur bisnis kritis yang sebelumnya hanya diuji secara manual.

## Test Cases

| ID | Alur Bisnis | Keterangan |
|----|-------------|------------|
| TC-01 | OAuth Token - Accurate Online | Autentikasi dan pengambilan token akses sebelum transaksi |
| TC-02 | Create Jobfile | Pembuatan jobfile baru beserta validasi field wajib |
| TC-03 | Create Jobfile-Buying | Input data buying pada jobfile yang sudah ada |
| TC-04 | Create Master Customer | Pendaftaran customer baru ke dalam master data |

## Struktur Direktori
## Struktur Direktori

| Folder / File | Keterangan |
|---|---|
| `Keywords/oaktree/` | Custom keywords reusable |
| `Keywords/oaktree/OaktreeLogin.groovy` | Login handler + session management |
| `Keywords/oaktree/Select2Input.groovy` | Interaksi dropdown berbasis Select2 |
| `Keywords/oaktree/SweetAlert.groovy` | Handler popup konfirmasi SweetAlert |
| `Object Repository/` | Locator elemen UI (XPath, CSS) |
| `Scripts/` | Script utama per test case |
| `Test Cases/` | Definisi test case Katalon |
| `Test Suites/` | Konfigurasi eksekusi suite |
| `Data Files/` | Data input test |
| `Include/config/` | Konfigurasi environment |

## Custom Keywords

Tiga keyword dibuat untuk menangani elemen UI non-standar yang tidak bisa ditangani langsung oleh built-in Katalon:

**OaktreeLogin** - mengelola proses login dan mempertahankan sesi antar test case.

**Select2Input** - menangani dropdown berbasis library Select2 yang tidak bisa diinteraksi lewat `click` biasa. Menggunakan JavaScript execution untuk membuka opsi dan memilih nilai.

**SweetAlert** - menunggu dan mengkonfirmasi popup SweetAlert2 yang muncul setelah aksi submit. Menghindari race condition antara popup dan langkah berikutnya.

## Tantangan Teknis

Beberapa kendala yang ditemui saat membangun script ini dan cara mengatasinya:

- **Select2 dropdown** tidak merespons interaksi Selenium standar. Diselesaikan dengan JavaScript execution langsung ke DOM.
- **SweetAlert popup** muncul asinkron sehingga sering menyebabkan `StaleElementException`. Ditangani dengan explicit wait dan keyword khusus.
- **AJAX loader** yang belum selesai menyebabkan elemen belum siap diklik. Diatasi dengan menunggu kondisi `document.readyState == complete`.
- **Elemen tertutup topbar** fixed-position menghalangi klik. Diselesaikan dengan `scrollIntoView` via JavaScript sebelum interaksi.
- **OAuth flow** memerlukan token yang diambil dari endpoint Accurate sebelum setiap sesi. Diintegrasikan ke dalam keyword login agar tidak perlu diulang di setiap test case.

## Tools

- Katalon Studio 9.x
- Groovy (scripting language)
- Selenium WebDriver (via Katalon)
- ClickUp (bug tracking & task management)
