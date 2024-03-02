package com.example.weclean

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class Add : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add)

        // TODO Here the communities should be fetched and added to the list
        val communities = ArrayList<String>()
        communities.add("community 1")
        communities.add("community 2")
        communities.add("community 3")

        val selectCommunitySpinner = findViewById<Spinner>(R.id.select_community)
        val selectCommunityAdapter = ArrayAdapter<String>(
            this, R.layout.spinner_item, communities)

        selectCommunityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        selectCommunityAdapter.setNotifyOnChange(true)
        selectCommunitySpinner.adapter = selectCommunityAdapter

        val tagButton = findViewById<Button>(R.id.select_tags_button)
        val tagInput = findViewById<EditText>(R.id.select_tags_input)
        val tagChipGroup = findViewById<ChipGroup>(R.id.tag_chip_group)
        val tagColors = intArrayOf(
            Color.rgb(220, 0, 220),
            Color.rgb(0, 191, 220),
            Color.rgb(220, 69, 0),
            Color.rgb(30, 144, 220),
            Color.rgb(34, 139, 34),
            Color.rgb(176, 48, 96),
            Color.rgb(0, 220, 0),
            Color.rgb(220, 220, 0),
            Color.rgb(47, 79, 79))

        fun addTagChip(chipText: String) {
            val chip = Chip(this)

            val nTags = tagChipGroup.size

            chip.text = chipText
            chip.isCloseIconVisible = true
            chip.chipStrokeWidth = 0F
            chip.chipBackgroundColor = ColorStateList.valueOf(tagColors[nTags])
            chip.setTextColor(getColorStateList(R.color.white))
            chip.closeIconTint = getColorStateList(R.color.white)
            chip.textSize = 16F
            chip.chipIconSize = 16F
            chip.textStartPadding = 2F
            chip.textEndPadding = 3F
            chip.closeIconEndPadding = 4F
            chip.chipEndPadding = 4F
            chip.setEnsureMinTouchTargetSize(false)
            chip.setOnCloseIconClickListener {
                tagChipGroup.removeView(chip)
            }
            chip.setOnClickListener {
                tagChipGroup.removeView(chip)
            }
            tagChipGroup.addView(chip)
        }

        tagInput.setOnKeyListener {_, keyCode, event ->
            when {
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    val tagText = tagInput.text.toString();
                    if (tagText.isNotEmpty()) {
                        addTagChip(tagText)
                        tagInput.setText("")
                    }

                    return@setOnKeyListener true
                }
                else -> false
            }
        }

        tagButton.setOnClickListener {
            val tagText = tagInput.text.toString();
            if (tagText.isNotEmpty()) {
                addTagChip(tagText)
                tagInput.setText("")
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_add

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(applicationContext, Home::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    startActivity(Intent(applicationContext, Map::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> return@OnNavigationItemSelectedListener true
                R.id.navigation_achievements -> {
                    startActivity(Intent(applicationContext, Achievements::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(applicationContext, Profile::class.java))
                    overridePendingTransition(0,0);
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}