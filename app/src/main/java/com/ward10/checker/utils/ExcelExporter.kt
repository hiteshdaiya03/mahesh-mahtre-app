package com.ward10.checker.utils

import android.os.Environment
import com.ward10.checker.db.WardPerson
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object ExcelExporter {
    fun exportChecked(checkedList: List<WardPerson>): File {
        val wb = XSSFWorkbook()
        val sheet = wb.createSheet("Checked")
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Name")
        header.createCell(1).setCellValue("Mobile")

        checkedList.forEachIndexed { idx, p ->
            val r = sheet.createRow(idx + 1)
            r.createCell(0).setCellValue(p.name)
            r.createCell(1).setCellValue(p.mobile)
        }

        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MaheshMahtre")
        if (!folder.exists()) folder.mkdirs()

        val file = File(folder, "checked_list.xlsx")
        FileOutputStream(file).use { out -> wb.write(out) }
        wb.close()
        return file
    }
}
