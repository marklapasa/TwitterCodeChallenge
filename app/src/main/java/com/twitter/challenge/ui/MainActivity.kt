package com.twitter.challenge.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.cycler.Recycler
import com.twitter.challenge.R
import com.twitter.challenge.databinding.ActivityMainBinding
import com.twitter.challenge.databinding.ViewWeatherRecordItemBinding
import com.twitter.challenge.model.WeatherRecord
import com.twitter.challenge.util.Units
import com.twitter.challenge.util.WeatherDataFormatter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainViewModel by inject()
    private lateinit var cycler: Recycler<WeatherRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbar)
        }
        setContentView(binding.root)

        lifecycleScope.launch {
            configRecycler()
            configViewModel()
        }
    }

    /**
     * Configure the recycler view
     */
    private fun configRecycler() {
        cycler = Recycler.adopt(binding.recyclerView) {
            row<WeatherRecord, ConstraintLayout> {
                create(R.layout.view_weather_record_item) {
                    bind { _, item ->
                        DataBindingUtil.bind<ViewWeatherRecordItemBinding>(view)?.apply {
                            this.record = item
                        }

                    }
                }
            }
        }
    }

    /**
     * Observe the current state dictated by the View Model
     */
    private suspend fun configViewModel() {

        viewModel.uiState.collect { uiState ->
            when(uiState) {
                is UIState.Refreshed -> {
                    isUIEnabled(true)
                    refreshList(uiState.list)

                    // Set the action bar and subtitle
                    title = uiState.list.firstOrNull()?.name
                    supportActionBar?.subtitle = viewModel.getStdDevStr()
                }
                is UIState.Error -> {
                    Snackbar.make(binding.root, uiState.msg, Snackbar.LENGTH_LONG).show()
                }
                is UIState.Loading -> {
                    isUIEnabled(false)
                    binding.emptyView.visibility = View.GONE
                }
                is UIState.Empty -> {
                    binding.emptyView.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Populate the recyclerview with the provided list of Weather Record objects
     */
    private fun refreshList(list: List<WeatherRecord>) {
        cycler.update {
            data = list
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.refresh -> {
                lifecycleScope.launch {
                    if(isNetworkConnected()) {
                        viewModel.refresh(true)
                    } else {
                        showInternetConnectionAlert()
                    }
                }
                true
            }
            R.id.toggleUnits -> {
                launchToggleUnits()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showInternetConnectionAlert() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(getString(R.string.internet_conn_alert))
            .setMessage(getString(R.string.please_check_your_internet))
            .setPositiveButton(
                getString(R.string.close)
            ) { _, _ -> finish() }.show()
    }

    /**
     * Prompt the user to specify Imperial or Metric units
     */
    private fun launchToggleUnits() {
        val listItems = arrayOf(Units.IMPERIAL.choiceLabel, Units.METRIC.choiceLabel)
        val selectedUnits = if (WeatherDataFormatter.curUnit == Units.IMPERIAL) 0 else 1
        AlertDialog.Builder(this).setTitle(getString(R.string.choose_unit))
            .setSingleChoiceItems(listItems, selectedUnits) { dialog, i ->
                WeatherDataFormatter.curUnit = if (i == 0) Units.IMPERIAL else Units.METRIC
                GlobalScope.launch {
                    viewModel.refresh(true)
                }
                dialog.dismiss()
            }.create().show()
    }


    /**
     * When true, hide the progress indicator
     */
    private fun isUIEnabled(isEnabled : Boolean) {
        with(binding) {
            progressBar.visibility = if (isEnabled) View.GONE else View.VISIBLE

        }
    }

    @SuppressLint("NewApi")
    private fun isNetworkConnected(): Boolean {

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
