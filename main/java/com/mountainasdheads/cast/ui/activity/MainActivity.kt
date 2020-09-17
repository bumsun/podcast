package com.mountainasdheads.cast.ui.activity

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.ui.fragments.*
import com.mountainasdheads.cast.utils.interfaces.OnMusicPicked

class MainActivity : AppCompatActivity() {
    private var currentFragment : BaseFragment? = null
    lateinit var nextFragment: BaseFragment
    lateinit var editFragment: BaseFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpFragments()


    }

    private fun setUpFragments(){
        currentFragment = CreateNewPodcastFragment.newInstance()
        setFragment()
        nextFragment = PodcastDetailsFragment.newInstance()
    }
    private fun replaceFragment(fragment: Fragment, animation : Int){
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(animation, 0)
        transaction.add(R.id.fragmentPlaceholder, fragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }
    public fun setDetailsFragment(){
        currentFragment = nextFragment
        replaceFragment(currentFragment!!, R.anim.enter_from_right)
        nextFragment = PreviewPodcastFragment.newInstance()
        editFragment = EditPodcastFragment.newInstance()
    }
    public fun setEditFragment(){
        currentFragment = editFragment
        replaceFragment(currentFragment!!, R.anim.enter_from_right)
        nextFragment = PreviewPodcastFragment.newInstance()
    }
    public fun setMusicPickerFragment(onMusicPicked: OnMusicPicked){
        val musicFragment = MusicPickerFragment.newInstance()
        musicFragment.onMusicPicked = onMusicPicked
        replaceFragment(musicFragment, R.anim.enter_from_right)
    }
    public fun setPreviewFragment(){
        currentFragment = nextFragment
        replaceFragment(currentFragment!!, R.anim.enter_from_right)
        nextFragment = PodcasterCreatedFragment.newInstance()
    }
    public fun setPodcastCreatedFragment(){
        val fm: FragmentManager = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        currentFragment = nextFragment
        setFragment()
        //replaceFragment(currentFragment!!, R.anim.enter_from_right)
        nextFragment = NewsFeedEditFragment.newInstance()
    }
    public fun setNewsfeedEditFragment(){
        currentFragment = nextFragment
        replaceFragment(currentFragment!!, R.anim.enter_from_right)
        nextFragment = NewsfeedFragment.newInstance()
    }
    public fun setNewsfeedFragment(){
        currentFragment = nextFragment
        replaceFragment(currentFragment!!, R.anim.enter_from_right)
        nextFragment = MyPodcastsFragment.newInstance()
    }
    public fun setMyPodcastsFragment(){
        val fm: FragmentManager = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        currentFragment = nextFragment
        setFragment()
        //replaceFragment(currentFragment!!, R.anim.enter_from_right)
        nextFragment = PodcastDetailsFragment.newInstance()
    }
    private fun setFragment(){
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragmentPlaceholder, currentFragment!!)
        transaction.commitAllowingStateLoss()
    }

    fun back(prevFragment: BaseFragment){
        supportFragmentManager.popBackStack()
        nextFragment = prevFragment
    }
}