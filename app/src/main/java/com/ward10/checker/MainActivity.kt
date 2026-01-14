package com.ward10.checker

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ward10.checker.db.AppDatabase
import com.ward10.checker.ui.WardAdapter
import com.ward10.checker.utils.ExcelExporter
import com.ward10.checker.utils.ExcelImporter
import com.ward10.checker.utils.ExcelMerger
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var etSearch: EditText
    private lateinit var btnUploadExcel: Button
    private lateinit var btnExport: Button
    private lateinit var tvStatus: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: WardAdapter

    private val pickExcel =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) mergeExcel(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)

        etSearch = findViewById(R.id.etSearch)
        btnUploadExcel = findViewById(R.id.btnUploadExcel)
        btnExport = findViewById(R.id.btnExport)
        tvStatus = findViewById(R.id.tvStatus)
        recycler = findViewById(R.id.recycler)

        adapter = WardAdapter { person ->
            scope.launch {
                withContext(Dispatchers.IO) { db.wardDao().markChecked(person.id) }
                searchNow(etSearch.text.toString())
            }
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        scope.launch { initDataIfNeeded() }

        etSearch.addTextChangedListener(SimpleTextWatcher { searchNow(it) })

        btnExport.setOnClickListener { scope.launch { exportExcel() } }

        btnUploadExcel.setOnClickListener {
            pickExcel.launch(arrayOf(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel"
            ))
        }
    }

    private suspend fun initDataIfNeeded() {
        tvStatus.text = "Loading database..."
        val total = withContext(Dispatchers.IO) { db.wardDao().totalCount() }
        if (total == 0) {
            tvStatus.text = "Importing Excel..."
            val list = withContext(Dispatchers.IO) {
                ExcelImporter.readFromAssets(this@MainActivity, "databasae.xlsx")
            }
            withContext(Dispatchers.IO) { db.wardDao().insertAll(list) }
        }
        val newTotal = withContext(Dispatchers.IO) { db.wardDao().totalCount() }
        tvStatus.text = "Database Ready ✅ Total Records: $newTotal"
    }

    private fun searchNow(q: String) {
        scope.launch {
            val query = q.trim()
            if (query.isEmpty()) {
                adapter.setData(emptyList())
                tvStatus.text = "Type name or mobile to search"
                return@launch
            }
            val results = withContext(Dispatchers.IO) { db.wardDao().search(query) }
            adapter.setData(results)
            tvStatus.text = "Found: ${results.size}"
        }
    }

    private fun mergeExcel(uri: Uri) {
        scope.launch {
            tvStatus.text = "Merging Excel..."
            val list = withContext(Dispatchers.IO) { ExcelMerger.readFromUri(this@MainActivity, uri) }
            withContext(Dispatchers.IO) { db.wardDao().insertAll(list) }
            val total = withContext(Dispatchers.IO) { db.wardDao().totalCount() }
            tvStatus.text = "Merge completed ✅ Total Records: $total"
            Toast.makeText(this@MainActivity, "Merged: ${list.size} rows", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun exportExcel() {
        val checkedList = withContext(Dispatchers.IO) { db.wardDao().getCheckedPeople() }
        if (checkedList.isEmpty()) {
            Toast.makeText(this, "No checked records yet", Toast.LENGTH_SHORT).show()
            return
        }
        val file = withContext(Dispatchers.IO) { ExcelExporter.exportChecked(checkedList) }
        Toast.makeText(this, "Exported ✅ ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
