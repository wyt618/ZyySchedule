package com.example.zyyschedule.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.databinding.ActivityAddLabelBinding
import com.example.zyyschedule.databinding.ColorpickerDialogBinding
import com.example.zyyschedule.viewmodel.AddLabelViewModel
import com.example.zyyschedule.widget.ColorPickView

class AddLabelActivity : AppCompatActivity(), View.OnClickListener, ColorPickView.OnColorChangedListener {
    private lateinit var binding: ActivityAddLabelBinding
    private lateinit var colorPickerDialogBinding: ColorpickerDialogBinding
    private var labelColor: Int = -0x98641c
    private val vm: AddLabelViewModel by viewModels()
    private lateinit var label: Label

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        colorPickerDialogBinding = ColorpickerDialogBinding.inflate(layoutInflater)
        binding.LabelSetColor.setOnClickListener(this)
        colorPickerDialogBinding.colorPickView.setOnColorChangedListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.LabelSetColor -> gotoColorPicker()
            R.id.add_label_exit -> this.finish()
            R.id.add_label_button -> addLabel()
        }
    }

    private fun gotoColorPicker() {
        if (colorPickerDialogBinding.root.parent != null) {
            val vg: ViewGroup = colorPickerDialogBinding.root.parent as ViewGroup
            vg.removeView(colorPickerDialogBinding.root)
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setView(colorPickerDialogBinding.root)
            setTitle(R.string.add_label_colorpicker)
            setPositiveButton(R.string.dialog_button_ok) { dialog: DialogInterface, _: Int ->
                binding.vLabelSetColor.setBackgroundColor(labelColor)
                dialog.dismiss()
            }
            setNeutralButton(R.string.dialog_button_cancel) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        builder.create().show()
    }

    private fun addLabel() {
        if (binding.LabelTitle.text!!.isNotBlank()) {
            val titleText: String = binding.LabelTitle.text.toString()
            vm.checkLabelTitle(titleText)?.observe(this) { labels ->
                if (labels.isNotEmpty()) {
                    val titleDouble: AlertDialog.Builder = AlertDialog.Builder(this)
                    titleDouble.apply {
                        setMessage(R.string.check_labeltitle_messgae)
                        setPositiveButton(R.string.add_label_dialog_neturl) { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        }
                    }
                    titleDouble.show()
                } else {
                    label = Label()
                    label.apply {
                        color = labelColor
                        title = binding.LabelTitle.text.toString()
                    }
                    vm.insertLabel(label)
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent)
                }
            }
        } else {
            val titleBlankDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            titleBlankDialog.apply {
                setMessage(R.string.add_label_dialog_messgae)
                setPositiveButton(R.string.add_label_dialog_neturl) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
            }
            titleBlankDialog.show()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onColorChange(a: Int, r: Int, g: Int, b: Int) {
        colorPickerDialogBinding.txtColor.apply {
            text = "R:$r\tG:$g\tB:$b\t" + colorPickerDialogBinding.colorPickView.colorStr
            setTextColor(Color.argb(a, r, g, b))
        }
        labelColor = Color.argb(a, r, g, b)
        binding.addLabelExit.setOnClickListener(this)
        binding.addLabelButton.setOnClickListener(this)
    }

}