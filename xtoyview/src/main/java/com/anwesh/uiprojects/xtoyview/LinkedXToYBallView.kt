package com.anwesh.uiprojects.xtoyview

/**
 * Created by anweshmishra on 26/07/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val NODES : Int = 5

fun Canvas.drawXTOYBallNode(i : Int, scale : Float, paint : Paint) {
    paint.color = Color.parseColor("#e74c3c")
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val xGap : Float = (w / 2) / NODES
    val yGap : Float = (h / 2) / NODES
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    save()
    translate(w / 2 + (i + 1) * xGap * sc2, h / 2 - (i + 1) * yGap * sc1)
    drawCircle(0f, 0f, Math.min(xGap, yGap)/8, paint)
    restore()
}

class LinkedXToYBallView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }
    }
}