package com.uvk.shramapplication.ui.profile.personal_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val profileName = MutableLiveData<String>()
    val profileImage = MutableLiveData<String>() // Store Base64 or Image URL
}
