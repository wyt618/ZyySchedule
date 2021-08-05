package com.example.zyyschedule.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.databinding.ActivityAddLabelBinding;
import com.example.zyyschedule.databinding.ColorpickerDialogBinding;
import com.example.zyyschedule.viewmodel.AddLabelViewModel;

public class AddLabelActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddLabelBinding binding;
    private ColorpickerDialogBinding colorpickerDialogBinding;
    private int labelcolor = 0xff679BE4;
    private AddLabelViewModel vm;
    private Label label;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        colorpickerDialogBinding = ColorpickerDialogBinding.inflate(getLayoutInflater());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_label);
        binding.LabelSetColor.setOnClickListener(this);
        vm = new ViewModelProvider(this).get(AddLabelViewModel.class);
        colorpickerDialogBinding.colorPickView.setOnColorChangedListener((a, r, g, b) -> {
            colorpickerDialogBinding.txtColor.setText("R:" + r + "\tG:" + g + "\tB:" + b + "\t" + colorpickerDialogBinding.colorPickView.getColorStr());
            colorpickerDialogBinding.txtColor.setTextColor(Color.argb(a, r, g, b));
            labelcolor = Color.argb(a, r, g, b);
        });
        binding.addLabelExit.setOnClickListener(this);
        binding.addLabelButton.setOnClickListener(this);


    }

    private void gotoColorpicker() {
        if (colorpickerDialogBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) colorpickerDialogBinding.getRoot().getParent();
            vg.removeView(colorpickerDialogBinding.getRoot());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(colorpickerDialogBinding.getRoot())
                .setTitle(R.string.add_label_colorpicker)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    binding.vLabelSetColor.setBackgroundColor(labelcolor);
                    dialog.dismiss();
                })
                .setNeutralButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LabelSetColor:
                gotoColorpicker();
                break;
            case R.id.add_label_exit:
                AddLabelActivity.this.finish();
                break;
            case R.id.add_label_button:
                AddLabel();
                break;
        }
    }

    private void AddLabel() {
        if (binding.LabelTitle.getText().length() > 0) {
            String title = binding.LabelTitle.getText().toString();
            vm.checkLabelTitle(title).observe(this, labels -> {
                if (labels.size() != 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddLabelActivity.this);
                    dialog.setMessage(R.string.check_labeltitle_messgae);
                    dialog.setPositiveButton(R.string.add_label_dialog_neturl, (dialog1, which) -> dialog1.dismiss());
                    dialog.show();
                } else {
                    label = new Label();
                    label.setColor(labelcolor);
                    label.setTitle(binding.LabelTitle.getText().toString());
                    vm.insertLabel(label);
                    AddLabelActivity.this.finish();
                }
            });
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddLabelActivity.this);
            dialog.setMessage(R.string.add_label_dialog_messgae);
            dialog.setPositiveButton(R.string.add_label_dialog_neturl, (dialog12, which) -> dialog12.dismiss());
            dialog.show();
        }
    }
}