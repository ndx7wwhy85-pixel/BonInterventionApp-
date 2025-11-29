package com.bonintervention.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bonintervention.app.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var photoUri: Uri? = null
    private var photoBitmap: Bitmap? = null
    private lateinit var pdfGenerator: PdfGenerator

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    photoBitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imagePreview.setImageBitmap(photoBitmap)
                    binding.imagePreview.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, getString(R.string.photo_captured_success), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.photo_load_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = contentResolver.openInputStream(it)
                photoBitmap = BitmapFactory.decodeStream(inputStream)
                binding.imagePreview.setImageBitmap(photoBitmap)
                binding.imagePreview.visibility = android.view.View.VISIBLE
                photoUri = it
                Toast.makeText(this, getString(R.string.photo_loaded_success), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.photo_load_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfGenerator = PdfGenerator(this)

        setupViews()
        setCurrentDateTime()
    }

    private fun setupViews() {
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        binding.btnSelectPhoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnGeneratePdf.setOnClickListener {
            if (validateFields()) {
                generatePdf()
            }
        }
    }

    private fun setCurrentDateTime() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
        
        binding.editData.setText(dateFormat.format(calendar.time))
        binding.editOra.setText(timeFormat.format(calendar.time))
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val photoFile = File.createTempFile(
            "IMG_${System.currentTimeMillis()}_",
            ".jpg",
            cacheDir
        )
        
        photoUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        
        cameraLauncher.launch(photoUri)
    }

    private fun validateFields(): Boolean {
        if (binding.editData.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_date), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.editOra.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_time), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.editService.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_service), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.editDescription.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_description), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.editDeclarant.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.enter_declarant), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun generatePdf() {
        binding.btnGeneratePdf.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        val interventionData = InterventionData(
            data = binding.editData.text.toString(),
            ora = binding.editOra.text.toString(),
            service = binding.editService.text.toString(),
            idSmv = binding.editIdSmv.text.toString(),
            equipement = binding.editEquipement.text.toString(),
            description = binding.editDescription.text.toString(),
            declarant = binding.editDeclarant.text.toString(),
            photo = photoBitmap
        )

        lifecycleScope.launch {
            try {
                val pdfFile = withContext(Dispatchers.IO) {
                    pdfGenerator.generatePdf(interventionData)
                }
                
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnGeneratePdf.isEnabled = true
                    
                    showShareDialog(pdfFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnGeneratePdf.isEnabled = true
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.pdf_generation_error, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showShareDialog(pdfFile: File) {
        val pdfUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            pdfFile
        )

        // Condividi direttamente con WhatsApp se disponibile
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            `package` = "com.whatsapp"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_TEXT, getString(R.string.bon_intervention_text))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Verifica se WhatsApp è installato
        val whatsappAvailable = packageManager.resolveActivity(
            whatsappIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null

        if (whatsappAvailable) {
            // Mostra dialog per scegliere
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.share_pdf_title))
            builder.setMessage(getString(R.string.pdf_generated_success))
            builder.setPositiveButton(getString(R.string.whatsapp_button)) { _, _ ->
                startActivity(whatsappIntent)
            }
            builder.setNeutralButton(getString(R.string.other_button)) { _, _ ->
                shareWithOtherApps(pdfUri)
            }
            builder.setNegativeButton(getString(R.string.cancel_button), null)
            builder.show()
        } else {
            // Se WhatsApp non è disponibile, usa il selettore standard
            shareWithOtherApps(pdfUri)
        }
    }

    private fun shareWithOtherApps(pdfUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_TEXT, getString(R.string.bon_intervention_text))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)))
    }
}
