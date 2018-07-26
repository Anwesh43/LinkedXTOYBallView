package com.anwesh.uiprojects.xtoyview

/**
 * Created by anweshmishra on 26/07/18.
 */

import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val NODES : Int = 5

fun Canvas.drawXTOYBallNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val xGap : Float = (w / 2) / NODES
    val yGap : Float = (h / 2) / NODES
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    val r : Float = Math.min(xGap, yGap)/4
    val gap : Float = w / NODES
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#4CAF50")
    drawLine(i * gap, 0.9f * h, i * gap + gap * scale, 0.9f * h, paint)
    paint.color = Color.parseColor("#e74c3c")
    save()
    translate(w / 2 - r + (w / 2 - i * xGap) * sc2, h / 2 + r - (i + 1) * yGap * (1 - sc1))
    drawCircle(0f, 0f, r, paint)
    restore()
}

class LinkedXToYBallView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    var onAnimationCompleteListener : OnAnimationCompleteListener ?= null

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    fun addOnCompletionListener(onComplete : (Int) -> Unit, onReset : (Int) -> Unit) {
        onAnimationCompleteListener = OnAnimationCompleteListener(onComplete, onReset)
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

    data class XTOYBallNode(var i : Int, val state : State = State()) {

        private var next : XTOYBallNode? = null

        private var prev : XTOYBallNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < NODES - 1) {
                next = XTOYBallNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawXTOYBallNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : XTOYBallNode {
            var curr : XTOYBallNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedXTOYBall(var i : Int) {

        private var root : XTOYBallNode = XTOYBallNode(0)

        private var curr : XTOYBallNode = root

        private var dir : Int = 1


        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            curr.update {i, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(i, scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedXToYBallView) {

        private val linkedXTOYBall : LinkedXTOYBall = LinkedXTOYBall(0)

        private var animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            linkedXTOYBall.draw(canvas, paint)
            animator.animate {
                linkedXTOYBall.update {i, scale ->
                    animator.stop()
                    when (scale) {
                        0f -> view.onAnimationCompleteListener?.onReset?.invoke(i)
                        1f -> view.onAnimationCompleteListener?.onComplete?.invoke(i)
                    }
                }
            }
        }

        fun handleTap() {
            linkedXTOYBall.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : LinkedXToYBallView {
            val view : LinkedXToYBallView = LinkedXToYBallView(activity)
            activity.setContentView(view)
            return view
        }
    }

    data class OnAnimationCompleteListener(var onComplete : (Int) -> Unit, var onReset : (Int) -> Unit)
}