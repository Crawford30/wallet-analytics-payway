package com.example.mobilewalletanalytics.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilewalletanalytics.R
import com.example.mobilewalletanalytics.databinding.FragmentHomeBinding
import com.example.mobilewalletanalytics.databinding.FragmentTransactionsListBinding
import com.example.mobilewalletanalytics.presentation.adapters.DailyTransactionsAdapter
import com.example.mobilewalletanalytics.presentation.adapters.TransactionDashboardAdapter
import com.example.mobilewalletanalytics.presentation.adapters.TransactionHistoryAdapter
import com.example.mobilewalletanalytics.presentation.viewmodels.AppViewModel
import com.example.mobilewalletanalytics.utils.TopSpacingItemDecoration
import com.example.mobilewalletanalytics.utils.formatNumberToThousands
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val appViewModel: AppViewModel by activityViewModels()
    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: TransactionDashboardAdapter
    private lateinit var dailyStatAdapter: DailyTransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        dailyStatAdapter = DailyTransactionsAdapter(binding!!.progressBar) {
        }
        adapter = TransactionDashboardAdapter(binding!!.progressBar) {
        }

        binding?.transactionDashboardRecycler?.adapter = adapter
        binding?.dailyDashboardRecycler?.adapter = dailyStatAdapter
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Dashboard Statistics"

        binding?.transactionDashboardRecycler?.apply {
            layoutManager = LinearLayoutManager(activity)
            val topSPacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSPacingDecoration)
        }

        binding?.dailyDashboardRecycler?.apply {
            layoutManager = LinearLayoutManager(activity)
            val topSPacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSPacingDecoration)
        }
        /**
         * Call the method to for the dashboard statistics from the [AppViewModel]
         */
        viewLifecycleOwner.lifecycleScope.launch {
            appViewModel.dashboardLiveData.observe(viewLifecycleOwner) {
//                println("DASHBOARD: ${it}")
                val balance = it.total_deposits.toLong() - it.total_withdrawals.toLong()
                binding?.balanceTextView?.text = "${formatNumberToThousands((balance))} UGX"
                adapter.submitList(it.category_breakdown)
                dailyStatAdapter.submitList(it.daily_statistics)

                print("DAILY STAT: ${it.daily_statistics}")

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}