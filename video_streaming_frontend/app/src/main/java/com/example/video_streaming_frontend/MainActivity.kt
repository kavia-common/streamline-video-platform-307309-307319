package com.example.video_streaming_frontend

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.example.video_streaming_frontend.ui.browse.VideoBrowseFragment

/**
 * Main Activity for Android TV.
 *
 * Hosts the app's fragment navigation (Browse -> Details).
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // On first launch, show the browse screen. Subsequent recreations (rotation/process death)
        // are handled by the FragmentManager state restore.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, VideoBrowseFragment(), VideoBrowseFragment.TAG)
                .commit()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Handle TV remote BACK: pop details -> browse and allow browse to regain focus.
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }
}
