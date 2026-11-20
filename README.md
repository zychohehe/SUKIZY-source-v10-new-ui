<div align="center">

# SUKIZY

**Pemutar musik YouTube Music dengan tampilan gaya iOS yang ringan untuk Android**

Versi **14.0.7**

[![TikTok](https://img.shields.io/badge/TikTok-@a2unknowwn-000000?style=for-the-badge&logo=tiktok&logoColor=white)](https://www.tiktok.com/@a2unknowwn)
[![GitHub](https://img.shields.io/badge/GitHub-zychohehe-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/zychohehe)

</div>

> [!WARNING]
> Memakai SUKIZY untuk mengakses YouTube Music bisa melanggar Ketentuan Layanan YouTube. Risiko ditanggung pengguna sendiri.

## Yang baru di 14.0.8

- **Versi otomatis:** workflow SUKIZY BUILD APK menaikkan `appVersion` (patch) di setiap push ke branch default, memperbarui README, membuat tag `vX.Y.Z`, dan memberi nama APK sesuai versi. `versionCode` kini dihitung dari `appVersion`. Run manual bisa memilih patch, minor, major, atau none.
- **Home lebih keren:** kartu sapaan bergradien sesuai jam di puncak Home dan judul section dengan aksen warna tema.
- **Koleksi lebih keren:** judul Koleksi, kartu akses cepat bergradien dengan warna tema bergantian.
- **Search lebih keren:** kolom pencarian dengan garis tipis warna tema dan riwayat pencarian berbentuk kartu.

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
