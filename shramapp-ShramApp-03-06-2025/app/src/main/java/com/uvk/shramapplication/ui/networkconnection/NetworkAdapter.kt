package com.uvk.shramapplication.ui.networkconnection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkAdapter(
    val context: Context,
    private val networkList: List<NetworkData>,
    val sendRequest: (String,String,String,Int) -> Unit,
    val cancleRequest: (String,String) -> Unit,
) : RecyclerView.Adapter<NetworkAdapter.NetworkViewHolder>() {

    private var bottomSheetDialog: BottomSheetDialog? = null

    inner class NetworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: CircleImageView = itemView.findViewById(R.id.imgProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
       // val tvInstitution: TextView = itemView.findViewById(R.id.tvInstitution)
        val btnConnect: Button = itemView.findViewById(R.id.btnConnect)
//        val btnClose: ImageView = itemView.findViewById(R.id.btnClose)
    val shimmerLayout = itemView.findViewById<ShimmerFrameLayout>(R.id.shimmerLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_network, parent, false)
        return NetworkViewHolder(view)
    }

    override fun onBindViewHolder(holder: NetworkViewHolder, position: Int) {
        val networkData = networkList[position]
        TranslationHelper.initialize(context)

        CoroutineScope(Dispatchers.IO).launch {
            val targetLan = context.languageName
            val name = TranslationHelper.translateText(networkData.name ?: "", targetLan)
            val designation = TranslationHelper.translateText(networkData.designation ?: "", targetLan)

            withContext(Dispatchers.Main) {
                holder.tvName.text = name
                holder.tvDescription.text = designation
            }
        }

        // Load profile image
            Glide.with(holder.itemView.context)
                .load(networkData.profile_image)
                .placeholder(R.drawable.user)
                .into(holder.imgProfile)


            Log.e("tag", "Status :${networkData.request_status}")
            if (networkData.request_status.equals("pending")) {
                holder.btnConnect.text = "Pending"
                holder.btnConnect.setTextColor(ContextCompat.getColor(context, R.color.black))
                // Disable the button so it becomes unclickable
                holder.btnConnect.isEnabled = true
            } else if (networkData.request_status.equals("confirm")) {
                holder.btnConnect.text = "Confirm"
                holder.btnConnect.setTextColor(ContextCompat.getColor(context, R.color.black))
                // Disable the button so it becomes unclickable
                holder.btnConnect.isEnabled = false
            } else if (networkData.request_status.equals("rejected")) {
                holder.btnConnect.text = "Rejected"
                holder.btnConnect.setTextColor(ContextCompat.getColor(context, R.color.black))
                // Disable the button so it becomes unclickable
                holder.btnConnect.isEnabled = false
            } else if (networkData.request_status.isNullOrEmpty()) {
                holder.btnConnect.text = "Connect"
            } else if (networkData.request_status.equals("not_connected")) {
                holder.btnConnect.text = "Connect"
                holder.btnConnect.setTextColor(ContextCompat.getColor(context, R.color.white))
                // Disable the button so it becomes unclickable
                holder.btnConnect.isEnabled = true
            } else {
                holder.btnConnect.visibility = View.GONE
            }

            // Handle button clicks
        holder.btnConnect.setOnClickListener {
            if (networkData.request_status.equals("pending")) {
                showBottomSheetCancleDialog(context.userid, networkData.network_id)
            } else {
                sendRequest(context.token, context.userid, networkData.id, position) // Pass position here
            }
        }

        /* holder.btnConnect.setOnClickListener {

             if (networkData.request_status.equals("pending")) {
                 // cancleRequest(context.userid, networkData.id)
                 Log.e("tag", "Cancle request : ${context.userid} ${networkData.network_id}")
                 showBottomSheetCancleDialog(context.userid, networkData.network_id)
             } else {
                 sendRequest(context.token, context.userid, networkData.id)
             }
         }*/

            holder.itemView.setOnClickListener {

                if (context.isuserlgin) {
                    // context.startActivity(Intent(context,NetworkDetailsActivity::class.java))
                    val intent = Intent(context, NetworkDetailsActivity::class.java).apply {
                        putExtra("net_id", networkData.id)
                        putExtra("network", "No Message")
                    }

                    context.startActivity(intent)

                } else {
                    Toast.makeText(context, "Please Login App", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as Activity).finish()
                }
            }

    }

    private fun showBottomSheetCancleDialog(userId: String, networkId: String) {
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
