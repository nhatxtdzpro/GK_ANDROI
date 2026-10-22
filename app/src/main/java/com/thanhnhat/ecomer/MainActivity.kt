package com.thanhnhat.ecomer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thanhnhat.ecomer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val adapter = ProductAdapter(::edit, ::delete)
    // Change this to the lecturer-approved admin email. Non-admin users can view only.
    private val adminEmails = setOf("admin@ecomer.com")
    private val canManage get() = FirebaseAuth.getInstance().currentUser?.email in adminEmails
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); binding = ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
        setSupportActionBar(binding.toolbar); binding.products.layoutManager = LinearLayoutManager(this); binding.products.adapter = adapter
        binding.addButton.visibility = if (canManage) View.VISIBLE else View.GONE
        binding.addButton.setOnClickListener { startActivity(Intent(this, ProductFormActivity::class.java)) }
        observeProducts()
    }
    private fun observeProducts() { db.collection("products").addSnapshotListener { value, error ->
        if (error != null) { Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show(); return@addSnapshotListener }
        adapter.submit(value?.toObjects(Product::class.java).orEmpty(), canManage)
        binding.emptyText.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    } }
    private fun edit(product: Product) { if (canManage) startActivity(Intent(this, ProductFormActivity::class.java).putExtra(ProductFormActivity.EXTRA_ID, product.id)) }
    private fun delete(product: Product) { if (!canManage) return; AlertDialog.Builder(this).setTitle("Xóa sản phẩm").setMessage("Xóa ${product.name}?").setNegativeButton("Hủy", null).setPositiveButton("Xóa") { _, _ -> db.collection("products").document(product.id).delete().addOnFailureListener { Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show() } }.show() }
    override fun onCreateOptionsMenu(menu: Menu): Boolean { menuInflater.inflate(R.menu.main_menu, menu); return true }
    override fun onOptionsItemSelected(item: MenuItem): Boolean { if (item.itemId == R.id.logout) { FirebaseAuth.getInstance().signOut(); startActivity(Intent(this, LoginActivity::class.java)); finish(); return true }; return super.onOptionsItemSelected(item) }
}
