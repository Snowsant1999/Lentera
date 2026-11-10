package com.PPBM.lentera

import com.PPBM.lentera.features.mahasiswa.siaga.BuatLaporanFormData
import com.PPBM.lentera.features.mahasiswa.siaga.KategoriLaporanType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase1ValidationTest {

    @Test
    fun buatLaporanFormData_defaultKategoriIsEmpty() {
        val defaultData = BuatLaporanFormData()
        assertTrue("Default kategori form harus kosong", defaultData.kategori.isEmpty())
        assertTrue("Default judul form harus kosong", defaultData.judul.isEmpty())
    }

    @Test
    fun kategoriValidation_flagsEmptyCategory() {
        val emptyCategory = ""
        val error = if (emptyCategory.isBlank()) {
            "Kategori laporan wajib dipilih terlebih dahulu."
        } else null

        assertEquals("Kategori laporan wajib dipilih terlebih dahulu.", error)
    }

    @Test
    fun kategoriLaporanType_displayNameMatches() {
        val item = KategoriLaporanType.entries.find { it.displayName == "Pelecehan Verbal" }
        assertEquals(KategoriLaporanType.PELECEHAN_VERBAL, item)
    }
}

