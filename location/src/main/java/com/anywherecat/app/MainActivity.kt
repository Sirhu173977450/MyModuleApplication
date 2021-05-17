package com.anywherecat.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    var latitude: String? = "0"

    var longitude: String? = "0"

    var mLocationClient: LocationClient? = null

    val PERMISSIONS_REQUEST_CODE = 1


    private lateinit var latLng: LatLng

    private lateinit var myListener: BDAbstractLocationListener

    private lateinit var mSearch: GeoCoder

    private var address: String = ""

    private lateinit var infoWindowView: View

    private lateinit var bitmapDescriptor: BitmapDescriptor

    private lateinit var markerOptions: MarkerOptions

    private var marker: Marker? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //39.907  116.391
        initMap()

        bt_localtion!!.setOnClickListener(View.OnClickListener {
            // On some version do like this
            if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                askRunTimePermissions()
                return@OnClickListener
            }

            if (!GpsUtil.isMockSettingsON(this)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("为了使用这个应用程序你必须启用模拟位置你想现在启用它吗?").setTitle("模拟位置未启用")
                builder.setPositiveButton("yes") { dialog, id ->
                    val i = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    startActivity(i)
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, id -> dialog.dismiss() }
                val dialog = builder.create()
                dialog.show()
                return@OnClickListener
            }

            latitude = et_latitude!!.text.toString().trim { it <= ' ' }
            longitude = et_longitude!!.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(longitude)) {
                Toast.makeText(this@MainActivity, "经度不能为空！", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(latitude)) {
                Toast.makeText(this@MainActivity, "维度不能为空！", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
//            //百度地图移动到指定经纬度位置
//            chooseMyLocation(latitude!!.toDouble(), longitude!!.toDouble())
            //同时更新本地经纬度
//            setLocation(latitude!!.toDouble(),longitude!!.toDouble())

            startService(Intent(this, LocationService::class.java)
                    .putExtra("latitude", latitude!!.toDouble())
                    .putExtra("longitude", longitude!!.toDouble())
            )
        })

        tv_gps?.setOnClickListener {
            //无法定位：1、提示用户打开定位服务；2、跳转到设置界面
            Toast.makeText(this@MainActivity, "无法定位，请打开定位服务",
                    Toast.LENGTH_SHORT).show()
            val i = Intent()
            i.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            startActivity(i)
        }

        tv_system_mock_position_status?.setOnClickListener {
            Toast.makeText(this@MainActivity, "无法定位，请打开模拟定位选择当前应用",
                    Toast.LENGTH_SHORT).show()
            GpsUtil.startDevelopmentActivity(this@MainActivity)
        }

        tv_gps!!.text = if (GpsUtil.isOPen(this)) "已开启" else "未开启"

        tv_system_mock_position_status!!.text = if (GpsUtil.isMockSettingsON(this)) "已开启" else "未开启"

        tv_use_rule.setOnClickListener {
            startActivity(Intent(this@MainActivity,RuleActivity::class.java))
        }

    }

    private fun initMap() {
        initInfoView()
        //1.初始化百度Map地图配置
        mLocationClient = LocationClient(applicationContext) // 声明LocationClient类
        myListener = MyLocationListener()
        mLocationClient!!.registerLocationListener(myListener) // 注册监听函数
        mLocationClient!!.locOption = initLocOption()
        mSearch = GeoCoder.newInstance()
        mSearch.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener {
            override fun onGetGeoCodeResult(geoCodeResult: GeoCodeResult) {

            }

            override fun onGetReverseGeoCodeResult(reverseGeoCodeResult: ReverseGeoCodeResult?) {
                if (reverseGeoCodeResult != null && reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    address = reverseGeoCodeResult.address
//                    if (::addrTv.isInitialized) {
                    tv_address.text = address
//                    }
                    initInfoWindow()
                }
            }
        })
        mLocationClient!!.start()// 开始定位

        mapView.map.mapType = BaiduMap.MAP_TYPE_NORMAL//普通地图
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_mark)
        mapView.map.setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(mapStatus: MapStatus) {

            }

            override fun onMapStatusChangeStart(mapStatus: MapStatus, i: Int) {

            }

            override fun onMapStatusChange(mapStatus: MapStatus) {
                if (!::markerOptions.isInitialized) {
                    markerOptions = MarkerOptions().position(mapStatus.target).icon(bitmapDescriptor).zIndex(9)
                    marker = mapView.map.addOverlay(markerOptions) as Marker
                }
                marker!!.position = mapStatus.target
                latLng = mapStatus.target
                initInfoWindow()
            }

            override fun onMapStatusChangeFinish(mapStatus: MapStatus) {
                latLng = mapStatus.target
                mSearch.reverseGeoCode(ReverseGeoCodeOption().location(latLng))
            }
        })
    }

    private inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            if (location == null) {
                Toast.makeText(this@MainActivity, "地址不可用", Toast.LENGTH_SHORT).show()
                return
            }
            Log.e("TAG", location.longitude.toString() + "??" + location.latitude.toString())

            Log.e("TAG_ addrStr :", location.addrStr.toString())

            //地址
            tv_address.text = location.addrStr.toString()


            latLng = if (location.latitude.toString() == "4.9E-324") {
                LatLng(40.058973, 116.312713)
            } else {
                LatLng(location.latitude, location.longitude)
            }
            mapView.map.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
            mapView.map.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f))
//            mSearch.reverseGeoCode(ReverseGeoCodeOption().location(latLng))
        }
    }

    private fun initLocOption(): LocationClientOption {
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//默认高精度定位模式
        option.setCoorType("bd09ll")//默认gcj02，设置返回的定位结果坐标系
        // option.setScanSpan(1000);//默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms
        option.setIsNeedAddress(true)//设置是否需要地址信息，默认不需要
        option.isOpenGps = true//默认false,设置是否使用gps
        option.isLocationNotify = true//默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true)//默认false，设置是否需要位置语义化结果
        option.setIsNeedLocationPoiList(true)//默认false，设置是否需要POI结果
        option.setIgnoreKillProcess(false)//默认true不杀死进程，设置是否在stop的时候杀死进程
        option.SetIgnoreCacheException(false)//默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false)//默认false，设置是否需要过滤GPS仿真结果，默认需要
        return option
    }

    private fun initInfoWindow() {
        //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，也可以设置一个 BitmapDescriptor 作为该窗口的内容
        val infoWindow = InfoWindow(infoWindowView, latLng, -80)
        //InfoWindow infoWindow = new InfoWindow(button, latLng, -47);
        //显示信息窗口

        mapView.map.showInfoWindow(infoWindow)
    }

    private fun initInfoView() {
        infoWindowView = LayoutInflater.from(this).inflate(R.layout.view_infowindow, null, false)
//        addrTv = infoWindowView.findViewById(R.id.tv_addr)
        infoWindowView.findViewById<View>(R.id.btn_collect).setOnClickListener {
//            addCollect()
        }
        infoWindowView.findViewById<View>(R.id.btn_through).setOnClickListener {
            //  mapView.map.hideInfoWindow();
//            if (canMockPosition() && !isGPSEnable()) {

            // On some version do like this
            if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                askRunTimePermissions()
                return@setOnClickListener
            }

            if (!GpsUtil.isMockSettingsON(this)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("为了使用这个应用程序你必须启用模拟位置你想现在启用它吗?").setTitle("模拟位置未启用")
                builder.setPositiveButton("yes") { dialog, id ->
                    val i = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    startActivity(i)
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, id -> dialog.dismiss() }
                val dialog = builder.create()
                dialog.show()
                return@setOnClickListener
            }


            startService(Intent(this, LocationService::class.java)
//                        .putExtra("latLng", latLng)
                    .putExtra("latitude", latLng.latitude)
                    .putExtra("longitude", latLng.longitude)


            )
            AlertDialog.Builder(this).setTitle("穿越成功")
                    .setMessage("你成功穿越到了 $address\n\n提醒:可能需要打开位置信息,微信需6.3以下版本(不包含6.3),纷享销客5.20以下(不包含5.20),今目标，微博等均支持。")
                    .setPositiveButton("确定", null)
                    .create().show()
//            } else {
//                settingDialog = SettingDialog.Builder(this).setMock(canMockPosition()).setLocation(!isGPSEnable()).create()
//                settingDialog.show()
//            }
        }
    }

    private fun askRunTimePermissions() {
        ActivityCompat.requestPermissions(this
                , arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Got permission location ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No location permissions", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时必须调用mMapView. onResume ()
//        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时必须调用mMapView. onPause ()
//        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时必须调用mMapView.onDestroy()
//        mapView?.onDestroy()
    }
}