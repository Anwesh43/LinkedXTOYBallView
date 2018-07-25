package com.anwesh.uiprojects.linkedxtoyballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.xtoyview.LinkedXToYBallView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedXToYBallView.create(this)
    }
}
