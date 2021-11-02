// 리사이클러 뷰 어댑터 클래스
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.heallo.ListLayout
import com.example.heallo.databinding.ListLayoutBinding

class ListAdapter(val itemList: ArrayList<ListLayout>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val binding = ListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = binding.root

        return ViewHolder(binding)
    }

    class ViewHolder(binding: ListLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        val name: TextView = binding.tvListName
        val road: TextView = binding.tvListRoad
        val address: TextView = binding.tvListAddress
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.road.text = itemList[position].road
        holder.address.text = itemList[position].address
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }



    private lateinit var itemClickListener : OnItemClickListener
}