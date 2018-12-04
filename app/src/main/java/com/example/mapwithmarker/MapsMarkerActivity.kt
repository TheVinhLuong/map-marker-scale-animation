package com.example.mapwithmarker

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsMarkerActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    var markerOriginalWidth: Int = 0
    var markerOriginalHeight: Int = 0
    val scaleUpFactor = 2F
    lateinit var originalLoc: LatLng

    private lateinit var mMap: GoogleMap

    var animation: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener(this)
        val sydney = LatLng(-34.0, 151.0)
        val mapIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_map_marker)
        markerOriginalHeight = mapIcon.height
        markerOriginalWidth = mapIcon.width
        val bitmap = Bitmap.createScaledBitmap(mapIcon, markerOriginalWidth, markerOriginalHeight, false)
        val mapIconBitMapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
        val marker = mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").icon(mapIconBitMapDescriptor).flat(true))
        marker.tag = bitmap
        originalLoc = marker.position
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val marker = p0!!
        val mapIcon = marker.tag as Bitmap
        val animation = ValueAnimator.ofFloat(1F, scaleUpFactor)
        animation.duration = 500
        animation.interpolator = LinearInterpolator()
        animation.addUpdateListener { animationState ->
            val scaleFactor = animationState.animatedValue as Float
            val point = mMap.projection.toScreenLocation(originalLoc)
            point.y += ((markerOriginalHeight * scaleFactor - markerOriginalHeight) / 2).toInt()
            marker.position = mMap.projection.fromScreenLocation(point)
            val newBitMap = Bitmap.createScaledBitmap(
                    mapIcon,
                    (markerOriginalWidth * scaleFactor).toInt(),
                    (markerOriginalHeight * scaleFactor).toInt(),
                    false
            )
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(newBitMap))
        }
        animation.start()
        this.animation = animation
        return true
    }

    override fun onMapClick(p0: LatLng?) {
        animation?.reverse()
        animation = null
    }
}
