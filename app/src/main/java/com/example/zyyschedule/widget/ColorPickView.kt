package com.example.zyyschedule.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.zyyschedule.R
import java.util.*
import kotlin.math.*

class ColorPickView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var bigCircle: Int? = null //外圆半径
    private var rudeRadius: Int? = null //可移动小球的半径
    private var centerColor: Int? = null
    private var bitmapBack: Bitmap? = null //背景图片
    private var mPaint: Paint? = null //背景画笔
    private var mCenterPaint: Paint? = null //可移动小球背景
    private var centerPoint: Point? = null //中心位置
    private var mRockPosition: Point? = null //小球当前位置
    private var listener: OnColorChangedListener? = null //小球移动监听
    var colorStr = ""

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPickView, 0, 0)
                .apply {
                    try {
                        //外圆半径
                        bigCircle = getResourceId(R.styleable.ColorPickView_circle_radius, 100)
                        //可移动小球半径
                        rudeRadius = getResourceId(R.styleable.ColorPickView_center_radius, 10)
                        //可移动小球的颜色
                        centerColor = getResourceId(R.styleable.ColorPickView_center_color, Color.WHITE)
                    } finally {
                        recycle()
                    }
                }

        //中心位置坐标
        centerPoint = Point(bigCircle!!, bigCircle!!)
        mRockPosition = Point(centerPoint!!)

        //初始化背景画笔和可移动小球的画笔

        //初始化背景画笔和可移动小球的画笔
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true

        mCenterPaint = Paint()
        mCenterPaint!!.color = centerColor!!

        bitmapBack = createColorBitmap(bigCircle!! * 2, bigCircle!! * 2)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //画背景图
        canvas!!.drawBitmap(bitmapBack!!, 0f, 0f, null)
        //画中心小球
        canvas.drawCircle(mRockPosition!!.x.toFloat(), mRockPosition!!.y.toFloat(), rudeRadius!!.toFloat(), mCenterPaint!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val length: Int = getLength(event.x, event.y, centerPoint!!.x, centerPoint!!.y)
                    if (length > bigCircle!! - rudeRadius!!) {
                        return true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val length: Int = getLength(event.x, event.y, centerPoint!!.x, centerPoint!!.y)
                    if (length <= bigCircle!! - rudeRadius!!) {
                        mRockPosition!![event.x.toInt()] = event.y.toInt()
                    } else {
                        mRockPosition = getBorderPoint(centerPoint!!, Point(event.x.toInt(), event.y.toInt()), bigCircle!! - rudeRadius!!)
                    }
                }
                MotionEvent.ACTION_UP -> {
                }
            }
        }
        getRGB()
        invalidate()
        performClick()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //视图大小设置为直径
        setMeasuredDimension(bigCircle!! * 2, bigCircle!! * 2)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    internal fun setOnColorChangedListener(listener: OnColorChangedListener) {
        this.listener = listener
    }

    private fun createColorBitmap(width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val colorCount = 12
        val colorAngleStep = 360 / 12
        val colors = IntArray(colorCount + 1)
        val hsv = floatArrayOf(0f, 1f, 1f)
        for (i in colors.indices) {
            hsv[0] = (360 - i * colorAngleStep % 360).toFloat()
            colors[i] = Color.HSVToColor(hsv)
        }
        colors[colorCount] = colors[0]
        val sweepGradient = SweepGradient((width / 2).toFloat(), (height / 2).toFloat(), colors, null)
        val radialGradient = RadialGradient((width / 2).toFloat(), (height / 2).toFloat(), bigCircle!!.toFloat(), -0x1, 0x00FFFFFF, Shader.TileMode.CLAMP)
        val composeShader = ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER)
        mPaint!!.shader = composeShader
        val canvas = Canvas(bitmap)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), bigCircle!!.toFloat(), mPaint!!)
        return bitmap
    }


    //计算两点之间的位置
    private fun getLength(x1: Float, y1: Float, x2: Int, y2: Int): Int {
        return sqrt((x1 - x2).toDouble().pow(2.0) + (y1 - y2).toDouble().pow(2.0)).toInt()
    }

    //当触摸点超出圆的范围的时候，设置小球边缘位置
    private fun getBorderPoint(a: Point, b: Point, cutRadius: Int): Point {
        val radian = getRadian(a, b)
        return Point(a.x + (cutRadius * cos(radian.toDouble())).toInt(), a.x + (cutRadius * sin(radian.toDouble())).toInt())
    }

    //触摸点与中心点之间直线与水平方向的夹角角度
    private fun getRadian(a: Point, b: Point): Float {
        val lenA = (b.x - a.x).toFloat()
        val lenB = (b.y - a.y).toFloat()
        val lenC = sqrt((lenA * lenA + lenB * lenB).toDouble()).toFloat()
        var ang = acos((lenA / lenC).toDouble()).toFloat()
        ang *= if (b.y < a.y) -1 else 1
        return ang
    }

    private fun getRGB() {
        val pixel = bitmapBack!!.getPixel(mRockPosition!!.x, mRockPosition!!.y)
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)
        val a = Color.alpha(pixel)

        //十六进制的颜色字符串
        colorStr = "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b)
        listener?.onColorChange(a, r, g, b)
    }

    private fun toBrowserHexValue(number: Int): String {
        val builder = StringBuilder(Integer.toHexString(number and 0xff))
        while (builder.length < 2) {
            builder.append("0")
        }
        return builder.toString().uppercase(Locale.ROOT)
    }


    //颜色发生变化的回调接口
    interface OnColorChangedListener {
        fun onColorChange(a: Int, r: Int, g: Int, b: Int)
    }


}