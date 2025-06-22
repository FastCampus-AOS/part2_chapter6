package fastcampus.aos.part2.part2_chapter6.chatlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aos.part2.part2_chapter6.Key.Companion.DB_CHAT_ROOMS
import fastcampus.aos.part2.part2_chapter6.R
import fastcampus.aos.part2.part2_chapter6.databinding.FragmentChatlistBinding

class ChatRoomListFragment : Fragment(R.layout.fragment_chatlist) {

    private lateinit var binding: FragmentChatlistBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatlistBinding.bind(view)

        val chatRoomListAdapter = ChatRoomAdapter()
        binding.chatRoomListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatRoomListAdapter
        }

        val currentUserId = Firebase.auth.currentUser?.uid ?: return
        val chatRoomsDB = Firebase.database.reference.child(DB_CHAT_ROOMS).child(currentUserId)
        chatRoomsDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRoomList = snapshot.children.map {
                    it.getValue(ChatRoomItem::class.java)
                }

                chatRoomListAdapter.submitList(chatRoomList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}