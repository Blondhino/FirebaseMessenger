package com.blondi.firebasemessenger.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class signupLoginAdapter (fragmentManager :  FragmentManager):FragmentPagerAdapter(fragmentManager) {
private val fragments = mutableListOf<Fragment>()
    fun addFragment(fragment : Fragment){fragments.add(fragment); notifyDataSetChanged()}
    fun removeFragment(position: Int){fragments.removeAt(position)}
    override fun getItem(position: Int): Fragment {return  fragments[position]}
    override fun getCount(): Int {return fragments.size}
}