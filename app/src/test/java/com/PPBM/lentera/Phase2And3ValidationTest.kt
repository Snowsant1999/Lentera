package com.PPBM.lentera

import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.features.admin.akunmahasiswa.Mahasiswa
import com.PPBM.lentera.features.admin.akunmahasiswa.StatusAkun
import com.PPBM.lentera.features.admin.akunmahasiswa.StudentItem
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class Phase2And3ValidationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun userProfile_deserializesWithNullFotoProfilUrl() {
        val jsonString = """
            {
                "id": "123",
                "role": "mahasiswa",
                "nama_lengkap": "Budi Santoso",
                "email": "budi@lentera.ac.id",
                "status_akun": "aktif"
            }
        """.trimIndent()

        val profile = json.decodeFromString<UserProfile>(jsonString)
        assertEquals("123", profile.id)
        assertEquals("Budi Santoso", profile.namaLengkap)
        assertNull(profile.fotoProfilUrl)
    }

    @Test
    fun userProfile_deserializesWithFotoProfilUrl() {
        val avatarUrl = "https://example.com/avatar.jpg"
        val jsonString = """
            {
                "id": "123",
                "role": "mahasiswa",
                "nama_lengkap": "Budi Santoso",
                "email": "budi@lentera.ac.id",
                "status_akun": "aktif",
                "foto_profil_url": "$avatarUrl"
            }
        """.trimIndent()

        val profile = json.decodeFromString<UserProfile>(jsonString)
        assertEquals(avatarUrl, profile.fotoProfilUrl)
    }

    @Test
    fun mahasiswaModel_holdsFotoProfilUrl() {
        val mhs = Mahasiswa(
            nama = "Siti Rahma",
            nim = "2024001",
            prodi = "Teknik Informatika",
            status = StatusAkun.AKTIF,
            fotoProfilUrl = "https://example.com/siti.png"
        )
        assertEquals("https://example.com/siti.png", mhs.fotoProfilUrl)
    }

    @Test
    fun studentItem_holdsPhotoUrl() {
        val student = StudentItem(
            name = "Ahmad Dani",
            rawNim = "2024002",
            status = "Aktif",
            photoUrl = "https://example.com/ahmad.jpg"
        )
        assertEquals("https://example.com/ahmad.jpg", student.photoUrl)
    }
}

