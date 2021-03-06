package com.app.homework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.homework.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//个人信息页面
public class PersonActivity extends AppCompatActivity {
    @BindView(R.id.usercenter_headPhoto)
    ImageView headPhoto;
    @BindView(R.id.usercenter_name)
    TextView PersonUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);

        //接收传值,设置头像和用户名
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        headPhoto.setImageResource(R.drawable.ath);
        PersonUsername.setText(username);
    }

    //退出个人信息页面
    @OnClick(R.id.settings_cha)
    public void onViewClicked() {
        finish();
    }

}
