package com.example.outfitcoordination.View

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.AddClothesViewModel
import com.example.outfitcoordination.databinding.LayoutAddClothesBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddClothesBottomSheet : BottomSheetDialogFragment() {

    private var _binding: LayoutAddClothesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddClothesViewModel by viewModels()
    private var imageUri: Uri? = null

    // Hàm gọi ngược để báo hiệu cho ManageClothes tải lại danh sách sau khi thêm thành công
    var onClothesAdded: (() -> Unit)? = null

    // Bộ mở thư viện ảnh của máy
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            binding.imgPreview.setImageURI(uri)
            binding.layoutAddIcon.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LayoutAddClothesBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bấm chọn ảnh
        binding.cardSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // QUAN SÁT CÁC TRẠNG THÁI TỪ VIEWMODEL
        viewModel.isLoad.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.btnSaveClothes.text = ""
                binding.progressBar.visibility = View.VISIBLE
                binding.cardSelectImage.isEnabled = false
            } else {
                binding.btnSaveClothes.text = "Lưu món đồ"
                binding.progressBar.visibility = View.GONE
                binding.cardSelectImage.isEnabled = true
            }
        }

        viewModel.isSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show()
                onClothesAdded?.invoke() // Gọi hàm làm mới danh sách ngoài màn hình chính
                dismiss() // Tắt bottom sheet
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        // Bấm nút Lưu món đồ
        binding.btnSaveClothes.setOnClickListener {
            val name = binding.edtClothesName.text.toString().trim()
            val type = when (binding.chipGroupAddType.checkedChipId) {
                R.id.chipAddTop -> "ao_trong"
                R.id.chipAddOuter -> "ao_khoac"
                R.id.chipAddPants -> "quan"
                else -> "ao_trong"
            }

            if (name.isEmpty() || imageUri == null) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin và ảnh!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Giao việc lưu dữ liệu cho ViewModel xử lý
            viewModel.addClothes(name, type, imageUri!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}