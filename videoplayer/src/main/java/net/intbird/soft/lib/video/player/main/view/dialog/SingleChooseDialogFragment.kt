package net.intbird.soft.lib.video.player.main.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.intbird.soft.lib.video.player.R
import net.intbird.soft.lib.video.player.api.bean.MediaCheckedData
import kotlinx.android.synthetic.main.lib_media_player_dialog_single_choose.*
import kotlinx.android.synthetic.main.lib_media_player_dialog_single_choose_item.view.*

/**
 * created by Bird
 * on 2020/9/11
 * DingTalk id: intbird
 */

class SingleChooseDialogFragment : DialogFragment() {

    companion object {
        const val DATA = "data"
        const val HEIGHT = "height"
        fun dismissDialog(fragmentManager: FragmentManager) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val dialog: Fragment? = fragmentManager.findFragmentByTag("dialog")
            if (dialog != null) {
                fragmentTransaction.remove(dialog)
            }
            fragmentTransaction.addToBackStack(null)
        }

        fun showDialog(fragmentManager: FragmentManager, listData: ArrayList<out MediaCheckedData>?, singleChooseCallback: SingleChooseCallback, height:Int? = 0) {
            dismissDialog(fragmentManager)

            val fragmentTransaction = fragmentManager.beginTransaction()
            val dialogFragment = SingleChooseDialogFragment()
            val arguments = Bundle()
            arguments.putSerializable(DATA, listData)
            arguments.putInt(HEIGHT, height?:0)
            dialogFragment.arguments = arguments
            dialogFragment.show(fragmentTransaction, "dialog")
            dialogFragment.registerCallback(singleChooseCallback)
        }
    }

    private var singleChooseCallback:SingleChooseCallback? = null
    private fun registerCallback(singleChooseCallback: SingleChooseCallback) {
        this.singleChooseCallback = singleChooseCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lib_media_player_dialog_single_choose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetViewHeight(layoutPlayerCover)
        flLeft.setOnClickListener { dismiss() }
    }

    private fun resetViewHeight(view: View) {
        view.post {
            val height = arguments?.getInt(HEIGHT)?:0
            if (height > 0) {
                val lp = view.layoutParams; lp?.height = height
                if (null != lp) view.layoutParams = lp
                view.requestLayout()

                createRecycleView()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun createRecycleView() {
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = StringArrayAdapter(this, onCreateItem())
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            background = ContextCompat.getDrawable(context, R.color.lib_media_player_control_panel_alpha)
        }
    }

    private fun onCreateItem(): ArrayList<MediaCheckedData> {
        return arguments?.getSerializable(DATA) as? ArrayList<MediaCheckedData>?: ArrayList()
    }

    private fun onChooseItem(index:Int, mediaCheckedData: MediaCheckedData) {
        mediaCheckedData.checked = true
        singleChooseCallback?.onChooseItem(index, mediaCheckedData)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        singleChooseCallback = null
        super.onDismiss(dialog)
    }

    class StringArrayAdapter(
        private val fragment: SingleChooseDialogFragment,
        private val itemArray: ArrayList<MediaCheckedData>
    ) :
        RecyclerView.Adapter<StringArrayAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        private val singleChooseColor = Color.parseColor("#FFAC33")
        private var singleValueIndexes = arrayListOf<Int>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val holdView = LayoutInflater.from(parent.context)
                .inflate(R.layout.lib_media_player_dialog_single_choose_item, parent, false)
            return ViewHolder(holdView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val adapterItem = itemArray[position]
            holder.view.textView.text = adapterItem.text
            holder.view.textView.setTextColor(
                if (adapterItem.checked) {
                    singleValueIndexes.add(position)
                    singleChooseColor
                } else Color.WHITE
            )

            if (!TextUtils.isEmpty(adapterItem.text)) {
                holder.view.textView.setOnClickListener {
                    clearChecked()
                    fragment.onChooseItem(position, adapterItem)
                }
            }
        }

        override fun getItemCount() = itemArray.size

        private fun clearChecked() {
            for (value in singleValueIndexes) {
                itemArray[value].checked = false
            }
            singleValueIndexes.clear()
        }
    }
}