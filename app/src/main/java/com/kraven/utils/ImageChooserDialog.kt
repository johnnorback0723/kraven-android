package com.kraven.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window


import com.kraven.R

import java.io.File
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


/**
 * Created by jd on 30/11/2018.
 */
class ImageChooserDialog : DialogFragment(), View.OnClickListener {


    private var selectedImage: Uri? = null
    private var imageUri: Uri? = null

    private var selectedImagePath: String? = null

    private var imageCallBack: ImageCallBack? = null

    fun setImageCallBack(imageCallBack: ImageCallBack) {
        this.imageCallBack = imageCallBack
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_select_image, container, false)
        val textViewCamera = view.findViewById<AppCompatTextView>(R.id.textViewCamera)
        textViewCamera.setOnClickListener(this)
        val textViewGallery = view.findViewById<AppCompatTextView>(R.id.textViewGallery)
        textViewGallery.setOnClickListener(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_FROM_GALLERY ->
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImage = data!!.data
                    val path = FileUtils.getPath(activity, selectedImage)

                    val imagePicker = ImagePicker()
                    imagePicker.setPick(true)
                    imagePicker.setImagePath(path)

                    if (imageCallBack != null)
                        imageCallBack!!.sendImage(imagePicker)
                    dismiss()

                } else {
                    val imagePicker = ImagePicker()
                    imagePicker.setPick(false)
                    imagePicker.setError("PROBLEM TO SET IMAGE FROM GALLERY")
                    if (imageCallBack != null)
                        imageCallBack!!.sendImage(imagePicker)
                    dismiss()
                }
            PICK_FROM_CAMERA -> if (resultCode == Activity.RESULT_OK) {
                val path = selectedImagePath
                val imagePicker = ImagePicker()
                imagePicker.setPick(true)
                imagePicker.setImagePath(path)
                if (imageCallBack != null)
                    imageCallBack!!.sendImage(imagePicker)
                dismiss()

            } else {
                val imagePicker = ImagePicker()
                imagePicker.setPick(false)
                imagePicker.setError("PROBLEM TO SET IMAGE FROM GELLERY")
                if (imageCallBack != null)
                    imageCallBack!!.sendImage(imagePicker)
                dismiss()
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag)
        }
    }

    override fun dismiss() {
        super.dismissAllowingStateLoss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.textViewCamera -> {
                getSaveImageUri()
                val mIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                mIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(mIntent, PICK_FROM_CAMERA)
            }
            R.id.textViewGallery -> {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_FROM_GALLERY)
            }
        }
    }

    private fun getSaveImageUri() {

        try {
            val root = File(Environment.getExternalStorageDirectory().toString() + "/KravenDriver/")
            if (!root.exists()) {
                root.mkdirs()
            }
            val imageName = "image_" + System.currentTimeMillis() + ".png"

            val sdImageMainDirectory = File(root, imageName)


            val isNoget = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            if (isNoget) {
                imageUri = FileProvider.getUriForFile(activity!!, activity!!.packageName + ".provider", sdImageMainDirectory)
                selectedImagePath = sdImageMainDirectory.absolutePath
            } else {
                imageUri = Uri.fromFile(sdImageMainDirectory)
                selectedImagePath = FileUtils.getPath(activity,
                        imageUri)
            }
        } catch (e: Exception) {
            DebugLog.e("Incident Photo" + "Error occurred. Please try again later.")
        }


    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            getDialog()?.window!!.setGravity(Gravity.BOTTOM)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    interface ImageCallBack {
        fun sendImage(imagePicker: ImagePicker)
    }

    companion object {


        private const val PICK_FROM_CAMERA = 2
        private const val PICK_FROM_GALLERY = 1


        fun newInstance(): ImageChooserDialog {

            val args = Bundle()

            val fragment = ImageChooserDialog()
            fragment.arguments = args

            return fragment
        }
    }

}