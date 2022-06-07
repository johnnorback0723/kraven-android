package com.kraven.ui.refer.friend

import android.content.ClipData
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import kotlinx.android.synthetic.main.refer_friends_fragment.*
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.widget.Toast
import com.kraven.BuildConfig
import com.kraven.extensions.getText
import com.kraven.ui.home.fragment.HomeNewFragment


class ReferFriendsFragment : BaseFragment() {
    override fun createLayout(): Int = R.layout.refer_friends_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.refer_friends))
        toolbar.setToolbarTextColorWhite(false)
        toolbar.setButtonTextVisibility(false)

        textViewReferCode.text = session.user?.referralCode


        imageViewCopy.setOnClickListener {
            val clipboard = activity!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("label", getTextSend())
            //clipboard!!.primaryClip = clip
            clipboard!!.setPrimaryClip(clip)
            Toast.makeText(activity!!, "Copy text", Toast.LENGTH_SHORT).show()
        }

        buttonShare.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getTextSend())
            startActivity(Intent.createChooser(sharingIntent, "Share"))
        }
    }

    private fun getTextSend(): String {
        return "Download the KRAVEN Food and Beverage delivery app NOW! Use my referral code " + getText(textViewReferCode) + " when signing up. Get \$5 off your first order by using promo code KRAVE5OFF in the cart!" + "\n" + "https://kravenbahamas.com"
    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }

}
