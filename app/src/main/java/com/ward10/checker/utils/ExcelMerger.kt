package com.ward10.checker.utils

import android.content.Context
import android.net.Uri
import com.ward10.checker.db.WardPerson
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object ExcelMerger {
    fun readFromUri(context: Context, uri: Uri): List<WardPerson> {
        val list = mutableListOf<WardPerson>()
        context.contentResolver.openInputStream(uri)?.use { input ->
            val wb = XSSFWorkbook(input)
            val sheet = wb.getSheetAt(0)
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue
                val name = row.getCell(0)?.toString()?.trim().orEmpty()
                val mobile = row.getCell(1)?.toString()?.trim().orEmpty()
                if (name.isNotBlank() && mobile.isNotBlank()) list.add(WardPerson(name=name, mobile=mobile))
            }
            wb.close()
        }
        return list
    }
}
