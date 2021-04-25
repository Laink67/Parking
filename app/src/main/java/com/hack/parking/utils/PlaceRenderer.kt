package com.hack.parking.utils

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.hack.parking.R
import com.hack.parking.data.model.Parking

class PlaceRenderer(
    private val context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<Parking>
) : DefaultClusterRenderer<Parking>(context, map, clusterManager) {

    // вызывается до отображения кластера на карте. Здесь вы можете предоставить настройки через MarkerOptions-
    // в этом случае он устанавливает заголовок, положение и значок маркера.
    override fun onBeforeClusterItemRendered(item: Parking?, markerOptions: MarkerOptions) {

        item?.let {
            markerOptions.title(it.title)
                .position(it.position)
                .icon(BitmapHelper.vectorToBitmap(context, R.drawable.marker))
        }
    }

    //вызывается сразу после отображения маркера на карте. Здесь вы можете получить доступ к созданному Marker объекту -
    // в этом случае он устанавливает свойство тега маркера.
    override fun onClusterItemRendered(clusterItem: Parking?, marker: Marker?) {
        marker?.tag = clusterItem
    }
}