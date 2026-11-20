<div align="center">

# SUKIZY

**Pemutar musik YouTube Music dengan tampilan gaya iOS yang ringan untuk Android**

Versi **14.1.0**

[![TikTok](https://img.shields.io/badge/TikTok-@a2unknowwn-000000?style=for-the-badge&logo=tiktok&logoColor=white)](https://www.tiktok.com/@a2unknowwn)
[![GitHub](https://img.shields.io/badge/GitHub-zychohehe-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/zychohehe)

</div>

> [!WARNING]
> Memakai SUKIZY untuk mengakses YouTube Music bisa melanggar Ketentuan Layanan YouTube. Risiko ditanggung pengguna sendiri.

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
