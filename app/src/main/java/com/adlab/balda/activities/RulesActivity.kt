package com.adlab.balda.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.*
import com.adlab.balda.R
import com.adlab.balda.databinding.ActivityRulesBinding
import com.adlab.balda.databinding.ItemRuleBinding
import com.adlab.balda.model.Rule
import com.adlab.balda.widgets.FadeOutPageTransformer
import com.adlab.balda.widgets.ZoomOutPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.item_rule.view.*

class RulesActivity: AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, RulesActivity::class.java)
    }

    private lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rules)
        supportActionBar?.title = getString(R.string.title_rules)
        initRules()

        binding.viewPagerRules.adapter = ViewPagerAdapter(rules)

        TabLayoutMediator(binding.dotsRules, binding.viewPagerRules) { _, _ -> }.attach()

        binding.viewPagerRules.registerOnPageChangeCallback(onPageChangeCallback)
        binding.viewPagerRules.setPageTransformer(ZoomOutPageTransformer())

        updateButtonsState()

        binding.buttonNext.setOnClickListener {
            when (binding.viewPagerRules.currentItem != rules.size - 1) {
                true -> onNextClicked()
                false -> onDoneClicked()
            }
        }

        binding.buttonPrev.setOnClickListener { onPreviousClicked() }
    }

    private fun onPreviousClicked() {
        binding.viewPagerRules.apply { currentItem -= 1 }
        updateButtonsState()
    }

    private fun onNextClicked() {
        binding.viewPagerRules.apply {
            currentItem += 1
        }
        updateButtonsState()
    }

    private fun onDoneClicked() {
        finish()
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateButtonsState()
        }
    }

    private fun updateButtonsState() = with(binding) {
        val buttonNextStrRes = if (isCurrentItemLast()) R.string.done else R.string.next
        buttonNext.text = getString(buttonNextStrRes)
        buttonPrev.visibility = if (viewPagerRules.currentItem > 0) VISIBLE else INVISIBLE
    }

    private fun isCurrentItemLast() = binding.viewPagerRules.currentItem == rules.size - 1

    private lateinit var rules: List<Rule>
    private fun initRules() {
        rules = listOf(
                Rule(getString(R.string.title1), getString(R.string.description1), R.drawable.im_rule1),
                Rule(getString(R.string.title2), getString(R.string.description2), R.drawable.im_rule2),
                Rule(getString(R.string.title3), getString(R.string.description3), R.drawable.im_rule3),
                Rule(getString(R.string.title4), getString(R.string.description4), R.drawable.im_rule3),
                Rule(getString(R.string.title5), getString(R.string.description5), R.drawable.im_rule3),
                Rule(getString(R.string.title6), getString(R.string.description6), R.drawable.im_rule3),
                Rule(getString(R.string.title7), getString(R.string.description7), R.drawable.im_rule3),
        )
    }


    class ViewPagerAdapter(private val items: List<Rule>): RecyclerView.Adapter<ViewPagerAdapter.PagerVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
                PagerVH(ItemRuleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: PagerVH, position: Int) {
            holder.bindRule(items[position])
        }

        override fun getItemCount(): Int = items.size

        class PagerVH(private val binding: ItemRuleBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bindRule(value: Rule) = with(binding) {
                rule = value
                executePendingBindings()
            }
        }
    }

}