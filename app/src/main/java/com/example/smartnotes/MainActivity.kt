package com.example.smartnotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartnotes.databinding.ActivityMainBinding
import com.example.smartnotes.ui.NotesListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(NotesListFragment(), false)
        }
    }

    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()

        // Add animations
        transaction.setCustomAnimations(
            R.anim.slide_in_right,  // enter
            R.anim.slide_out_left,  // exit
            R.anim.slide_in_right,  // popEnter
            R.anim.slide_out_left   // popExit
        )

        transaction.replace(R.id.nav_host_fragment, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}