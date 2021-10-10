package com.lifengqiang.biquge.ui.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.book.ReadAnimationSetting;

public class SettingActivity extends AppCompatActivity {
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_setting);
        RadioGroup group = findViewById(R.id.group);
        switch (ReadAnimationSetting.get(this)) {
            case ReadAnimationSetting.NONE:
                ((RadioButton) findViewById(R.id.none)).setChecked(true);
                break;
            case ReadAnimationSetting.HORIZONTAL:
                ((RadioButton) findViewById(R.id.horizontal)).setChecked(true);
                break;
//            case ReadAnimationSetting.VERTICAL:
//                ((RadioButton) findViewById(R.id.vertical)).setChecked(true);
//                break;
        }
        group.setOnCheckedChangeListener((g, checkedId) -> {
            switch (checkedId) {
                case R.id.none:
                    ReadAnimationSetting.set(this, ReadAnimationSetting.NONE);
                    break;
                case R.id.horizontal:
                    ReadAnimationSetting.set(this, ReadAnimationSetting.HORIZONTAL);
                    break;
//                case R.id.vertical:
//                    ReadAnimationSetting.set(this, ReadAnimationSetting.VERTICAL);
//                    break;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
