package com.thanhnhat.ecomer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thanhnhat.ecomer.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale
import android.graphics.BitmapFactory
import android.util.Base64

class ProductAdapter(private val onEdit: (Product) -> Unit, private val onDelete: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.Holder>() {
    private var items = emptyList<Product>(); private var editable = false
    fun submit(newItems: List<Product>, canEdit: Boolean) { items = newItems; editable = canEdit; notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
    inner class Holder(private val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root) { fun bind(p: Product) { b.nameText.text = "Tên SP: ${p.name}"; b.priceText.text = "Giá SP: ${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(p.price)} đ"; b.categoryText.text = "Loại SP: ${p.category}"; b.editButton.isVisible = editable; b.deleteButton.isVisible = editable; b.editButton.setOnClickListener { onEdit(p) }; b.deleteButton.setOnClickListener { onDelete(p) }
        if (p.imageUrl.startsWith("base64,")) { try { val bytes = Base64.decode(p.imageUrl.removePrefix("base64,"), Base64.DEFAULT); b.image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)) } catch (_: Exception) { b.image.setImageResource(android.R.drawable.ic_menu_gallery) } }
        else Glide.with(b.image).load(p.imageUrl).centerCrop().placeholder(android.R.drawable.ic_menu_gallery).into(b.image)
    } }
}
