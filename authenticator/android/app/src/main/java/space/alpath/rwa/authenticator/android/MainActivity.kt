package space.alpath.rwa.authenticator.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import webauthnkit.core.authenticator.internal.ui.UserConsentUI
import webauthnkit.core.authenticator.internal.ui.UserConsentUIFactory

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
class MainActivity : AppCompatActivity() {

    var consentUI: UserConsentUI? = null
    var deepLinkUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        consentUI = UserConsentUIFactory.create(this)

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (intent.action == "android.intent.action.VIEW" && intent.data != null) {
            deepLinkUrl = intent.data.toString().removePrefix("rwa:#")
        }
    }
}
