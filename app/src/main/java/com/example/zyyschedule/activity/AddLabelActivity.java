package com.example.zyyschedule.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.databinding.ActivityAddLabelBinding;
import com.example.zyyschedule.databinding.ColorpickerDialogBinding;
import com.example.zyyschedule.viewmodel.AddLabelViewModel;
import com.example.zyyschedule.widget.ColorPickView;

import java.util.List;

public class AddLabelActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddLabelBinding binding;
    private ColorpickerDialogBinding colorpickerDialogBinding;
    private AlertDialog.Builder builder;
    private int labelcolor = 0xffffffff ;
    private AddLabelViewModel vm;
    private Label label;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        colorpickerDialogBinding = ColorpickerDialogBinding.inflate(getLayoutInflater());
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_label);
        binding.LabelSetColor.setOnClickListener(this);
        vm = new ViewModelProvider(this).get(AddLabelViewModel.class);
        colorpickerDialogBinding.colorPickView.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {
            @Override
            public void onColorChange(int a, int r, int g, int b) {
                colorpickerDialogBinding.txtColor.setText("R:" + r + "\tG:" + g + "\tB:" + b + "\t" + colorpickerDialogBinding.colorPickView.getColorStr());
                colorpickerDialogBinding.txtColor.setTextColor(Color.argb(a, r, g, b));
                labelcolor = Color.argb(a, r, g, b);
            }
        });
        binding.addLabelExit.setOnClickListener(this);
        binding.addLabelButton.setOnClickListener(this);



    }

    private void gotoColorpicker() {
        if (colorpickerDialogBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) colorpickerDialogBinding.getRoot().getParent();
            vg.removeView(colorpickerDialogBinding.getRoot());
        }
        builder = new AlertDialog.Builder(this);
        builder.setView(colorpickerDialogBinding.getRoot())
                .setTitle(R.string.add_label_colorpicker)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.vLabelSetColor.setBackgroundColor(labelcolor);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
    private void AddLabel(){
        if(binding.LabelTitle.getText().length()>0){
            String title =  binding.LabelTitle.getText().toString();
            vm.CheckLabelTitle(title).observe(this, new Observer<List<Label>>() {
                @Override
                public void onChanged(List<Label> labels) {
                    if(labels.size()!=0){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AddLabelActivity.this);
                        dialog.setMessage(R.string.check_labeltitle_messgae);
                        dialog.setPositiveButton(R.string.add_label_dialog_neturl, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }else{
                        label = new Label();
                        label.setColor(labelcolor);
                        label.setTitle(binding.LabelTitle.getText().toString());
                        vm.insertLabel(label);
                        AddLabelActivity.this.finish();
                    }
                }
            });
        }else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddLabelActivity.this);
            dialog.setMessage(R.string.add_label_dialog_messgae);
            dialog.setPositiveButton(R.string.add_label_dialog_neturl, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}