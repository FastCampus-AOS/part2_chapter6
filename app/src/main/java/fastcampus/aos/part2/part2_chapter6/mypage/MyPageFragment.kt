package fastcampus.aos.part2.part2_chapter6.mypage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aos.part2.part2_chapter6.Key.Companion.DB_USERS
import fastcampus.aos.part2.part2_chapter6.LoginActivity
import fastcampus.aos.part2.part2_chapter6.R
import fastcampus.aos.part2.part2_chapter6.databinding.FragmentMypageBinding
import fastcampus.aos.part2.part2_chapter6.userlist.UserItem


class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private lateinit var binding: FragmentMypageBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMypageBinding.bind(view)

        val currentUserId = Firebase.auth.currentUser?.uid ?: ""
        val currentUserDB = Firebase.database.reference.child(DB_USERS).child(currentUserId)

        currentUserDB.get().addOnSuccessListener {
            val currentUserItem = it.getValue(UserItem::class.java)

            binding.userNameEditText.setText(currentUserItem?.username)
            binding.descriptionEditText.setText(currentUserItem?.description)

            currentUserItem?.username
            currentUserItem?.description
        }

        binding.applyButton.setOnClickListener {
            val userName = binding.userNameEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()

            if (userName.isEmpty()) {
                Toast.makeText(context, "유저이름은 빈 값으로 두실 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // todo Firebase Realtime Database Update


            val user = mutableMapOf<String, Any>()
            user["username"] = userName
            user["description"] = description
            currentUserDB.updateChildren(user)
        }

        binding.signOutButton.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    }

}