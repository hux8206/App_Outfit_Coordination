package com.example.outfitcoordination.Repository

import android.net.Uri
import com.example.outfitcoordination.Model.Clothes
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddClothesRepository {
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun uploadImageAndAddClothes(
        name: String,
        type: String,
        imageUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // 1. Tạo tên file ảnh ngẫu nhiên để tải lên Firebase Storage
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = storage.reference.child("ClothesImages/$fileName")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                // 2. Lấy link URL ảnh sau khi tải lên Storage thành công
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                    // 3. Tạo Object đúng khuôn Clothes.kt của bạn
                    val newClothes = Clothes(
                        name = name,
                        type = type,
                        image = downloadUrl.toString() // Gán link ảnh vào field 'image'
                    )

                    // 4. Lưu vào Firestore
                    db.collection("clothes").add(newClothes)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure("Lỗi lưu dữ liệu vào Firestore") }
                }.addOnFailureListener { onFailure("Lỗi lấy đường dẫn ảnh từ Storage") }
            }
            .addOnFailureListener { onFailure("Lỗi tải ảnh lên mạng") }
    }
}