<div align="center">

# SUKIZY

**Pemutar musik YouTube Music dengan tampilan gaya iOS yang ringan untuk Android**

Versi **14.2.0**

[![TikTok](https://img.shields.io/badge/TikTok-@a2unknowwn-000000?style=for-the-badge&logo=tiktok&logoColor=white)](https://www.tiktok.com/@a2unknowwn)
[![GitHub](https://img.shields.io/badge/GitHub-zychohehe-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/zychohehe)

</div>

> [!WARNING]
> Memakai SUKIZY untuk mengakses YouTube Music bisa melanggar Ketentuan Layanan YouTube. Risiko ditanggung pengguna sendiri.

## Rilis terbaru

<!-- changelog:start -->
#### v14.2.0 (2026-11-20)

##### Fitur baru
- Home personal: kartu Lanjutkan mendengarkan dan ubin pintasan dengan efek tekan dan getaran
- Search kategori mood dan genre bergaya bento
- Rilis otomatis: GitHub Release, changelog, dan APK release bertanda tangan
- Cek otomatis: cek cepat dan lint Kotlin di workflow build

##### Perapian kode
- Pengaturan, Player, dan Tentang diselaraskan dengan gaya bento (kartu Claude bisa diklik)

<!-- changelog:end -->

## Yang baru di 14.2.0

- **Home personal:** kartu *Lanjutkan mendengarkan* muncul saat ada lagu aktif (sampul, judul, artis, tombol play/pause). Ubin pintasan kini mengecil saat ditekan dan memberi getaran halus.
- **Search kategori:** sebelum mengetik ada kategori mood dan genre bergaya bento (Pop, Hip-Hop, Rock, Santai, Olahraga, Fokus, Pesta, Nostalgia); sekali tekan langsung mencari.
- **Pengaturan:** ikon halaman berada di ubin tonal dan judul grup memakai aksen warna tema.
- **Player:** tombol sebelumnya/berikutnya memberi getaran halus dan bentuknya lebih kotak membulat.
- **Tentang:** kartu Claude bisa diklik dan membuka claude.ai.
- **Rilis otomatis:** setiap bump versi membuat GitHub Release `vX.Y.Z` dengan APK dan catatan rilis dari `CHANGELOG.md`. Dengan 4 secret keystore, APK release bertanda tangan ikut dilampirkan.
- **Cek otomatis:** job *Cek cepat* (tes skrip, resource string, YAML) berjalan sebelum bump dan build; *Lint Kotlin* berjalan paralel dan tidak memblokir.

## Yang baru di 14.1.0

- **Gaya baru ala tren 2026 (bento + aurora):** Home punya kartu sapaan bergradien tiga warna tema dengan dua ubin pintasan (Cari dan Koleksi). Sudut lebih besar, tipografi lebih tebal, tanpa blur supaya tetap ringan.
- **Koleksi:** judul besar, kartu akses cepat bergradien dengan warna bergantian, dan baris daftar berbentuk kartu tonal.
- **Search:** kolom pencarian dengan garis tipis warna tema dan riwayat pencarian berbentuk kartu.
- **Tentang:** bagian *Kontributor* dengan **Claude** tepat di bawah Kreator.
- **Versi otomatis:** workflow SUKIZY BUILD APK menaikkan patch di setiap push ke branch default, memperbarui README, membuat tag `vX.Y.Z`, dan memberi nama APK sesuai versi. Kalau push sudah mengubah `appVersion` sendiri (seperti rilis ini), versinya dipakai apa adanya. `versionCode` dihitung dari `appVersion`.

## Yang baru di 14.0.7

- **Navbar polos dan ringan:** tidak lagi memakai liquid glass. Sekarang berupa pill datar dengan penanda tab `secondaryContainer`, tanpa blur, gradien, dan border.
- **Optimasi performa:** blur live dimatikan di seluruh aplikasi, permukaan kaca (mini player, top bar) memakai tint datar dengan shadow kecil, dan motion memakai skema standar (tanpa pantulan).
- **APK debug non-debuggable:** build debug berjalan jauh lebih cepat di perangkat.
- **Perbaikan build:** error `Unresolved reference 'VisibilityThreshold'` di `IosMotion.kt` sudah diperbaiki.

## Yang baru di 14.0.6

- **Perbaikan build:** error `CONFLICTING_JVM_DECLARATIONS` pada `GlassBlurPolicy` (`setScrolling` diganti `updateScrolling`).
- **Animasi mulus:** gerak ala iOS dengan spring tanpa pantulan di home.
- **Home lebih rapi:** judul section tebal dan bersih, tombol aksi berupa pill kecil, jarak antar shelf lebih lega.
- **Halaman Tentang:** kini "Tentang SUKIZY" dengan satu kreator dan satu link sosmed (TikTok).

## Fitur

- Pemutaran YouTube Music dengan atau tanpa login
- Tampilan Material 3 yang ringan dan responsif
- Lirik, pengenalan musik, Listen Together, dan casting
- Tanpa iklan

## Kreator

Dikembangkan oleh [zychohehe](https://github.com/zychohehe).

## Rilis otomatis (GitHub Actions)

Setiap push ke branch default: *Cek cepat* → *Naikkan versi* (patch, tag, `CHANGELOG.md`) → *SUKIZY BUILD APK* → GitHub Release. Ubah `appVersion` sendiri di `gradle.properties` untuk rilis minor atau major; versi itu dipakai apa adanya. Tulis commit gaya `feat: ...` / `fix: ...` supaya changelog rapi.

**Prasyarat satu kali**
1. Repo → Settings → Actions → General → Workflow permissions: **Read and write**.
2. Branch default tidak boleh diproteksi untuk push bot (atau beri pengecualian).
3. APK release bertanda tangan: buat keystore, lalu simpan 4 secret di Settings → Secrets and variables → Actions.

```bash
keytool -genkeypair -v -keystore sukizy-release.jks -alias sukizy -keyalg RSA -keysize 4096 -validity 36500
base64 -w0 sukizy-release.jks   # tempel hasilnya ke SUKIZY_KEYSTORE_B64
```

| Secret | Isi |
|---|---|
| `SUKIZY_KEYSTORE_B64` | keystore dalam base64 |
| `SUKIZY_KEYSTORE_PASSWORD` | password keystore |
| `SUKIZY_KEY_ALIAS` | alias key (contoh `sukizy`) |
| `SUKIZY_KEY_PASSWORD` | password key |

Simpan file `.jks` dan passwordnya di tempat aman: kalau hilang, APK baru tidak bisa menimpa versi lama. Tanpa keempat secret, rilis tetap dibuat sebagai *pre-release* berisi APK debug. Secret `SUKIZY_DEBUG_KEYSTORE_B64` (opsional) menjaga tanda tangan APK debug tetap sama antar build.

Sosial media: [TikTok @a2unknowwn](https://www.tiktok.com/@a2unknowwn)

### Kontributor

- **Claude** (Anthropic): kontributor desain dan kode.

## Build

Proyek memakai Gradle dan JDK 25. Build APK debug:

```sh
./gradlew :androidApp:assembleDebug
```

Sebelum build, pastikan folder `metroproto` dan `opusDecoder/src/main/jni/libopus` terisi (workflow GitHub Actions sudah meng-clone-nya otomatis).

## Lisensi dan atribusi

SUKIZY adalah turunan dari [Metrolist](https://github.com/MetrolistGroup/Metrolist) karya MetrolistGroup dan dilisensikan di bawah **GPL-3.0**. Lihat [LICENSE](LICENSE).

> [!NOTE]
> Proyek ini tidak berafiliasi dengan, didanai, atau didukung oleh YouTube, Google LLC, atau afiliasinya. Semua merek dagang milik pemiliknya masing-masing.
