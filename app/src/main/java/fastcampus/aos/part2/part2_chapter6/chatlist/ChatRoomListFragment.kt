package fastcampus.aos.part2.part2_chapter6.chatlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
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

        chatRoomListAdapter.submitList(
            mutableListOf<ChatRoomItem>().apply {
                add(ChatRoomItem("1", "otherUserName_test", "lastMessage_test"))
            }
        )
    }
}