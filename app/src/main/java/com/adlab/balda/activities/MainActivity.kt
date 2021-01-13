package com.adlab.balda.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adlab.balda.R
import com.adlab.balda.contracts.MainScreenContract
import com.adlab.balda.databinding.ActivityMainBinding
import com.adlab.balda.enums.GameType
import com.adlab.balda.utils.PresenterManager

class MainActivity: AppCompatActivity(), MainScreenContract.View {

    private lateinit var mPresenter: MainScreenContract.Presenter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        binding.bOneManGame.setOnClickListener { mPresenter.startOneManGameClicked() }
        binding.bGameWithAndroid.setOnClickListener {
            Toast.makeText(this, "Sorry, but game with Android isn't available yet", Toast.LENGTH_SHORT).show()
        }
        binding.bGameWithFriends.setOnClickListener { mPresenter.startMultiplayerGameClicked() }
        binding.bSettings.setOnClickListener { mPresenter.settingsClicked() }

        PresenterManager.provideMainScreenPresenter(this)
        mPresenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.resetView()
        if (isFinishing) {
            mPresenter.cleanup()
            PresenterManager.resetMainScreenPresenter()
        }
    }

    override fun setPresenter(presenter: MainScreenContract.Presenter) {
        mPresenter = presenter
    }

    override fun showInitiation() {
        binding.initRoot.root.visibility = View.VISIBLE
        binding.bOneManGame.visibility = View.GONE
        binding.bGameWithAndroid.visibility = View.GONE
        binding.bGameWithFriends.visibility = View.GONE
        binding.bSettings.visibility = View.GONE
    }

    override fun showMainButtons() {
        binding.initRoot.root.visibility = View.GONE
        binding.bOneManGame.visibility = View.VISIBLE
        binding.bGameWithAndroid.visibility = View.VISIBLE
        binding.bGameWithFriends.visibility = View.VISIBLE
        binding.bSettings.visibility = View.VISIBLE
    }

    override fun navigateToGameSettings(gameType: GameType) {
        startActivity(GameSettingsActivity.createIntent(this, gameType))
    }

    override fun navigateToSettings() {
        startActivity(RulesActivity.createIntent(this))
    }
}