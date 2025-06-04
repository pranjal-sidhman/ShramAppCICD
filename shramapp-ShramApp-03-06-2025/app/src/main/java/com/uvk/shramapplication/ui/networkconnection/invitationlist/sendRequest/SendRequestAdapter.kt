package com.uvk.shramapplication.ui.networkconnection.invitationlist.sendRequest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.networkconnection.NetworkData
import com.uvk.shramapplication.ui.networkconnection.NetworkDetailsActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendRequestAdapter (
    val context: Context,
    private val networkList: List<NetworkData> ,
    val cancleRequest: (String,String) -> Unit,
) : RecyclerView.Adapter<SendRequestAdapter.SendRequestViewHolder>() {

    private var bottomSheetDialog: BottomSheetDialog? = null

    inner class SendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDesc)
        val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)
        val btnCancel: Button = itemView.findViewById(R.id.btnMessage)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SendRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_followers, parent, false)
        return SendRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: SendRequestViewHolder, position: Int) {
        val networkData = networkList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvName.text =
                TranslationHelper.translateText(networkData.name ?: "", targetLan)
        }
        Log.d("MyConnectionAdapter", "Binding data for: ${networkData.connected_user_name}")

        holder.btnRemove.visibility = View.GONE
        holder.btnCancel.text = "Cancel"

        holder.btnRemove.setOnClickListener {
           // removeConnection(networkData.network_connection_id,networkData.connected_user_name)
        }

        holder.btnCancel.setOnClickListener {
            showBottomSheetCancleDialog(context.userid, networkData.network_id)
        }

        if(!networkData.designation.isNullOrEmpty()){

            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                holder.tvDescription.text =
                    TranslationHelper.translateText(networkData.designation ?: "", targetLan)
            }
        }else{

            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                holder.tvDescription.text =
                    TranslationHelper.translateText(networkData.company_name ?: "", targetLan)
            }
        }


        // Load profile image
        Glide.with(holder.itemView.context)
            .load(networkData.profile_image)
            .placeholder(R.drawable.user)
            .into(holder.imgProfile)


        Log.e("tag","Status :${networkData.request_status}")


        holder.itemView.setOnClickListener {

            if (context.isuserlgin) {
                val intent = Intent(context, NetworkDetailsActivity::class.java).apply {
                    putExtra("net_id", networkData.id)
                    putExtra("network", "No Message")
                }

                context.startActivity(intent)

            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
        }

    }

    private fun showBottomSheetCancleDialog(userId: String, networkId: String) {
       //val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_delete_comment, null)

        bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_delete_comment, null)

        bottomSheetDialog?.setContentView(dialogView)


        val deleteComment = dialogView.findViewById<LinearLayout>(R.id.llDeleteComment)
        val tvMsg = dialogView.findViewById<TextView>(R.id.tvMsg)
        tvMsg!!.text = "Cancel request"

        deleteComment!!.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            // builder.setTitle("Call Confirmation")
            builder.setMessage(context.getString(R.string.cancele_request_msg))

            builder.setPositiveButton(R.string.yes) { dialog, _ ->
                Log.e("comment", "id : $networkId")
                cancleRequest(userId, networkId)
                dialog.dismiss()
                bottomSheetDialog?.dismiss()
            }

            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
                bottomSheetDialog?.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        // Show the bottom sheet dialog
        bottomSheetDialog?.show()
    }

    override fun getItemCount() = networkList.size
}
