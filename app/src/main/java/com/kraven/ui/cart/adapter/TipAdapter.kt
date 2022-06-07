package com.kraven.ui.cart.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.extensions.clearText
import com.kraven.extensions.getText
import kotlinx.android.synthetic.main.tip_raw.view.*


class TipAdapter(var tipList: ArrayList<String>, var onSelectTip: OnSelectTip) : RecyclerView.Adapter<TipAdapter.ViewHolder>() {

    interface OnSelectTip {
        fun onSelectTip(selectTip: String, isEnter: Boolean)
    }

    private var lastSelectedPosition = -1
    private var lastSelectEdit = -1

    fun setPosition() {
        lastSelectedPosition = -1
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tip_raw, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindData(position)
    }

    override fun getItemCount(): Int {
        return tipList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun onBindData(position: Int) = with(itemView) {
            editTextTip.clearText()
            textViewTip.text = tipList[position] + " " + "%"

            textViewTip.setOnClickListener {
                editTextTip.setBackgroundResource(R.drawable.text_view_tip_bg_gray)
                onSelectTip.onSelectTip(tipList[position], false)
                lastSelectedPosition = adapterPosition
                lastSelectEdit = -1
                notifyDataSetChanged()
            }
            if (position == (tipList.size - 1)) {
                editTextTip.visibility = View.VISIBLE

                editTextTip.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus && lastSelectEdit == -1) {
                        lastSelectedPosition = position
                        lastSelectEdit = 0
                        notifyDataSetChanged()
                    }
                }

                editTextTip.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        onSelectTip.onSelectTip(p0.toString(), true)

                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                })

                editTextTip.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            //val selectTips = holder.editTextTip.setText(twoDigit(getText(holder.editTextTip)!!.toFloat()))
                            if (getText(editTextTip).isNullOrEmpty().not()) {
                                val imm = itemView.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(itemView.windowToken, 0)
                                lastSelectEdit = -1

                            } else {
                                Toast.makeText(itemView.context, itemView.context.getString(R.string.select_tip), Toast.LENGTH_SHORT).show()
                            }

                            //onSelectTip.onSelectTip(getText(holder.editTextTip)!!, true)

                            return true
                        }
                        return false
                    }
                })
            }
            textViewTip.setBackgroundResource(if (position == lastSelectedPosition && (lastSelectEdit < 0))
                R.drawable.text_view_tip_bg else R.drawable.text_view_tip_bg_gray)

            if (position == lastSelectedPosition && lastSelectEdit == 0) {
                editTextTip.requestFocus()
                editTextTip.setBackgroundResource(R.drawable.text_view_tip_bg)
            } else if (lastSelectEdit == -1) {
                editTextTip.setBackgroundResource(R.drawable.text_view_tip_bg_gray)

            }
        }
    }


}