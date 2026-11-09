package com.PPBM.lentera.backend.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val WITA_ZONE: ZoneId = ZoneId.of("Asia/Makassar")

@Serializable
data class BuatLaporanRpcResult(
    val id: String,
    @SerialName("nomor_laporan")
    val nomorLaporan: String
)

private val INDONESIAN_MONTHS = mapOf(
    "januari" to 1, "jan" to 1,
    "februari" to 2, "feb" to 2,
    "maret" to 3, "mar" to 3,
    "april" to 4, "apr" to 4,
    "mei" to 5, "may" to 5,
    "juni" to 6, "jun" to 6,
    "juli" to 7, "jul" to 7,
    "agustus" to 8, "agt" to 8, "agu" to 8, "aug" to 8,
    "september" to 9, "sep" to 9, "sept" to 9,
    "oktober" to 10, "okt" to 10, "oct" to 10,
    "november" to 11, "nov" to 11,
    "desember" to 12, "des" to 12, "dec" to 12
)

private val INDONESIAN_DATE_REGEX = Regex(
    """^(\d{1,2})\s+([A-Za-z]+)\s+(\d{4})(?:[,;\s·-]+(\d{1,2})[.:](\d{2})(?:[.:](\d{2}))?)?$"""
)

private val NUMERIC_DATE_REGEX = Regex(
    """^(\d{1,2})[/-](\d{1,2})[/-](\d{4})(?:[,;\s·T-]+(\d{1,2})[.:](\d{2})(?:[.:](\d{2}))?)?$"""
)

private val RELATIVE_DATE_REGEX = Regex(
    """^(hari\s+ini|kemarin)(?:[,;\s·-]+(\d{1,2})[.:](\d{2}))?$""",
    RegexOption.IGNORE_CASE
)

fun normalizeSistemKerja(raw: String, fallback: String = "On-site"): String {
    return when (raw.trim().lowercase()) {
        "on-site", "onsite", "on site", "on_site", "wfo", "tatap muka" -> "On-site"
        "remote", "wfh", "daring" -> "Remote"
        "hybrid", "campuran" -> "Hybrid"
        else -> fallback
    }
}

fun normalizeStatusAkun(raw: String): String {
    return when (raw.trim().lowercase().replace(" ", "_")) {
        "aktif" -> "aktif"
        "belum_aktivasi" -> "belum_aktivasi"
        "lulus" -> "lulus"
        "nonaktif", "non_aktif", "tidak_aktif" -> "nonaktif"
        else -> "belum_aktivasi"
    }
}

fun normalizeLaporanStatus(raw: String): String {
    return when (raw.trim().lowercase()) {
        "baru" -> "baru"
        "diperiksa", "verifikasi" -> "diperiksa"
        "diproses", "investigasi", "penanganan" -> "diproses"
        "selesai" -> "selesai"
        "ditolak" -> "ditolak"
        else -> "baru"
    }
}

fun normalizeLamaranStatus(raw: String): String {
    return when (raw.trim().lowercase().replace(" ", "_")) {
        "terkirim", "dikirim" -> "terkirim"
        "pemeriksaan", "verifikasi_berkas" -> "pemeriksaan"
        "perlu_perbaikan", "revisi", "perlu_revisi" -> "perlu_perbaikan"
        "seleksi", "wawancara" -> "seleksi"
        "diterima" -> "diterima"
        "ditolak" -> "ditolak"
        else -> "terkirim"
    }
}

fun normalizeTempatStatus(raw: String): String {
    return when (raw.trim().lowercase()) {
        "draf", "draft" -> "draf"
        "aktif" -> "aktif"
        "arsip", "arsipkan", "nonaktif", "ditutup" -> "arsip"
        else -> "draf"
    }
}

fun normalizeLowonganStatus(raw: String): String {
    return when (raw.trim().lowercase()) {
        "draf", "draft" -> "draf"
        "terbit", "terbitkan", "aktif" -> "terbit"
        "ditutup", "tutup", "arsip", "arsipkan", "nonaktif" -> "ditutup"
        else -> "draf"
    }
}

fun normalizeDokumenMahasiswaJenis(raw: String): String {
    val low = raw.trim().lowercase()
    return when {
        low.contains("transkrip") -> "Transkrip"
        low.contains("portofolio") -> "Portofolio"
        else -> "CV"
    }
}

fun normalizeDokumenLamaranJenis(raw: String): String {
    val low = raw.trim().lowercase()
    return when {
        low.contains("surat") -> "Surat Pengantar"
        low.contains("transkrip") -> "Transkrip"
        low.contains("ktm") || low.contains("kartu tanda") -> "KTM"
        low.contains("portofolio") -> "Portofolio"
        low.contains("sertifikat") -> "Sertifikat"
        else -> "CV"
    }
}

fun parseWitaIsoTimestamp(
    input: String,
    zoneId: ZoneId = WITA_ZONE,
    now: ZonedDateTime = ZonedDateTime.now(zoneId)
): String {
    val cleaned = input.trim()
        .replace(Regex("""\s+(WITA|WIB|WIT)$""", RegexOption.IGNORE_CASE), "")
        .trim()
    val fallbackZoned = now.withZoneSameInstant(zoneId)

    if (cleaned.isEmpty()) {
        return fallbackZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    runCatching { OffsetDateTime.parse(cleaned).atZoneSameInstant(zoneId) }
        .getOrNull()?.let { return it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) }

    runCatching { Instant.parse(cleaned).atZone(zoneId) }
        .getOrNull()?.let { return it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) }

    for (pattern in listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd HH.mm"
    )) {
        runCatching {
            LocalDateTime.parse(cleaned, DateTimeFormatter.ofPattern(pattern)).atZone(zoneId)
        }.getOrNull()?.let { return it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) }
    }

    runCatching { LocalDate.parse(cleaned, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(zoneId) }
        .getOrNull()?.let { return it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) }

    RELATIVE_DATE_REGEX.matchEntire(cleaned)?.let { match ->
        val baseDate = if (match.groupValues[1].lowercase().startsWith("kemarin")) {
            fallbackZoned.toLocalDate().minusDays(1)
        } else {
            fallbackZoned.toLocalDate()
        }
        val hour = match.groupValues[2].toIntOrNull()
        val minute = match.groupValues[3].toIntOrNull()
        val time = if (hour != null && minute != null && hour in 0..23 && minute in 0..59) {
            LocalTime.of(hour, minute)
        } else {
            fallbackZoned.toLocalTime().truncatedTo(ChronoUnit.MINUTES)
        }
        return ZonedDateTime.of(baseDate, time, zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    INDONESIAN_DATE_REGEX.matchEntire(cleaned)?.let { match ->
        val day = match.groupValues[1].toIntOrNull()
        val month = INDONESIAN_MONTHS[match.groupValues[2].lowercase()]
        val year = match.groupValues[3].toIntOrNull()
        if (day != null && month != null && year != null) {
            val hour = match.groupValues[4].toIntOrNull() ?: 0
            val minute = match.groupValues[5].toIntOrNull() ?: 0
            val second = match.groupValues[6].toIntOrNull() ?: 0
            runCatching {
                LocalDateTime.of(year, month, day, hour, minute, second).atZone(zoneId)
            }.getOrNull()?.let { return it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) }
        }
    }

    NUMERIC_DATE_REGEX.matchEntire(cleaned)?.let { match ->
        val day = match.groupValues[1].toIntOrNull()
        val month = match.groupValues[2].toIntOrNull()
        val year = match.groupValues[3].toIntOrNull()
        if (day != null && month != null && year != null) {
            val hour = match.groupValues[4].toIntOrNull() ?: 0
            val minute = match.groupValues[5].toIntOrNull() ?: 0
            val second = match.groupValues[6].toIntOrNull() ?: 0
            runCatching {
                LocalDateTime.of(year, month, day, hour, minute, second).atZone(zoneId)
            }.getOrNull()?.let { return it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) }
        }
    }

    return fallbackZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

fun parseLocalDateFlexible(input: String): LocalDate? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return null

    if (trimmed.length >= 10) {
        runCatching { LocalDate.parse(trimmed.take(10), DateTimeFormatter.ISO_LOCAL_DATE) }
            .getOrNull()?.let { return it }
    }

    runCatching { OffsetDateTime.parse(trimmed).atZoneSameInstant(WITA_ZONE).toLocalDate() }
        .getOrNull()?.let { return it }

    INDONESIAN_DATE_REGEX.matchEntire(trimmed)?.let { match ->
        val day = match.groupValues[1].toIntOrNull()
        val month = INDONESIAN_MONTHS[match.groupValues[2].lowercase()]
        val year = match.groupValues[3].toIntOrNull()
        if (day != null && month != null && year != null) {
            runCatching { return LocalDate.of(year, month, day) }
        }
    }

    NUMERIC_DATE_REGEX.matchEntire(trimmed)?.let { match ->
        val day = match.groupValues[1].toIntOrNull()
        val month = match.groupValues[2].toIntOrNull()
        val year = match.groupValues[3].toIntOrNull()
        if (day != null && month != null && year != null) {
            runCatching { return LocalDate.of(year, month, day) }
        }
    }

    return null
}

fun daysUntilDeadline(
    batasPendaftaran: String,
    today: LocalDate = LocalDate.now(WITA_ZONE)
): Int {
    val deadline = parseLocalDateFlexible(batasPendaftaran) ?: return 0
    return ChronoUnit.DAYS.between(today, deadline).toInt().coerceAtLeast(0)
}

fun isPgUniqueViolation(error: Throwable): Boolean {
    val msg = generateSequence(error) { it.cause }
        .mapNotNull { it.message }
        .joinToString(" | ")
    return msg.contains("23505") ||
        msg.contains("duplicate key", ignoreCase = true) ||
        msg.contains("uq_lamaran_mhs_lowongan", ignoreCase = true) ||
        msg.contains("already exists", ignoreCase = true)
}

fun mapSupabaseError(error: Throwable, fallback: String): String {
    val msg = generateSequence(error) { it.cause }
        .mapNotNull { it.message }
        .joinToString(" | ")
    return when {
        msg.contains("uq_lamaran_mhs_lowongan", ignoreCase = true) ->
            "Kamu sudah melamar pada lowongan ini."
        msg.contains("lowongan_terbuka", ignoreCase = true) ||
            msg.contains("row-level security policy", ignoreCase = true) && msg.contains("lamaran", ignoreCase = true) ->
            "Lowongan ini sudah ditutup atau melewati batas pendaftaran."
        msg.contains("23505") && msg.contains("nim", ignoreCase = true) ->
            "NIM sudah terdaftar dalam sistem."
        msg.contains("23505") && msg.contains("email", ignoreCase = true) ->
            "Email sudah terdaftar dalam sistem."
        msg.contains("23505") || msg.contains("duplicate key", ignoreCase = true) ->
            "Data yang sama sudah terdaftar di sistem."
        msg.contains("nonaktif", ignoreCase = true) ->
            "Akun dinonaktifkan oleh admin kampus."
        msg.contains("Sesi login berakhir", ignoreCase = true) ||
            msg.contains("Belum login", ignoreCase = true) ||
            msg.contains("JWT", ignoreCase = true) ->
            "Sesi login kamu sudah berakhir. Silakan masuk kembali."
        msg.contains("row-level security", ignoreCase = true) ||
            msg.contains("permission denied", ignoreCase = true) ->
            "Kamu tidak memiliki izin untuk melakukan tindakan ini."
        else -> error.message?.takeIf { it.isNotBlank() } ?: fallback
    }
}

fun extractShortFileType(fileName: String, fallback: String = "FILE"): String {
    val ext = fileName.substringAfterLast('.', "").trim().uppercase()
    return ext.ifBlank { fallback }.take(10)
}

