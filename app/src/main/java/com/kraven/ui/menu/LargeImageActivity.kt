package com.kraven.ui.menu


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kraven.R
import kotlinx.android.synthetic.main.frag_image_large.*


class LargeImageActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_image_large)
        val intent = intent


        Glide.with(this)
                .load(intent.extras!!.getString("image"))
                .into(large_image)


        large_image.setOnClickListener {
            supportFinishAfterTransition()
        }
    }
}