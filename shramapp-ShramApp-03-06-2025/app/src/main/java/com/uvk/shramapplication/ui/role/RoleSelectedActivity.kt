package com.uvk.shramapplication.ui.role

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.databinding.ActivityRoleSelectedBinding
import com.mahindra.serviceengineer.savedata.AppRole
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.savedata.language.LanguagePref
import com.uvk.shramapplication.ui.lang.LanguageSelectionActivity

class RoleSelectedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoleSelectedBinding

    // Variables to store the selected option
    private var selectedCardName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoleSelectedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Log.e("RoleSelected", "language RoleSelected: $languageName")

        // Set click listeners for the radio buttons
        binding.jobCard.setOnClickListener { //onOptionSelected("Job")
            selectCard("Find Job")
            binding.btnNext.visibility = View.VISIBLE
        }
        binding.hireCard.setOnClickListener {
            selectCard("Hire Top Talent")
            binding.btnNext.visibility = View.VISIBLE
        }

        // Back button click listener
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, LanguageSelectionActivity::class.java))
        }

        // Next button click listener
        binding.btnNext.setOnClickListener {
            if (selectedCardName != null) {

                saveRole(selectedCardName!!)
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                Toast.makeText(this, R.string.select_card, Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun saveRole(selectedCardName: String) {
        AppRole = selectedCardName
        LanguagePref.setAppRole(this, AppRole)
        Log.e("RoleSelected", "Role : $AppRole")

    }

    // Function to handle card selection
    private fun selectCard(cardName: String) {
        selectedCardName = cardName

        // Reset card backgrounds
        binding.jobCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.hireCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // Highlight the selected card
        when (cardName) {
            "Find Job" -> {

                /*binding.jobCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.lightskyblue
                    )
                )*/
                binding.jobCard.setBackgroundResource(R.drawable.card_border_orange)
                binding.hireCard.setBackgroundResource(R.drawable.card_border)
                binding.ivFindJob.setBackgroundResource(R.drawable.bg_round_button_round)
                binding.ivHire.setBackgroundResource(R.drawable.icgraycircle)
                binding.ivFindIcon.setColorFilter(ContextCompat.getColor(this, R.color.white))
                binding.ivHireIcon.setColorFilter(ContextCompat.getColor(this, R.color.sub_text))
            }

            "Hire Top Talent" -> {
              /*  binding.hireCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.lightskyblue
                    )
                )*/
                binding.hireCard.setBackgroundResource(R.drawable.card_border_orange)
                binding.jobCard.setBackgroundResource(R.drawable.card_border)
                binding.ivFindJob.setBackgroundResource(R.drawable.icgraycircle)
                binding.ivHire.setBackgroundResource(R.drawable.bg_round_button_round)
                binding.ivFindIcon.setColorFilter(ContextCompat.getColor(this, R.color.sub_text))
                binding.ivHireIcon.setColorFilter(ContextCompat.getColor(this, R.color.white))
            }
        }
    }


}