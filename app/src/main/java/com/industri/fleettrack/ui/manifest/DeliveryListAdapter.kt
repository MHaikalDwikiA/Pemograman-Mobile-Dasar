package com.industri.fleettrack.ui.manifest

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.databinding.ItemDeliveryOrderBinding
import java.text.NumberFormat
import java.util.Locale

class DeliveryListAdapter(
    private val onDeliverClicked: (DeliveryOrderEntity) -> Unit
) : ListAdapter<DeliveryOrderEntity, DeliveryListAdapter.OrderViewHolder>(OrderDiffCallback()) {

    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))

    class OrderViewHolder(val binding: ItemDeliveryOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemDeliveryOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTrackingNo.text = item.trackingNumber
            tvRecipient.text = "${item.recipientName} (${item.recipientPhone})"
            tvAddress.text = item.destinationAddress
            tvCodAmount.text = if (item.codAmount > 0) rupiahFormat.format(item.codAmount) else "Non-COD"

            // Setup custom shape for badge
            val badgeDrawable = GradientDrawable()
            badgeDrawable.cornerRadius = 12f * holder.itemView.context.resources.displayMetrics.density

            tvStatusBadge.text = item.deliveryStatus
            when (item.deliveryStatus) {
                "DELIVERED" -> {
                    badgeDrawable.setColor(Color.parseColor("#DCFCE7"))
                    tvStatusBadge.background = badgeDrawable
                    tvStatusBadge.setTextColor(Color.parseColor("#15803D"))
                    
                    btnActionDeliver.visibility = View.GONE
                }
                "FAILED" -> {
                    badgeDrawable.setColor(Color.parseColor("#FEE2E2"))
                    tvStatusBadge.background = badgeDrawable
                    tvStatusBadge.setTextColor(Color.parseColor("#B91C1C"))
                    
                    btnActionDeliver.visibility = View.GONE
                }
                "IN_DELIVERY" -> {
                    badgeDrawable.setColor(Color.parseColor("#DBEAFE"))
                    tvStatusBadge.background = badgeDrawable
                    tvStatusBadge.setTextColor(Color.parseColor("#1D4ED8"))
                    
                    btnActionDeliver.visibility = View.VISIBLE
                    btnActionDeliver.text = "Tandai Terkirim"
                    btnActionDeliver.isEnabled = true
                }
                else -> { // PENDING
                    badgeDrawable.setColor(Color.parseColor("#FEF3C7"))
                    tvStatusBadge.background = badgeDrawable
                    tvStatusBadge.setTextColor(Color.parseColor("#B45309"))
                    
                    btnActionDeliver.visibility = View.VISIBLE
                    btnActionDeliver.text = "Tandai Terkirim"
                    btnActionDeliver.isEnabled = true
                }
            }

            btnActionDeliver.setOnClickListener {
                onDeliverClicked(item)
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<DeliveryOrderEntity>() {
        override fun areItemsTheSame(oldItem: DeliveryOrderEntity, newItem: DeliveryOrderEntity): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: DeliveryOrderEntity, newItem: DeliveryOrderEntity): Boolean {
            return oldItem == newItem
        }
    }
}
