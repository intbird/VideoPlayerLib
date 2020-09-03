package intbird.soft.lib.video.player.main.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.lib_media_player_clarity.*
import kotlinx.android.synthetic.main.lib_media_player_clarity_item.view.*
import intbird.soft.lib.video.player.R
import intbird.soft.lib.video.player.api.bean.MediaClarity
import intbird.soft.lib.video.player.main.VideoPlayerFragment

/**
 * created by intbird
 * on 2020/8/31
 * DingTalk id: intbird
 */
class ClarityDialogFragment : DialogFragment() {

    companion object {
        fun dismissDialog(fragmentManager: FragmentManager) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val dialog: Fragment? = fragmentManager.findFragmentByTag("dialog")
            if (dialog != null) {
                fragmentTransaction.remove(dialog)
            }
            fragmentTransaction.addToBackStack(null)
        }

        fun showDialog(fragmentManager: FragmentManager) {
            dismissDialog(fragmentManager)

            val fragmentTransaction = fragmentManager.beginTransaction()
            val dialogFragment = ClarityDialogFragment()
            dialogFragment.show(fragmentTransaction, "dialog")
        }
    }

    private val viewModel: VideoPlayerFragment.SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lib_media_player_clarity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flLeft.setOnClickListener { dismiss() }
        createRecycleView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun createRecycleView() {
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = StringArrayAdapter(this, onCreateItem(), selectedItem())
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun onCreateItem(): ArrayList<MediaClarity> {
        return viewModel.clarityArray.value ?: arrayListOf()
    }

    private fun selectedItem(): MediaClarity? {
        return viewModel.clarityArrayChecked.value
    }

    private fun onChooseItem(mediaClarity: MediaClarity) {
        mediaClarity.clarityChecked = true
        mediaClarity.selectedByUser = true
        viewModel.clarityArrayChecked.value = mediaClarity
        dismiss()
    }

    class StringArrayAdapter(
        private val fragment: ClarityDialogFragment,
        private val itemArray: ArrayList<MediaClarity>,
        private val selected: MediaClarity?
    ) :
        RecyclerView.Adapter<StringArrayAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        private var singleValueIndexes = arrayListOf<Int>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val holdView = LayoutInflater.from(parent.context)
                .inflate(R.layout.lib_media_player_clarity_item, parent, false)
            return ViewHolder(holdView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val adapterItem = itemArray[position]
            holder.view.textView.text = adapterItem.clarityText
            holder.view.textView.setTextColor(
                if (adapterItem.clarityChecked || (position == selected?.clarityIndex)) {
                    singleValueIndexes.add(position)
                    Color.parseColor("#FFAC33")
                } else Color.WHITE
            )

            if(!TextUtils.isEmpty(adapterItem.clarityText)) {
                holder.view.textView.setOnClickListener {
                    clearChecked()
                    fragment.onChooseItem(adapterItem)
                }
            }
        }

        override fun getItemCount() = itemArray.size

        private fun clearChecked() {
            for (value in singleValueIndexes) {
                itemArray[value].clarityChecked = false
            }
            singleValueIndexes.clear()
        }
    }
}