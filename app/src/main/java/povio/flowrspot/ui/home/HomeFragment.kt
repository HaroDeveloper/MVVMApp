package povio.flowrspot.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import povio.flowrspot.R
import povio.flowrspot.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by inject()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        addCollectors()
    }

    private fun setAdapter() {
        TODO("Not yet implemented")
    }

    private fun addCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state.collect {
                    it.let {
                        when (it) {
                            State.LAST_PAGE_REACHED -> TODO()
                            State.LOADING_NEXT_PAGE -> TODO()
                            State.FILTERING_DONE -> TODO()
                            State.FILTER_REMOVED -> TODO()
                            State.LOADING_INIT -> showProgressBar()
                            State.PAGE_LOADED -> hideProgressBar()
                            State.FAILURE_NETWORK -> showNetworkError()
                            State.FAILURE_GENERIC -> showGenericError()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.flowers.collect{
                    //setAdapter
                }
            }

        }
    }

    private fun showGenericError() {
        TODO("Not yet implemented")
    }

    private fun showNetworkError() {
        TODO("Not yet implemented")
    }

    private fun hideProgressBar() {
        TODO("Not yet implemented")
    }

    private fun showProgressBar() {
        TODO("Not yet implemented")
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}