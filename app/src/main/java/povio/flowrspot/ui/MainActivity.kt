package povio.flowrspot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import povio.flowrspot.R
import povio.flowrspot.databinding.ActivityMainBinding
import povio.flowrspot.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addFragmentToContainer(HomeFragment.newInstance())
    }

    private fun addFragmentToContainer(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment).commitAllowingStateLoss()
    }
}