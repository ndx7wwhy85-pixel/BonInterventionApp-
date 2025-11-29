package com.bonintervention.app

import android.content.Context
import android.graphics.Bitmap
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    fun generatePdf(data: InterventionData): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val fileName = "Bon_Intervention_$timestamp.pdf"
        val pdfFile = File(context.cacheDir, fileName)

        val pdfWriter = PdfWriter(pdfFile)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)
        
        document.setMargins(30f, 30f, 30f, 30f)

        // Titolo
        val title = Paragraph("Bon Intervention")
            .setFontSize(24f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)
        document.add(title)

        // Tabella principale con foto e informazioni
        val mainTable = Table(2)
            .useAllAvailableWidth()
            .setMarginBottom(20f)

        // Colonna sinistra - Foto
        val photoCell = Cell()
            .setPadding(10f)
            .setBorder(SolidBorder(ColorConstants.BLACK, 1f))
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setHorizontalAlignment(HorizontalAlignment.CENTER)

        if (data.photo != null) {
            try {
                val stream = ByteArrayOutputStream()
                data.photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                val imageData = ImageDataFactory.create(byteArray)
                val image = Image(imageData)
                    .setWidth(200f)
                    .setAutoScale(true)
                photoCell.add(image)
            } catch (e: Exception) {
                photoCell.add(Paragraph("Photo non disponible").setFontSize(10f))
            }
        } else {
            photoCell.add(Paragraph("Aucune photo").setFontSize(10f).setItalic())
        }

        mainTable.addCell(photoCell)

        // Colonna destra - Informazioni
        val infoTable = Table(2)
            .useAllAvailableWidth()

        // Helper per aggiungere righe alla tabella info
        fun addInfoRow(label: String, value: String, isLast: Boolean = false) {
            val labelCell = Cell()
                .add(Paragraph(label).setBold().setFontSize(10f))
                .setPadding(5f)
                .setBorder(SolidBorder(ColorConstants.BLACK, 1f))
                .setBackgroundColor(DeviceRgb(230, 230, 230))

            val valueCell = Cell()
                .add(Paragraph(value).setFontSize(10f))
                .setPadding(5f)
                .setBorder(SolidBorder(ColorConstants.BLACK, 1f))

            infoTable.addCell(labelCell)
            infoTable.addCell(valueCell)
        }

        addInfoRow("DATE", data.data)
        addInfoRow("HEURE", data.ora)
        addInfoRow("SERVICE", data.service)
        addInfoRow("ID SMV", data.idSmv)
        addInfoRow("ÉQUIPEMENT", data.equipement, true)

        val infoCell = Cell()
            .add(infoTable)
            .setBorder(SolidBorder(ColorConstants.BLACK, 1f))
            .setPadding(0f)

        mainTable.addCell(infoCell)
        document.add(mainTable)

        // Sezione Descrizione
        val descriptionLabel = Paragraph("Description")
            .setBold()
            .setFontSize(12f)
            .setMarginBottom(5f)
        document.add(descriptionLabel)

        val descriptionBox = Table(1)
            .useAllAvailableWidth()
            .setMarginBottom(20f)

        val descriptionCell = Cell()
            .add(Paragraph(data.description).setFontSize(10f))
            .setPadding(10f)
            .setBorder(SolidBorder(ColorConstants.BLACK, 1f))
            .setMinHeight(100f)

        descriptionBox.addCell(descriptionCell)
        document.add(descriptionBox)

        // Sezione Dichiarante
        val declarantLabel = Paragraph("Déclarant")
            .setBold()
            .setFontSize(12f)
            .setMarginBottom(5f)
        document.add(declarantLabel)

        val declarantBox = Table(1)
            .useAllAvailableWidth()

        val declarantCell = Cell()
            .add(Paragraph(data.declarant).setFontSize(10f))
            .setPadding(10f)
            .setBorder(SolidBorder(ColorConstants.BLACK, 1f))
            .setMinHeight(60f)

        declarantBox.addCell(declarantCell)
        document.add(declarantBox)

        // Footer con data di generazione
        val footer = Paragraph("Document généré le ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())}")
            .setFontSize(8f)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20f)
            .setFontColor(ColorConstants.GRAY)
        document.add(footer)

        document.close()
        return pdfFile
    }
}
