package com.kraven.ui.comman.fragment


import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.activity.AuthenticationActivity
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.authentication.fragement.LoginFragment
import com.kraven.ui.authentication.fragement.SelectIslandFragment
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.comman.adapter.DrawerAdapter
import com.kraven.ui.edit.profile.fragment.EditProfileFragment
import com.kraven.ui.favorite.fragment.FavoriteFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.ui.my.offer.fragment.MyOfferFragment
import com.kraven.ui.my.order.fragment.MyOrderHistoryFragment
import com.kraven.ui.refer.friend.ReferFriendsFragment
import com.kraven.ui.setting.fragment.SettingFragment
import com.kraven.ui.setting.fragment.SupportUsFragment
import com.kraven.ui.wallet.fragment.MyWalletFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.drawer_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


class DrawerFragment : BaseFragment(), DrawerAdapter.DrawerAdapterInterface, GoogleApiClient.OnConnectionFailedListener {


    var drawerAdapter: DrawerAdapter? = null

    override fun createLayout(): Int = R.layout.drawer_layout

    private lateinit var authViewModel: AuthViewModel
    var mGoogleApiClient: GoogleSignInClient? = null

    //lateinit var closeNavigationDrawer: CloseNavigationDrawer
    lateinit var homeActivity: HomeActivity

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    /* override fun onAttach(context: Context) {
         super.onAttach(context)
         try {
             this.homeActivity = context as HomeActivity
             this.closeNavigationDrawer = context

         } catch (e: ClassCastException) {
             throw ClassCastException("$context must implement OnCompleteListener")
         }
     }

     interface CloseNavigationDrawer {
         fun close()
     }
 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.liveData.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (responseBody.code == StatusCode.CODE_SUCCESS) {


                        /*  val locationServiceIntent = Intent(requireContext(), LocationUpdateService::class.java)
                          locationServiceIntent.action = Common.Actions.STOP_SERVICE
                          requireContext().startService(locationServiceIntent)*/

                        if (session.user?.signupType == "Google") {
                            Log.d("JSR", "GoogleSignInOptions Builder  - 3")
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build()

                            mGoogleApiClient = GoogleSignIn.getClient(activity!!, gso)

                            mGoogleApiClient?.signOut()?.addOnCompleteListener {

                            }
                        } else if (session.user?.signupType == "Facebook") {
                            LoginManager.getInstance().logOut()
                        }

                        //}

                        session.clearSession()
                        session.user = null
                        session.userSession = ""
                        DrawerAdapter.row_index = 0
                        navigator.loadActivity(AuthenticationActivity::class.java).setPage(LoginFragment::class.java).byFinishingAll().start()
                    }
                }
                , { true })


    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun bindData() {
        //DrawerAdapter.row_index = 0
        val displayMetrics = DisplayMetrics()
        activity?.windowManager
                ?.defaultDisplay
                ?.getMetrics(displayMetrics)
        constrainList.layoutParams.width = displayMetrics.widthPixels
        constrainList.requestLayout()
        recyclerViewMenu.layoutManager = GridLayoutManager(activity, 1)
        val array = resources.getStringArray(R.array.drawer_menu)
        val list = listOf(*array)
        drawerAdapter = DrawerAdapter(list, this)
        recyclerViewMenu.adapter = drawerAdapter

        Glide.with(activity!!)
                .load(session.user?.profileImage)
                .apply(RequestOptions().placeholder(R.drawable.ic_user))
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(imageViewProfile)

        textViewUserName.text = getString(R.string.space_string, session.user?.firstName, session.user?.lastName)

        imageViewProfile.setOnClickListener {
            navigator.load(EditProfileFragment::class.java).replace(false)
            //closeNavigationDrawer.close()
            EventBus.getDefault().post("close")
        }
    }

    override fun onClickItem(position: Int) {
        //closeNavigationDrawer.close()
        GlobalScope.launch(Dispatchers.Main) {
            delay(150)

            when (position) {
                0 -> {
                    //Home
                    navigator.load(HomeNewFragment::class.java).replace(false)
                }
                1 -> {
                    //Promos
                    navigator.load(MyOfferFragment::class.java).replace(false)
                }
                2 -> {
                    //Wallet
                    navigator.load(MyWalletFragment::class.java).replace(false)
                }
                3 -> {
                    //Refer A Friends
                    navigator.load(ReferFriendsFragment::class.java).replace(false)
                }
                4 -> {
                    //My Profile
                    navigator.load(EditProfileFragment::class.java).replace(false)
                }
                5 -> {
                    //Order History
                    navigator.load(MyOrderHistoryFragment::class.java).replace(false)
                }
                6 -> {
                    //Favorite Restaurant
                    navigator.load(FavoriteFragment::class.java).replace(false)

                }
                7 -> {
                    //Edit Island
                    navigator.load(SelectIslandFragment::class.java).setBundle(bundleOf(ConstantApp.PassValue.NEW_ISLAND to false)).replace(false)
                }
                8 -> {
                    //Contact Us
                    navigator.load(SupportUsFragment::class.java).replace(false)
                }
                9 -> {
                    //Support Us
                    navigator.load(SettingFragment::class.java).replace(false)
                }
                10 -> {
                    //Logout
                    commandDialogYesNo(getString(R.string.app_name), getString(R.string.sure_logout), object : BaseFragment.DialogInterfaceYesNo {
                        override fun onClickYes() {
                            if (session.isProtoType) {
                                session.user = null
                                session.userSession = ""
                                // session.user = null
                                //session.userSession = ""
                                //session.isUploadImage = false
                                navigator.loadActivity(IsolatedActivity::class.java).setPage(LoginFragment::class.java).byFinishingAll().start()
                            } else {
                                showLoader(true)
                                authViewModel.logout()
                            }
                        }

                        override fun onClickNo() {

                        }
                    })
                }
            }
        }
        EventBus.getDefault().post("close")

    }

    fun setPosition(text: Int) {
        drawerAdapter?.setPosition(text)
    }

    fun updateUser() {
        if (session.isProtoType) {
            textViewUserName.text = "JOHN DEO"
            Glide.with(activity!!)
                    .load("https://engineering.unl.edu/images/staff/Kayla_Person-small.jpg")
                    .apply(RequestOptions().placeholder(R.drawable.placeholder_car))
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageViewProfile)
        } else {
            Glide.with(activity!!)
                    .load(session.user?.profileImage)
                    .apply(RequestOptions().placeholder(R.drawable.ic_user))
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageViewProfile)

            textViewUserName.text = getString(R.string.space_string, session.user?.firstName, session.user?.lastName)
        }


    }
}