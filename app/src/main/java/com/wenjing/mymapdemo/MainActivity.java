package com.wenjing.mymapdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.wenjing.mymapdemo.util.AmapTTSController;
import com.wenjing.mymapdemo.util.CheckPermissionsActivity;

public class MainActivity extends CheckPermissionsActivity implements View.OnClickListener, INaviInfoCallback {

    Button button, button1;
    AmapTTSController amapTTSController;
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button1 = (Button) findViewById(R.id.button1);
        etName= (EditText) findViewById(R.id.et_put);

        button.setOnClickListener(this);
        button1.setOnClickListener(this);

        // SpeechUtils.getInstance(this).speakText();系统自带的语音播报
        amapTTSController = AmapTTSController.getInstance(getApplicationContext());
        amapTTSController.init();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(null), MainActivity.this);
        }
        if (view.getId() == R.id.button1) {
            String name = etName.getText().toString().trim();
            Intent intent=new Intent(this,WalkRouteCalculateActivity.class);
            intent.putExtra("name",name);
            startActivity(intent);
        }
    }

    /**
     * 返回键处理事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            System.exit(0);// 退出程序
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {
        amapTTSController.onGetNavigationText(s);
    }

    @Override
    public void onStopSpeaking() {
        amapTTSController.stopSpeaking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        amapTTSController.destroy();
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }


}
