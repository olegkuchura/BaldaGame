package com.adlab.balda.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adlab.balda.R
import com.adlab.balda.databinding.ActivityRulesBinding

class RulesActivity: AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, RulesActivity::class.java)
    }

    private lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rules)
        supportActionBar?.title = getString(R.string.title_rules)
    }

}