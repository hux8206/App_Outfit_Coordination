package com.example.outfitcoordination.Repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.outfitcoordination.Model.Clothes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddClothesRepository {
    // Khởi tạo các dịch vụ
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ĐÃ THÊM: Hàm xử lý upload ảnh lên Cloudinary và trả về link
    private suspend fun uploadImageToCloudinary(imageUri: Uri): String? = suspendCoroutine { continuation ->
        MediaManager.get().upload(imageUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    // Lấy đường dẫn URL an toàn khi upload thành công
                    val secureUrl = resultData["secure_url"] as? String
                    continuation.resume(secureUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    continuation.resume(null)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            }).dispatch()
    }

    // ĐÃ SỬA: Thêm .await() và gỡ lỗi Unresolved Reference
    suspend fun addNewClothesByAdmin(
        imageUri: Uri, name: String, type: String, color: String, maleLink: String, femaleLink: String
    ): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return false

            // 1. Chờ Cloudinary upload xong
            val imageUrl = uploadImageToCloudinary(imageUri) ?: return false

            // 2. Gom dữ liệu
            val newClothesId = db.collection("clothes").document().id
            val newClothes = Clothes(
                id = newClothesId,
                name = name,
                type = type,
                color = color,
                image = imageUrl,
                male = maleLink,
                female = femaleLink,
                favourite = false,
                public = true,
                userId = uid
            )

            // 3. Lưu vào Firestore
            db.collection("clothes").document(newClothesId).set(newClothes).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Hàm cũ dùng Firebase Storage (Bạn có thể giữ lại làm code dự phòng hoặc xóa đi cho gọn)
    fun uploadImageAndAddClothes(
        name: String,
        type: String,
        imageUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = storage.reference.child("ClothesImages/$fileName")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val newClothes = Clothes(
                        name = name,
                        type = type,
                        image = downloadUrl.toString()
                    )
                    db.collection("clothes").add(newClothes)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure("Lỗi lưu dữ liệu vào Firestore") }
                }.addOnFailureListener { onFailure("Lỗi lấy đường dẫn ảnh từ Storage") }
            }
            .addOnFailureListener { onFailure("Lỗi tải ảnh lên mạng") }
    }
}