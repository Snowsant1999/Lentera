package com.PPBM.lentera

import com.PPBM.lentera.backend.auth.repository.RecoveryDeepLinkParser
import com.PPBM.lentera.backend.auth.repository.RecoveryDeepLinkResult
import com.PPBM.lentera.backend.core.WITA_ZONE
import com.PPBM.lentera.backend.core.daysUntilDeadline
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.backend.core.isPgUniqueViolation
import com.PPBM.lentera.backend.core.mapSupabaseError
import com.PPBM.lentera.backend.core.normalizeDokumenLamaranJenis
import com.PPBM.lentera.backend.core.normalizeDokumenMahasiswaJenis
import com.PPBM.lentera.backend.core.normalizeLamaranStatus
import com.PPBM.lentera.backend.core.normalizeLaporanStatus
import com.PPBM.lentera.backend.core.normalizeLowonganStatus
import com.PPBM.lentera.backend.core.normalizeSistemKerja
import com.PPBM.lentera.backend.core.normalizeStatusAkun
import com.PPBM.lentera.backend.core.normalizeTempatStatus
import com.PPBM.lentera.backend.core.parseLocalDateFlexible
import com.PPBM.lentera.backend.core.parseWitaIsoTimestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZonedDateTime

class LenteraContractsTest {

    private val fixedNow = ZonedDateTime.of(2026, 10, 12, 9, 30, 0, 0, WITA_ZONE)

    @Test
    fun normalizeSistemKerja_mapsAliasesAndFallback() {
        assertEquals("On-site", normalizeSistemKerja("wfo"))
        assertEquals("On-site", normalizeSistemKerja(" Tatap Muka "))
        assertEquals("Remote", normalizeSistemKerja("WFH"))
        assertEquals("Hybrid", normalizeSistemKerja("campuran"))
        assertEquals("Hybrid", normalizeSistemKerja("unknown", fallback = "Hybrid"))
    }

    @Test
    fun normalizeStatusEnums_matchDatabaseChecks() {
        assertEquals("aktif", normalizeStatusAkun("Aktif"))
        assertEquals("belum_aktivasi", normalizeStatusAkun("Belum Aktivasi"))
        assertEquals("nonaktif", normalizeStatusAkun("tidak aktif"))
        assertEquals("lulus", normalizeStatusAkun("LULUS"))
        assertEquals("belum_aktivasi", normalizeStatusAkun("lainnya"))

        assertEquals("diperiksa", normalizeLaporanStatus("verifikasi"))
        assertEquals("diproses", normalizeLaporanStatus("penanganan"))
        assertEquals("diproses", normalizeLaporanStatus("investigasi"))
        assertEquals("selesai", normalizeLaporanStatus("SELESAI"))
        assertEquals("ditolak", normalizeLaporanStatus("Ditolak"))
        assertEquals("baru", normalizeLaporanStatus("???"))

        assertEquals("terkirim", normalizeLamaranStatus("dikirim"))
        assertEquals("pemeriksaan", normalizeLamaranStatus("verifikasi berkas"))
        assertEquals("perlu_perbaikan", normalizeLamaranStatus("perlu revisi"))
        assertEquals("seleksi", normalizeLamaranStatus("wawancara"))
        assertEquals("diterima", normalizeLamaranStatus("Diterima"))
        assertEquals("terkirim", normalizeLamaranStatus("unknown"))

        assertEquals("draf", normalizeTempatStatus("draft"))
        assertEquals("aktif", normalizeTempatStatus("aktif"))
        assertEquals("arsip", normalizeTempatStatus("nonaktif"))

        assertEquals("terbit", normalizeLowonganStatus("aktif"))
        assertEquals("ditutup", normalizeLowonganStatus("tutup"))
        assertEquals("draf", normalizeLowonganStatus("unknown"))
    }

    @Test
    fun normalizeDokumenJenis_mapsDocumentCategories() {
        assertEquals("Transkrip", normalizeDokumenMahasiswaJenis("Transkrip Nilai Semester 5"))
        assertEquals("Portofolio", normalizeDokumenMahasiswaJenis("Link Portofolio UI"))
        assertEquals("CV", normalizeDokumenMahasiswaJenis("Resume PDF"))

        assertEquals("Surat Pengantar", normalizeDokumenLamaranJenis("Surat Pengantar Fakultas"))
        assertEquals("KTM", normalizeDokumenLamaranJenis("Kartu Tanda Mahasiswa"))
        assertEquals("Sertifikat", normalizeDokumenLamaranJenis("Sertifikat Kompetensi"))
        assertEquals("CV", normalizeDokumenLamaranJenis("Curriculum Vitae"))
    }

    @Test
    fun parseWitaIsoTimestamp_parsesSupportedFormatsDeterministically() {
        assertEquals(
            "2026-10-12T14:30:00+08:00",
            parseWitaIsoTimestamp("2026-10-12T06:30:00Z", now = fixedNow)
        )
        assertEquals(
            "2026-10-12T14:30:00+08:00",
            parseWitaIsoTimestamp("12 Oktober 2026, 14.30 WITA", now = fixedNow)
        )
        assertEquals(
            "2026-11-05T08:15:00+08:00",
            parseWitaIsoTimestamp("05/11/2026 08:15", now = fixedNow)
        )
        assertEquals(
            "2026-10-12T10:00:00+08:00",
            parseWitaIsoTimestamp("hari ini, 10.00", now = fixedNow)
        )
        assertEquals(
            "2026-10-11T20:45:00+08:00",
            parseWitaIsoTimestamp("kemarin, 20.45", now = fixedNow)
        )
        assertEquals(
            "2026-10-12T09:30:00+08:00",
            parseWitaIsoTimestamp("", now = fixedNow)
        )
    }

    @Test
    fun parseLocalDateAndDaysUntilDeadline_handlesDatesAndClampsPast() {
        val today = LocalDate.of(2026, 10, 12)
        assertEquals(LocalDate.of(2026, 10, 20), parseLocalDateFlexible("2026-10-20"))
        assertEquals(LocalDate.of(2026, 10, 20), parseLocalDateFlexible("20 Oktober 2026"))
        assertEquals(LocalDate.of(2026, 11, 5), parseLocalDateFlexible("05-11-2026"))
        assertNull(parseLocalDateFlexible("tidak valid"))

        assertEquals(8, daysUntilDeadline("2026-10-20", today))
        assertEquals(0, daysUntilDeadline("2026-10-12", today))
        assertEquals(0, daysUntilDeadline("2026-10-01", today))
        assertEquals(0, daysUntilDeadline("invalid", today))
    }

    @Test
    fun errorMappers_inspectCauseChainAndReturnIndonesianMessages() {
        val nestedUnique = RuntimeException("wrapper", IllegalStateException("ERROR: duplicate key value 23505"))
        assertTrue(isPgUniqueViolation(nestedUnique))
        assertFalse(isPgUniqueViolation(RuntimeException("timeout")))

        assertEquals(
            "Kamu sudah melamar pada lowongan ini.",
            mapSupabaseError(RuntimeException(RuntimeException("violates uq_lamaran_mhs_lowongan")), "fallback")
        )
        assertEquals(
            "Lowongan ini sudah ditutup atau melewati batas pendaftaran.",
            mapSupabaseError(
                RuntimeException("new row violates row-level security policy for table \"lamaran\""),
                "fallback"
            )
        )
        assertEquals(
            "NIM sudah terdaftar dalam sistem.",
            mapSupabaseError(RuntimeException("23505 duplicate key nim_key"), "fallback")
        )
        assertEquals(
            "Email sudah terdaftar dalam sistem.",
            mapSupabaseError(RuntimeException("23505 duplicate key profiles_email_key"), "fallback")
        )
        assertEquals(
            "Akun dinonaktifkan oleh admin kampus.",
            mapSupabaseError(RuntimeException("Status akun nonaktif"), "fallback")
        )
        assertEquals(
            "Sesi login kamu sudah berakhir. Silakan masuk kembali.",
            mapSupabaseError(RuntimeException("JWT expired"), "fallback")
        )
        assertEquals(
            "Kamu tidak memiliki izin untuk melakukan tindakan ini.",
            mapSupabaseError(RuntimeException("permission denied for relation"), "fallback")
        )
        assertEquals(
            "Email tidak terdaftar dalam sistem.",
            mapSupabaseError(RuntimeException("Signups not allowed for otp"), "fallback")
        )
        assertEquals(
            "Terlalu banyak permintaan email. Coba beberapa saat lagi.",
            mapSupabaseError(RuntimeException("over_email_send_rate_limit"), "fallback")
        )
        assertEquals(
            "Alamat email belum diizinkan oleh layanan SMTP server.",
            mapSupabaseError(RuntimeException("email_address_not_authorized"), "fallback")
        )
        assertEquals(
            "Kode atau tautan verifikasi tidak valid atau sudah kedaluwarsa.",
            mapSupabaseError(RuntimeException("otp_expired"), "fallback")
        )
        assertEquals(
            "Terjadi kesalahan",
            mapSupabaseError(RuntimeException(""), "Terjadi kesalahan")
        )
    }

    @Test
    fun recoveryDeepLinkParser_parsesValidAndInvalidUrisSafely() {
        val valid = RecoveryDeepLinkParser.parse(
            scheme = "lentera",
            host = "reset-password",
            encodedQuery = null,
            encodedFragment = "access_token=jwt123&expires_at=1700000000&expires_in=3600&refresh_token=ref456&token_type=bearer&type=recovery"
        )
        assertTrue(valid is RecoveryDeepLinkResult.TokenSession)
        val sessionResult = valid as RecoveryDeepLinkResult.TokenSession
        assertEquals("recovery", sessionResult.type)
        assertTrue(sessionResult.fragment.contains("access_token=jwt123"))

        val expired = RecoveryDeepLinkParser.parse(
            scheme = "lentera",
            host = "reset-password",
            encodedQuery = null,
            encodedFragment = "error=access_denied&error_code=otp_expired&error_description=Email+link+is+invalid+or+has+expired"
        )
        assertTrue(expired is RecoveryDeepLinkResult.Error)
        assertEquals("otp_expired", (expired as RecoveryDeepLinkResult.Error).code)
        assertTrue(expired.message.contains("kedaluwarsa", ignoreCase = true))

        val queryError = RecoveryDeepLinkParser.parse(
            scheme = "lentera",
            host = "reset-password",
            encodedQuery = "error=access_denied&error_description=Denied",
            encodedFragment = null
        )
        assertTrue(queryError is RecoveryDeepLinkResult.Error)
        assertEquals("access_denied", (queryError as RecoveryDeepLinkResult.Error).code)

        val incomplete = RecoveryDeepLinkParser.parse(
            scheme = "lentera",
            host = "reset-password",
            encodedQuery = null,
            encodedFragment = "type=recovery"
        )
        assertTrue(incomplete is RecoveryDeepLinkResult.Error)
        assertEquals("invalid_or_incomplete_link", (incomplete as RecoveryDeepLinkResult.Error).code)

        val unsupportedType = RecoveryDeepLinkParser.parse(
            scheme = "lentera",
            host = "reset-password",
            encodedQuery = null,
            encodedFragment = "access_token=jwt123&expires_in=3600&refresh_token=ref456&token_type=bearer&type=email_change"
        )
        assertTrue(unsupportedType is RecoveryDeepLinkResult.Error)
        assertEquals("unsupported_link_type", (unsupportedType as RecoveryDeepLinkResult.Error).code)

        val foreignUri = RecoveryDeepLinkParser.parse(
            scheme = "https",
            host = "example.com",
            encodedQuery = null,
            encodedFragment = "access_token=jwt123"
        )
        assertNull(foreignUri)
    }

    @Test
    fun extractShortFileType_returnsUppercaseTruncatedExtensionOrFallback() {
        assertEquals("PDF", extractShortFileType("cv_mahasiswa.pdf"))
        assertEquals("FILE", extractShortFileType("tanpa_ekstensi"))
        assertEquals("VERYLONGEX", extractShortFileType("arsip.verylongextension"))
    }
}

