package com.thanhnhat.ecomer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.thanhnhat.ecomer.databinding.ActivityProductFormBinding
import java.io.ByteArrayOutputStream
import android.util.Base64

class ProductFormActivity : AppCompatActivity() {
    companion object { const val EXTRA_ID = "product_id" }
    private lateinit var binding: ActivityProductFormBinding
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var imageUri: Uri? = null; private var oldImageUrl = ""; private val productId by lazy { intent.getStringExtra(EXTRA_ID).orEmpty() }
    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { imageUri = it; binding.image.setImageURI(it) } }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); binding = ActivityProductFormBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }; binding.chooseImageButton.setOnClickListener { imagePicker.launch("image/*") }; binding.saveButton.setOnClickListener { save() }
        if (productId.isNotEmpty()) loadProduct() else binding.toolbar.title = "Thêm sản phẩm"
    }
    private fun loadProduct() { binding.toolbar.title = "Sửa sản phẩm"; db.collection("products").document(productId).get().addOnSuccessListener { doc -> doc.toObject(Product::class.java)?.let { p -> binding.nameEdit.setText(p.name); binding.priceEdit.setText(p.price.toString()); binding.categoryEdit.setText(p.category); oldImageUrl = p.imageUrl; showImage(oldImageUrl) } } }
    private fun showImage(value: String) {
        if (value.startsWith("base64,")) try {
            val bytes = Base64.decode(value.removePrefix("base64,"), Base64.DEFAULT)
            binding.image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
        } catch (_: Exception) { binding.image.setImageResource(android.R.drawable.ic_menu_gallery) }
        else Glide.with(this).load(value).into(binding.image)
    }
    private fun save() { val name = binding.nameEdit.text.toString().trim(); val category = binding.categoryEdit.text.toString().trim(); val price = binding.priceEdit.text.toString().toLongOrNull()
        if (name.isBlank() || category.isBlank() || price == null || price < 0) { toast("Nhập đầy đủ tên, giá và loại sản phẩm"); return }; setLoading(true)
        // Spark plan: store a small, compressed Base64 image in Firestore, no Cloud Storage required.
        val imageData = imageUri?.let(::compressImage) ?: oldImageUrl
        if (imageUri != null && imageData == null) { setLoading(false); toast("Không thể đọc hình ảnh"); return }
        writeProduct(name, price, category, imageData)
    }
    /** Keeps the Firestore document below its 1 MiB maximum. */
    private fun compressImage(uri: Uri): String? {
        return try {
            val original = contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream) ?: return null
            val longest = maxOf(original.width, original.height)
            val scaled = if (longest > 600) {
                val ratio = 600f / longest
                Bitmap.createScaledBitmap(original, (original.width * ratio).toInt(), (original.height * ratio).toInt(), true)
            } else original
            ByteArrayOutputStream().use { output ->
                scaled.compress(Bitmap.CompressFormat.JPEG, 65, output)
                "base64," + Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
            }
        } catch (_: Exception) { null }
    }
    private fun writeProduct(name: String, price: Long, category: String, imageUrl: String) { val product = Product(name = name, price = price, category = category, imageUrl = imageUrl); val task = if (productId.isEmpty()) db.collection("products").add(product) else db.collection("products").document(productId).set(product); task.addOnSuccessListener { toast("Đã lưu sản phẩm"); finish() }.addOnFailureListener { setLoading(false); toast(it.localizedMessage ?: "Không thể lưu") } }
    private fun setLoading(value: Boolean) { binding.progress.visibility = if (value) View.VISIBLE else View.GONE; binding.saveButton.isEnabled = !value }
    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
