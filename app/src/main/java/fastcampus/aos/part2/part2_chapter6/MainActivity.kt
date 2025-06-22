package fastcampus.aos.part2.part2_chapter6

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fastcampus.aos.part2.part2_chapter6.chatlist.ChatRoomListFragment
import fastcampus.aos.part2.part2_chapter6.databinding.ActivityMainBinding
import fastcampus.aos.part2.part2_chapter6.mypage.MyPageFragment
import fastcampus.aos.part2.part2_chapter6.userlist.UserFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userFragment = UserFragment()
    private val chatRoomFragment = ChatRoomListFragment()
    private val myPageFragment = MyPageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = Firebase.auth.currentUser

        if (currentUser == null) {
            // 로그인이 되어 있지 않은 경우
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

//        askNotificationPermission()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.userList -> {
                    replaceFragment(userFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.chatRoomList -> {
                    replaceFragment(chatRoomFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.myPage -> {
                    replaceFragment(myPageFragment)
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }
        replaceFragment(userFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {

        }
    }

//    private fun askNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                showPermissionRationalDialog()
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun showPermissionRationalDialog() {
//        AlertDialog.Builder(this)
//            .setMessage("알림 권한이 없으면 알림을 받을 수 없습니다.")
//            .setPositiveButton("권한 허용하기") { _, _ ->
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
//    }
}