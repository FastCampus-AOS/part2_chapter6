package fastcampus.aos.part2.part2_chapter6

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val currentUser = Firebase.auth.currentUser

        if (currentUser == null) {
            // 로그인이 되어 있지 않은 경우
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}