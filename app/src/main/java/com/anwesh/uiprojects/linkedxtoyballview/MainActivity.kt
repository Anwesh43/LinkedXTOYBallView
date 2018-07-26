package com.anwesh.uiprojects.linkedxtoyballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.xtoyview.LinkedXToYBallView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LinkedXToYBallView = LinkedXToYBallView.create(this)
        fullScreen()
        view.addOnCompletionListener({i -> createCompleteToast(i)},{i -> createResetToast(i)})
    }

    private fun createCompleteToast(i : Int) {
        createToast("animation number ${i} is completed")
    }

    private fun createResetToast(i : Int) {
        createToast("animation number ${i} is reset")
    }

    private fun createToast(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}