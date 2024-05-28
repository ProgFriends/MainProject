package com.prog.mainproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.prog.mainproject.databinding.ActivityNaviBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_FV = "fv_fragment"
private const val TAG_PS = "ps_fragment"
private const val TAG_SHOW = "show_fragment"

class NaviActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_home -> setFragment(TAG_HOME, HomeFragment())
                R.id.page_fv -> setFragment(TAG_FV, fvFragment())
                R.id.page_ps-> setFragment(TAG_PS, PsFragment())
                R.id.page_show-> setFragment(TAG_SHOW, ShowFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val fv = manager.findFragmentByTag(TAG_FV)
        val ps = manager.findFragmentByTag(TAG_PS)
        val show = manager.findFragmentByTag(TAG_SHOW)

        if (home != null) {
            fragTransaction.hide(home)
        }

        if (fv != null) {
            fragTransaction.hide(fv)
        }

        if (ps != null) {
            fragTransaction.hide(ps)
        }

        if (show != null) {
            fragTransaction.hide(show)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        } else if (tag == TAG_FV) {
            if (fv != null) {
                fragTransaction.show(fv)
            }
        } else if (tag == TAG_PS) {
            if (ps != null) {
                fragTransaction.show(ps)
            }
        } else if (tag == TAG_SHOW) {
            if (show != null) {
                fragTransaction.show(show)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}