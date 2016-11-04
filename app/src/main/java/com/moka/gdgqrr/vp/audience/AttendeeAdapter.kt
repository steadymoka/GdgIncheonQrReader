package com.moka.gdgqrr.vp.audience


import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moka.framework.extenstion.parseVisitString
import com.moka.framework.widget.adapter.BaseAdapter
import com.moka.framework.widget.adapter.ItemData
import com.moka.framework.widget.adapter.RecyclerItemView
import com.moka.gdgqrr.R
import com.moka.gdgqrr.model.User
import com.moka.gdgqrr.vp.audience.AttendeeAdapter.AttendeeData
import com.moka.gdgqrr.vp.audience.AttendeeAdapter.AttendeeItemView
import kotlinx.android.synthetic.main.attendee_item_view.view.*


class AttendeeAdapter constructor(private val context: Context) : BaseAdapter<AttendeeData, AttendeeItemView>(context) {

    var onItemClickListener: ((data: AttendeeData) -> Unit)? = null

    override fun onCreateContentItemViewHolder(parent: ViewGroup, contentViewType: Int): RecyclerView.ViewHolder {
        val itemView = AttendeeItemView(context, parent)
        itemView.onItemClickListener = onItemClickListener
        return itemView
    }

    /**
     */

    class AttendeeData(var user: User) : ItemData()

    inner class AttendeeItemView(context: Context, parent: ViewGroup) :
            RecyclerItemView<AttendeeData>(context, LayoutInflater.from(context).inflate(R.layout.attendee_item_view, parent, false)) {

        var onItemClickListener: ((data: AttendeeData) -> Unit)? = null

        init {
            itemView.textView_isVisit.setOnClickListener { if (null != onItemClickListener) onItemClickListener!!(data) }
        }

        override fun refreshView(data: AttendeeData?) {
            if (null == data)
                return

            itemView.textView_name.text = data.user.name
            itemView.textView_email.text = data.user.email
            itemView.textView_isVisit.text = data.user.isVisit.parseVisitString()

            if (data.user.isVisit == 0)
                itemView.textView_isVisit.setTextColor(ContextCompat.getColor(context, R.color.red))
            else
                itemView.textView_isVisit.setTextColor(ContextCompat.getColor(context, R.color.base_text_view_color))
        }

    }

}
