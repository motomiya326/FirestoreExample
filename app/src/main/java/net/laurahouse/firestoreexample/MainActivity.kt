package net.laurahouse.firestoreexample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import net.laurahouse.firestoreexample.data.FirestoreUseCase
import net.laurahouse.firestoreexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.buttonRoom.setOnClickListener {
            val nickName = binding.editText.text.toString()
            if (!TextUtils.isEmpty(nickName)) {
                startActivity(RoomActivity.createIntent(this, nickName))
            }
        }
        binding.buttonDelete.setOnClickListener {
            FirestoreUseCase.deleteAll()
        }
    }

}
