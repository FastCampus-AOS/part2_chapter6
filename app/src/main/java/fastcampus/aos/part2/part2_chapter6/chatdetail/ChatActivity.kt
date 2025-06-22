package fastcampus.aos.part2.part2_chapter6.chatdetail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aos.part2.part2_chapter6.Key.Companion.DB_CHATS
import fastcampus.aos.part2.part2_chapter6.Key.Companion.DB_CHAT_ROOMS
import fastcampus.aos.part2.part2_chapter6.Key.Companion.DB_USERS
import fastcampus.aos.part2.part2_chapter6.Key.Companion.FCM_SERVER_KEY
import fastcampus.aos.part2.part2_chapter6.R
import fastcampus.aos.part2.part2_chapter6.databinding.ActivityChatBinding
import fastcampus.aos.part2.part2_chapter6.userlist.UserItem
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ChatActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter

    private var chatRoomId: String = ""
    private var otherUserId: String = ""
    private var otherUserFcmToken: String = ""
    private var myUserId: String = ""
    private var myUserName: String = ""
    private var isInit: Boolean = false


    private val chatItemList = mutableListOf<ChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatAdapter = ChatAdapter()

        chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID) ?: return
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: return
        myUserId = Firebase.auth.currentUser?.uid ?: ""

        Firebase.database.reference.child(DB_USERS).child(myUserId).get()
            .addOnSuccessListener {
                val myUserItem = it.getValue(UserItem::class.java)
                myUserName = myUserItem?.username ?: ""

                getOtherUserData()
            }

        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        binding.sendButton.setOnClickListener {

            if (isInit == false) return@setOnClickListener

            if (myUserName.isEmpty()) {
                Toast.makeText(applicationContext, "아직 사용자 정보 로딩 중 입니다.", Toast.LENGTH_SHORT).show()
                Log.d("ChatActivity", "myUserName is empty")
                return@setOnClickListener
            }

            val message = binding.messageEditText.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "빈 메시지를 전송할 수는 없습니다.", Toast.LENGTH_SHORT).show()
                Log.d("ChatActivity", "message is empty")
                return@setOnClickListener
            }

            Log.d("ChatActivity", "메시지 전송 시도 : $message")

            val newChatRef = Firebase.database.reference.child(DB_CHATS).child(chatRoomId).push()

            val newChatItem = ChatItem(
                chatId = newChatRef.key,
                message = message,
                userId = myUserId
            )
            newChatRef.setValue(newChatItem)

            val updates: MutableMap<String, Any> = hashMapOf(
                "${DB_CHAT_ROOMS}/${myUserId}/$otherUserId/lastMessage" to message,
                "${DB_CHAT_ROOMS}/${otherUserId}/$myUserId/lastMessage" to message,
                "${DB_CHAT_ROOMS}/${otherUserId}/$myUserId/chatRoomId" to chatRoomId,
                "${DB_CHAT_ROOMS}/${otherUserId}/$myUserId/otherUserId" to myUserId,
                "${DB_CHAT_ROOMS}/${otherUserId}/$myUserId/otherUserName" to myUserName,
            )

            Firebase.database.reference.updateChildren(updates)

            val client = OkHttpClient()

            val root = JSONObject()
            val notification = JSONObject()
            notification.put("title", getString(R.string.app_name))
            notification.put("body", message)
            root.put("to", otherUserFcmToken)
            root.put("priority", "high")
            root.put("notification", notification)

            val requestBody = root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .post(requestBody)
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", "key=$FCM_SERVER_KEY").build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                override fun onResponse(call: Call, response: Response) {}
            })

            binding.messageEditText.text.clear()
        }

        Log.d("ChatActivity", "chatRoomId = $chatRoomId, otherUserId = $otherUserId, myUserId = $myUserId")
    }

    private fun getOtherUserData() {
        Firebase.database.reference.child(DB_USERS).child(otherUserId).get()
            .addOnSuccessListener {
                val otherUserItem = it.getValue(UserItem::class.java)
                otherUserFcmToken = otherUserItem?.fcmToken.orEmpty()
                chatAdapter.otherUserItem = otherUserItem

                isInit = true
                getChatData()
            }
    }

    private fun getChatData() {
        Firebase.database.reference.child(DB_CHATS).child(chatRoomId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatItem = snapshot.getValue(ChatItem::class.java)
                    chatItem ?: return

                    chatItemList.add(chatItem)
                    chatAdapter.submitList(chatItemList.toList())
                    binding.chatRecyclerView.scrollToPosition(chatItemList.size - 1)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    companion object {
        const val EXTRA_CHAT_ROOM_ID = "CHAT_ROOM_ID"
        const val EXTRA_OTHER_USER_ID = "OTHER_USER_ID"

    }
}