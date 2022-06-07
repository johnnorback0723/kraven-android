package com.kraven.services


import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.android.gms.maps.model.LatLng
import com.kraven.core.Common
import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.CommonRepository
import com.kraven.di.Injector
import com.kraven.di.component.DaggerServiceComponent
import com.kraven.di.component.ServiceComponent
import com.kraven.utils.LocationManager
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject


/**
 * Created by Android Developer on 21/12/18.
 */
class LocationUpdateService : Service(), LocationManager.LocationListener {

    @Inject
    lateinit var commonRepository: CommonRepository

    @Inject
    lateinit var locationManager: LocationManager

    private val compositeDisposable = CompositeDisposable()
    private lateinit var serviceComponent: ServiceComponent


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        serviceComponent = DaggerServiceComponent
                .builder()
                .bindApplicationComponent(Injector.INSTANCE.applicationComponent)
                .bindService(this)
                .build()

        serviceComponent.inject(this)


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action != null && intent.action.equals(Common.Actions.START_SERVICE, ignoreCase = true)) {
            locationManager.getLocationUpdate(this)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               val  notification = Notification( 0, null, System.currentTimeMillis() )
                notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
                startForeground(1, notification)
            }

        } else if (intent.action != null && intent.action.equals(Common.Actions.STOP_SERVICE, ignoreCase = true)) {
            //locationManager.stop()
            stopForeground(true)
            stopSelf()
        }

        return START_NOT_STICKY // this will prevent to restart service
    }


    override fun onFail(status: LocationManager.LocationListener.Status) {

    }

    override fun onLocationAvailable(latLng: LatLng) {
        val parameters = Parameters()


        parameters.latitude = latLng.latitude.toString()
        parameters.longitude = latLng.longitude.toString()
        if (::commonRepository.isInitialized) {
            commonRepository
                    .updateLocation(parameters)
                    .subscribe(object : SingleObserver<DataWrapper<Any>> {
                        override fun onError(e: Throwable) {}
                        override fun onSuccess(t: DataWrapper<Any>) {

                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }
                    })
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

}