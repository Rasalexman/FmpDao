package pro.krit.fmpdaoexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pro.krit.generated.database.MainDatabaseImpl

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //MainDatabaseImpl().create()
    }
}