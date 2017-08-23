package com.wenjing.mymapdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class WalkRouteCalculateActivity extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener {

    private double longitude;
    private double latitude;
    private double nameLatitude;
    private double nameLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic_navi);

        String name = getIntent().getStringExtra("name");
        Log.e("-----------", name);


        //                        AppContext.showToast("有权限");
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);

        getLocation();

        //搜索
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
        GeocodeQuery query = new GeocodeQuery(name, "");
        String city = query.getCity();
        Log.e("-----------城市--", city);
        query.setCity(city);
        geocodeSearch.getFromLocationNameAsyn(query);

    }

    private void getLocation() {

        positionText = (TextView) findViewById(R.id.position_text);
        tipInfo = (TextView) findViewById(R.id.tipInfo);
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceName); // 查找到服务信息
        // 获得LocationManager的实例
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //优先使用gps
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 没有可用的位置提供器
            Toast.makeText(WalkRouteCalculateActivity.this, "没有位置提供器可供使用", Toast.LENGTH_LONG)
                    .show();
            return;
        }

    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        mAMapNavi.startNavi(NaviType.GPS);
    }

    private TextView positionText;// 存放经纬度的文本
    private TextView tipInfo;// 提示信息

    private LocationManager locationManager;// 位置管理类


    private String provider;// 位置提供器

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    protected void onDestroy() {
        super.onDestroy();
        finish();
        if (locationManager != null) {
            // 关闭程序时将监听器移除
            locationManager.removeUpdates(locationListener);
        }

    }


    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onLocationChanged(Location location) {
            // 设备位置发生改变时，执行这里的代码
            String changeInfo = "隔10秒刷新的提示：\n 时间：" + sdf.format(new Date())
                    + ",\n当前的经度是：" + location.getLongitude() + ",\n 当前的纬度是："
                    + location.getLatitude();
            showLocation(location, changeInfo, WalkRouteCalculateActivity.this.latitude, WalkRouteCalculateActivity.this.longitude);

        }
    };

    /**
     * 显示当前设备的位置信息
     *
     * @param location
     */
    private void showLocation(Location location, String changeInfo, double start, double end) {

        // TODO Auto-generated method stub
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Log.e("-----------", latitude + "---" + longitude + "终点" + start + "---" + end);
        String currentLocation = "当前的经度是：" + longitude + ",\n"
                + "当前的纬度是：" + latitude;
        positionText.setText(currentLocation);
        tipInfo.setText(changeInfo);

        mAMapNavi.calculateWalkRoute(new NaviLatLng(latitude, longitude), new NaviLatLng(start, end));


    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
        for (GeocodeAddress geocodeAddress : geocodeAddressList) {
            LatLonPoint latLonPoint = geocodeAddress.getLatLonPoint();
            nameLatitude = latLonPoint.getLatitude();
            nameLongitude = latLonPoint.getLongitude();
            Log.e("-----------", nameLatitude + "===" + nameLongitude);
            getData(nameLatitude, nameLongitude);
        }

    }

    private void getData(double latitude, double longitude) {
        //        Location location = locationManager.getLastKnownLocation(provider);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        String locationProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);

        if (location != null) {
            // 显示当前设备的位置信息
            String firstInfo = "第一次请求的信息";
            showLocation(location, firstInfo, latitude, longitude);
        } else {
            String info = "无法获得当前位置";
            Toast.makeText(WalkRouteCalculateActivity.this, info, Toast.LENGTH_SHORT).show();
            positionText.setText(info);
        }


        // 更新当前位置
        locationManager.requestLocationUpdates(provider, 10 * 1000, 1, locationListener);
    }

}
