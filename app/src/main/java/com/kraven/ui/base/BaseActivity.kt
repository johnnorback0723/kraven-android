package com.kraven.ui.base


import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.kraven.R
import com.kraven.core.AppPreferences
import com.kraven.core.Session
import com.kraven.di.HasComponent
import com.kraven.di.Injector
import com.kraven.di.component.ActivityComponent
import com.kraven.di.component.DaggerActivityComponent
import com.kraven.error.ExceptionHandler
import com.kraven.exception.ApplicationException
import com.kraven.exception.NoDataFoundException
import com.kraven.exception.ServerException
import com.kraven.exception.UpdateException
import com.kraven.extensions.addFireBaseLogs
import com.kraven.location.NewLocationManager
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.authentication.fragement.LoginFragment
import com.kraven.ui.manager.*
import com.kraven.utils.LocationManager
import com.kraven.utils.Locator
import com.throdle.exception.AuthenticationException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*
import org.json.JSONException
import java.net.SocketTimeoutException
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasComponent<ActivityComponent>, HasToolbar, Navigator {
    override val component: ActivityComponent
        get() = activityComponent

    @Inject
    lateinit var navigationFactory: FragmentNavigationFactory

    @Inject
    lateinit var activityStarter: ActivityStarter

    //internal var progressDialog: AVLoadingIndicatorView? = null
    internal var progressDialog: ProgressDialog? = null
    private var alertDialog: AlertDialog? = null

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var session: Session

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var newLocationManager: NewLocationManager

    @Inject
    lateinit var locator: Locator
    private var isBack = false
    private lateinit var activityComponent: ActivityComponent

    private var dialog: Dialog? = null

    val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {

        activityComponent = DaggerActivityComponent.builder()
                .bindApplicationComponent(Injector.INSTANCE.applicationComponent)
                .bindActivity(this)
                .build()

        inject(activityComponent)

        super.onCreate(savedInstanceState)

        dialog = Dialog(this, android.R.style.Theme_Dialog)

        locationManager.setActivity(this)

        setContentView(findContentView())

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        ExceptionHandler.register(this, getString(R.string.CRASH_LOG))
        if (appToolbar != null)
            setSupportActionBar(appToolbar)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setDisplayShowTitleEnabled(false)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        setUpAlertDialog()

        //progressDialog = CustomProgressDialog()
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)

        locationManager.setActivity(this)

    }


    fun newInstance(activity: AppCompatActivity): LocationManager {

        val locationManager = LocationManager()
        locationManager.setActivity(activity)
        return locationManager
    }

    override fun onResume() {
        super.onResume()
        newLocationManager.activity = this
    }

    private fun setUpAlertDialog() {
        alertDialog = AlertDialog.Builder(this)
                .setPositiveButton("ok", null)
                .setTitle(R.string.app_name)
                .create()
    }

    fun <F : BaseFragment> getCurrentFragment(): F? {
        return if (findFragmentPlaceHolder() == 0) null else supportFragmentManager.findFragmentById(findFragmentPlaceHolder()) as? F
    }

    abstract fun findFragmentPlaceHolder(): Int

    @LayoutRes
    abstract fun findContentView(): Int


    abstract fun inject(activityComponent: ActivityComponent)


    override fun setToolbar(toolbar: Toolbar) {
        if (appToolbar != null) {
            if (this.appToolbar != null) {
                toolbar.visibility = View.GONE
            }
            setSupportActionBar(appToolbar)
        }
    }

    override fun showToolbar(isVisible: Boolean) {
        if (supportActionBar != null) {
            if (isVisible) {
                supportActionBar!!.show()
            } else {
                supportActionBar!!.hide()
            }
        }
    }

    override fun setToolbarTitle(title: String) {
        if (appToolbar != null) {
            textViewMainTitle!!.text = title
        }
    }


    fun setToolbarButtonHide() {

    }

    override fun setToolbarTextColorWhite(isWhite: Boolean) {
        isBack = false
        if (appToolbar != null) {
            if (isWhite) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                textViewMainTitle!!.setTextColor(ContextCompat.getColor(this, R.color.white))
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white)
            } else {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                textViewMainTitle!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
            }
        }
    }

    override fun setButtonVisibility(isVisible: Boolean) {
        if (isVisible) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun setToolbarTextColorWhiteClose(isWhite: Boolean) {
        if (appToolbar != null) {
            isBack = true
            appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            textViewMainTitle!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_rating_close)
        }
    }

    override fun setToolbarExpanded(isExp: Boolean, isExpt: Boolean) {

    }

    override fun setDrawerLock(isLock: Boolean) {
        if (drawerLayout != null) {
            if (isLock) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    override fun setToolbarProfile(isProfile: Boolean) {

    }

    override fun showAppBar(isVisible: Boolean) {

    }

    override fun showToolbarElevation(isVisible: Boolean) {

    }

    override fun setToolbarButton(isBack: Boolean) {
        this.isBack = isBack
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            if (isBack) {
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
                appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                textViewMainTitle!!.setTextColor(ContextCompat.getColor(this, R.color.black))

            } else {
                appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                textViewMainTitle!!.setTextColor(ContextCompat.getColor(this, R.color.black))
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)

            }
        }
    }


    override fun setToolbarColor(@ColorRes color: Int) {

        if (appToolbar != null) {
            appToolbar!!.setBackgroundResource(color)

            /*if (color == R.color.colorWhite) {
                if (toolbarTitle != null)
                    toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                if (getSupportActionBar() != null)
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
            } else {
                if (toolbarTitle != null)
                    toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));

                if (getSupportActionBar() != null)
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
            }*/
        }
    }

    fun setDrawerToolbarColor(@ColorRes color: Int) {

        if (appToolbar != null) {
            appToolbar!!.setBackgroundResource(color)

            /* if (color == R.color.colorWhite) {
                if (toolbarTitle != null)
                    toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                if (getSupportActionBar() != null)
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
            } else {
                if (toolbarTitle != null)
                    toolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));

                if (getSupportActionBar() != null)
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
            }*/
        }
    }


    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun shouldGoBack(): Boolean {
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer()
        } else {
            hideKeyboard()
            val currentFragment = getCurrentFragment<BaseFragment>()

            if (currentFragment == null) {
                super.onBackPressed()
            } else if (currentFragment.onBackActionPerform() && shouldGoBack()) {
                super.onBackPressed()
            }

            /*  val count = supportFragmentManager.backStackEntryCount

              if (count == 0) {
                  super.onBackPressed()
                  //additional code
              } else {
                  supportFragmentManager.popBackStack()
              }*/
        }
    }

    fun closeDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun openDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.openDrawer(GravityCompat.START)
    }

    fun hideKeyboard() {
        // Check if no view has focus:

        val view = this.currentFocus
        if (view != null) {
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == LocationManager.REQUEST_CHECK_SETTINGS)
                locationManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                if (isBack) {

                    onBackPressed()
                } else {

                    if (drawerLayout != null) {

                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                }
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("WrongConstant")
    override fun showErrorMessage(message: String) {
        if (getCurrentFragment<BaseFragment>() != null) {
            val snackBar = Snackbar.make(getCurrentFragment<BaseFragment>()!!.view!!, message, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_LONG)
            val snackBarView = snackBar.view
            val tv = snackBarView.findViewById<TextView>(R.id.snackbar_text)
            tv.maxLines = 5
            snackBar.show()
        }


    }

    override fun toggleLoader(show: Boolean, message: String) {
        /* if (show) {
             //progressDialog?.show()
             *//*if (progressDialog!!.isVisible) {
                return
            }
            if (!progressDialog!!.isVisible) {
                if (progressDialog!!.isAdded) {
                    progressDialog!!.dialog?.show()
                } else {
                    progressDialog!!.show(supportFragmentManager, progressDialog!!.javaClass.simpleName)
                }
            }*//*
        } else {
           // progressDialog?.hide()
           *//* if (progressDialog!!.isVisible || progressDialog!!.isAdded)
                progressDialog?.dismissAllowingStateLoss()*//*
        }*/

        if (show) {
            if (!progressDialog!!.isShowing)
                progressDialog!!.setMessage("Please wait...")
            progressDialog!!.show()
        } else {
            if (progressDialog!!.isShowing)
                progressDialog!!.dismiss()
        }
    }

    fun showBackButton(b: Boolean) {
        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(b)
    }

    fun setToolbarElevation(isVisible: Boolean) {
        if (supportActionBar != null) {
            supportActionBar!!.elevation = if (isVisible) 8f else 0f
        }
    }

    fun showKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    override fun <T : BaseFragment> load(tClass: Class<T>): FragmentActionPerformer<T> {
        return navigationFactory.make(tClass)
    }

    override fun loadActivity(aClass: Class<out BaseActivity>): ActivityBuilder {
        return activityStarter.make(aClass)
    }

    override fun <T : BaseFragment> loadActivity(aClass: Class<out BaseActivity>, pageTClass: Class<T>): ActivityBuilder {
        return activityStarter.make(aClass).setPage(pageTClass)
    }


    override fun goBack() {
        onBackPressed()
    }

    fun commandDialog(title: String, message: String, dialogIneterface: BaseFragment.DialogInterface) {

        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setContentView(R.layout.dialog_info)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)

        val textViewDialogTitle = dialog?.findViewById(R.id.textViewDialogTitle) as AppCompatTextView
        textViewDialogTitle.text = title
        val textViewInfo = dialog?.findViewById(R.id.textViewInfo) as AppCompatTextView
        textViewInfo.text = message
        val buttonOk = dialog?.findViewById(R.id.buttonOk) as MaterialButton
        buttonOk.setOnClickListener {
            dialog?.dismiss()
            dialogIneterface.onClickOk()
        }
        if (dialog?.isShowing?.not()!!) {
            dialog?.show()
        }
    }

    override fun onError(throwable: Throwable) {
        //Log.d("Hlink","getCurrentFragment<BaseFragment>() ${getCurrentFragment<BaseFragment>()?::class.java.simpleName}")
        toggleLoader(false, "")
        try {
            when (throwable) {
                is UpdateException -> {
                    commandDialog(getString(R.string.app_name), throwable.message!!,
                            object : BaseFragment.DialogInterface {
                                override fun onClickOk() {
                                    try {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                                        finishAffinity()
                                    } catch (anfe: android.content.ActivityNotFoundException) {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                                        finishAffinity()
                                    }
                                }
                            })
                }
                is ServerException -> showErrorMessage(throwable.message!!)
                /*is ConnectException -> {
                    commandDialog(getString(R.string.app_name), "Please check your network. You appear to have a weak signal",
                            object : BaseFragment.DialogInterface {
                                override fun onClickOk() {

                                }
                            })
                    // showErrorMessage("Please check your network. You appear to have a weak signal")
                }*/
                is ApplicationException -> showErrorMessage(throwable.message)
                is AuthenticationException -> {
                    session.clearSession()
                    session.user = null
                    session.userSession = ""
                    loadActivity(IsolatedActivity::class.java).setPage(LoginFragment::class.java).byFinishingAll().start()
                }
                is SocketTimeoutException -> showErrorMessage("Connection time out")
                is JSONException -> {
                    showErrorMessage(throwable.message!!)
                    addFireBaseLogs("1003", "APILiveData Class", throwable.message!!)
                }
                is NoDataFoundException -> {
                    showErrorMessage(throwable.message!!)
                    addFireBaseLogs("1004", "APILiveData Class", throwable.message!!)
                }
                else -> {

                    throwable.printStackTrace()
                }
            }
            throwable.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setButtonTextVisibility(isVisible: Boolean) {
        if (supportActionBar != null) {
            if (isVisible) {
                tvButtonTitle!!.visibility = View.VISIBLE
            } else {
                tvButtonTitle!!.visibility = View.GONE
            }
        }
    }
}
