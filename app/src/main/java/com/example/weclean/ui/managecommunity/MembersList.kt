package com.example.weclean.ui.managecommunity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weclean.R
import com.example.weclean.backend.Community
import com.example.weclean.backend.FireBase
import com.example.weclean.ui.login.LoginActivity
import com.example.weclean.ui.profile.CommunityAdapter
import com.example.weclean.ui.profile.CommunityListData
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MembersList : Fragment(), MemberAdapter.RecyclerViewMember {
    private var fireBase = FireBase()

    //TODO set up firebase to get list of members of current community. how?
    //private val memberObject = Member(fireBase)

    private val members = ArrayList<MemberListData>()

    private lateinit var membersListAdapter: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_members_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the recycler view
        membersListAdapter = MemberAdapter(members, this)
        val memberListView = view.findViewById<RecyclerView>(R.id.member_list)

        val mLayoutManager = LinearLayoutManager(activity as AppCompatActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        memberListView.layoutManager = mLayoutManager
        memberListView.itemAnimator = DefaultItemAnimator()
        memberListView.adapter = membersListAdapter

        setMembers()
    }

    private fun setMembers() {
        runBlocking {
            launch {
                val userId = fireBase.currentUserId()

                //TODO firebase stuff. get member names of current community

            }
        }
    }

    override fun onMemberClicked(adapterPosition: Int) {
        // This method will be called when a member is clicked in the RecyclerView
        // handle the click event here

        Toast.makeText(requireContext(), "Member clicked at position $adapterPosition", Toast.LENGTH_SHORT).show()
    }
}