package com.moka.gdgqrr.vp.audience

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.jakewharton.rxbinding.widget.RxTextView
import com.moka.framework.base.BaseFragment
import com.moka.framework.extenstion.init
import com.moka.framework.extenstion.put
import com.moka.gdgqrr.R
import com.moka.gdgqrr.model.User
import kotlinx.android.synthetic.main.fragment_audience_list.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

class AudienceListFragment : BaseFragment() {

    private var attendeeList: ArrayList<AttendeeAdapter.AttendeeData> = ArrayList()
    private var attendeeListFromSearch: ArrayList<AttendeeAdapter.AttendeeData> = ArrayList()

    private lateinit var adapter: AttendeeAdapter
    private lateinit var mDatabase: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater!!.inflate(R.layout.fragment_audience_list, container, false)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        loadData()
    }

    private fun initView() {
        adapter = AttendeeAdapter(activity)
        adapter.onItemClickListener = { onClickVisit(it) }

        recyclerView.init(activity, adapter)

        setAutoSearchOnEditText()

        imageView_back.setOnClickListener { activity.onBackPressed() }
        textView_cancel.setOnClickListener { onClickToCancel() }
    }

    private fun setAutoSearchOnEditText() {
        RxTextView.textChanges(editText_search)
                .debounce(222.toLong(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { text ->
                    if (text.isNullOrEmpty())
                        textView_cancel.visibility = View.GONE
                    else
                        textView_cancel.visibility = View.VISIBLE

                    attendeeListFromSearch.clear()
                    Observable.from(attendeeList)
                            .filter { it.user.name!!.contains(text) }
                            .subscribe(
                                    {
                                        attendeeListFromSearch.add(it)
                                    },
                                    { e -> },
                                    {
                                        adapter.items = attendeeListFromSearch
                                    })
                }
                .put(getCompositeSubscription())
    }

    private fun loadData() {
        progressBar_loading.visibility = View.VISIBLE
        attendeeList.clear()

        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("attendees").orderByKey().addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                if (null == dataSnapshot || !isAdded)
                    return
                progressBar_loading.visibility = View.GONE

                val user = dataSnapshot.getValue(User::class.java)
                user.id = dataSnapshot.key

                attendeeList.add(AttendeeAdapter.AttendeeData(user))
                adapter.items = attendeeList
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
            }

            override fun onCancelled(p0: DatabaseError?) {
            }

        })
    }

    /**
     * Listener
     */

    private fun onClickVisit(data: AttendeeAdapter.AttendeeData) {
        if (data.user.isVisit == 0) {
            data.user.isVisit = 1
            mDatabase.child("attendees").child(data.user.id).child("isVisit").setValue(1)
        }
        else {
            data.user.isVisit = 0
            mDatabase.child("attendees").child(data.user.id).child("isVisit").setValue(0)
        }
        adapter.notifyDataSetChanged()
    }

    private fun onClickToCancel() {
        editText_search.text.clear()
        loadData()
    }


}