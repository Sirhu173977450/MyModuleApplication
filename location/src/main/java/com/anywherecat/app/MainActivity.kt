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
                builder.setMessage("??????????????????????????????????????????????????????????????????????????????????").setTitle("?????????????????????")
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
                Toast.makeText(this@MainActivity, "?????????????????????", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(latitude)) {
                Toast.makeText(this@MainActivity, "?????????????????????", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
//            //??????????????????????????????????????????
//            chooseMyLocation(latitude!!.toDouble(), longitude!!.toDouble())
            //???????????????????????????
//            setLocation(latitude!!.toDouble(),longitude!!.toDouble())

            startService(Intent(this, LocationService::class.java)
                    .putExtra("latitude", latitude!!.toDouble())
                    .putExtra("longitude", longitude!!.toDouble())
            )
        })

        tv_gps?.setOnClickListener {
            //???????????????1????????????????????????????????????2????????????????????????
            Toast.makeText(this@MainActivity, "????????????????????????????????????",
                    Toast.LENGTH_SHORT).show()
            val i = Intent()
            i.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            startActivity(i)
        }

        tv_system_mock_position_status?.setOnClickListener {
            Toast.makeText(this@MainActivity, "??????????????????????????????????????????????????????",
                    Toast.LENGTH_SHORT).show()
            GpsUtil.startDevelopmentActivity(this@MainActivity)
        }

        tv_gps!!.text = if (GpsUtil.isOPen(this)) "?????????" else "?????????"

        tv_system_mock_position_status!!.text = if (GpsUtil.isMockSettingsON(this)) "?????????" else "?????????"

        tv_use_rule.setOnClickListener {
            startActivity(Intent(this@MainActivity,RuleActivity::class.java))
        }

    }

    private fun initMap() {
        initInfoView()
        //1.???????????????Map????????????
        mLocationClient = LocationClient(applicationContext) // ??????LocationClient???
        myListener = MyLocationListener()
        mLocationClient!!.registerLocationListener(myListener) // ??????????????????
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
        mLocationClient!!.start()// ????????????

        mapView.map.mapType = BaiduMap.MAP_TYPE_NORMAL//????????????
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
                Toast.makeText(this@MainActivity, "???????????????", Toast.LENGTH_SHORT).show()
                return
            }
            Log.e("TAG", location.longitude.toString() + "??" + location.latitude.toString())

            Log.e("TAG_ addrStr :", location.addrStr.toString())

            //??????
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
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//???????????????????????????
        option.setCoorType("bd09ll")//??????gcj02???????????????????????????????????????
        // option.setScanSpan(1000);//??????0???????????????????????????????????????????????????????????????????????????1000ms
        option.setIsNeedAddress(true)//????????????????????????????????????????????????
        option.isOpenGps = true//??????false,??????????????????gps
        option.isLocationNotify = true//??????false??????????????????GPS???????????????1S/1???????????????GPS??????
        option.setIsNeedLocationDescribe(true)//??????false??????????????????????????????????????????
        option.setIsNeedLocationPoiList(true)//??????false?????????????????????POI??????
        option.setIgnoreKillProcess(false)//??????true?????????????????????????????????stop?????????????????????
        option.SetIgnoreCacheException(false)//??????false?????????????????????CRASH?????????????????????
        option.setEnableSimulateGps(false)//??????false???????????????????????????GPS???????????????????????????
        return option
    }

    private fun initInfoWindow() {
        //?????????????????????????????????????????????????????????View???????????????????????????????????????????????? BitmapDescriptor ????????????????????????
        val infoWindow = InfoWindow(infoWindowView, latLng, -80)
        //InfoWindow infoWindow = new InfoWindow(button, latLng, -47);
        //??????????????????

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
                builder.setMessage("??????????????????????????????????????????????????????????????????????????????????").setTitle("?????????????????????")
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
            AlertDialog.Builder(this).setTitle("????????????")
                    .setMessage("????????????????????? $address\n\n??????:??????????????????????????????,?????????6.3????????????(?????????6.3),????????????5.20??????(?????????5.20),?????????????????????????????????")
                    .setPositiveButton("??????", null)
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
        //???activity??????onResume???????????????mMapView. onResume ()
//        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        //???activity??????onPause???????????????mMapView. onPause ()
//        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //???activity??????onDestroy???????????????mMapView.onDestroy()
//        mapView?.onDestroy()
    }
}