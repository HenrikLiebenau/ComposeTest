package com.lutz.composetutorial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.font.*
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDCheckBox
import java.io.File


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FormGUI()
        }
    }


    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun FormGUI(){
        var text1 by rememberSaveable { mutableStateOf("") }
        var checkbox = remember { mutableStateOf(false) }
        var text2 by rememberSaveable { mutableStateOf("") }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                TextField(
                    value = text1,
                    onValueChange = {
                        text1 = it
                    },
                    label = { Text("Text") }, modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                Checkbox(checked = checkbox.value, onCheckedChange ={
                    checkbox.value = it
                } )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                TextField(
                    value = text2,
                    onValueChange = {
                        text2 = it
                    },
                    label = { Text("Zahl") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                var test = Button(
                    onClick = {
                        generatePDF(name = text1, bool = checkbox.toString(), number = text2)
                        text1 = ""
                        checkbox = mutableStateOf(false)
                        text2 = ""
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                )

                {
                    Text(text = "Enddatei generieren", color = Color.White)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                var test = Button(
                    onClick = {
                        openForm()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                )

                {
                    Text(text = "Formular öffnen", color = Color.White)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                var test = Button(
                    onClick = {
                        openFinal()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                )

                {
                    Text(text = "Enddatei öffnen", color = Color.White)
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                var test = Button(
                    onClick = {
                        deleteFinal()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                )

                {
                    Text(text = "Enddatei löschen", color = Color.White)
                }
            }

        }
    }



    fun generatePDF(name: String, bool: String, number: String){

        PDFBoxResourceLoader.init(applicationContext)

        val file = File("/data/data/com.lutz.composetutorial/files/test_form.pdf")
        val pdf_file = PDDocument.load(file)
        val pdfFont = PDType1Font.TIMES_ROMAN



        val acroform = pdf_file.documentCatalog.acroForm
        val List = acroform.fields
        var count = 0;
        for (i in List){
            if (count == 0){
                i.setValue(name)
            }
            else if (count == 1){

                if (bool.contains("true")){
                    acroform.getField("boolean").setValue("Yes")
                }
                else {
                    Toast.makeText(this, bool, Toast.LENGTH_SHORT).show()

                    acroform.getField("boolean").setValue("Off")
                }
            }
            else if (count == 2){
                i.setValue(number.toString())
            }
            count ++;
        }
        acroform.flatten()
        pdf_file.save(File("/data/data/com.lutz.composetutorial/files/test_form_test.pdf"))
        Toast.makeText(this, "Datei generiert", Toast.LENGTH_SHORT).show()
    }

    fun openForm() {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val file = File("/data/data/com.lutz.composetutorial/files/test_form.pdf")
        val uri = FileProvider.getUriForFile(this, "com.lutz.composetutorial.fileprovider", file)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    fun openFinal(){
        val file = File("/data/data/com.lutz.composetutorial/files/test_form_test.pdf")
        if (file.exists()){
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val file = File("/data/data/com.lutz.composetutorial/files/test_form_test.pdf")
            val uri = FileProvider.getUriForFile(this, "com.lutz.composetutorial.fileprovider", file)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
        else {
            Toast.makeText(this, "Keine Datei vorhanden", Toast.LENGTH_SHORT ).show()
        }
    }

    fun deleteFinal() {
        val file = File("/data/data/com.lutz.composetutorial/files/test_form_test.pdf")
        val success = file.delete()
        if (success){
            Toast.makeText(this, "Datei gelöscht", Toast.LENGTH_SHORT ).show()
        }
        else {
            Toast.makeText(this, "Datei konnte nicht gelöscht werden", Toast.LENGTH_SHORT ).show()
        }
    }
}
