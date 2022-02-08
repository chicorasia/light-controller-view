package br.com.chicorialabs.lightscontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Essa classe representa uma View personalizada que herda
 * diretamente da classe View.
 */
class LightControllerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * As constantes _OFFSET são usadas para ajustar a posição de elementos
     * da View; pointPosition é um objeto tipo Point Float que serve como origem
     * da View e ajuda a calcular as coordenadas dos outros elementos visuais
     * no momento da renderização.
     * O objeto paint é usado para desenhar os objetos no Canvas.
     * Declarar e inicializar as variáveis aqui acelera o processo de desenho.
     * Evitar fazer inicializações dentro do método onDraw()!
     * barWidth e barHeight representam as dimensões da barra do indicador, que
     * é menor do que a View em si.
     */
    private var barWidth = 0.0f
    private var barHeight = 0.0f
    private val LABEL_X_OFFSET = 20
    private val PADDING_OFFSET = 27
    private val pointPosition = PointF(0.0f, 0.0f)
    private var controllerSetting = ControllerSetting.OFF

    /**
     * Uma configuração básica do objeto Paint; já define atributos
     * de texto para ganhar tempo.
     */
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.LEFT
        textSize = 55.0F
        typeface = Typeface.create("", Typeface.BOLD)
    }

    /**
     * Esse bloco faz a configuração inicial da View
     */
    init {
        isClickable = true
    }

    /**
     * Calcula o tamanho da barra vertical em função das dimensões da View
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        barWidth = (w / 2).toFloat()
        barHeight = h.toFloat()
    }

    /**
     * Esse método desenha os elementos visuais sobre a Canvas.
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        /**
         * Reseta o valor de pointPosition
         */
        pointPosition.x = 0.0f
        pointPosition.y = 0.0f

        /**
         * Define uma cor para o objeto Paint
         */
        paint.color = Color.GRAY
        /**
         * Desenha um retângulo sobre a Canvas descontando o
         * PADDING_OFFSET no topo e na base da View
         */
        canvas?.drawRect(
            pointPosition.x + barWidth / 2,
            pointPosition.y + PADDING_OFFSET,
            (pointPosition.x + barWidth * 1.5).toFloat(),
            pointPosition.y + barHeight - PADDING_OFFSET,
            paint
        )

        /**
         * Desenha o retângulo do indicador; isso precisa acontecer antes
         * do desenho dos rótulos por causa do valor de pointPosition!
         */
        val indicator = pointPosition.createIndicatorRectF(controllerSetting, barWidth, barHeight)
        paint.color = Color.MAGENTA
        canvas?.drawRect(indicator, paint)

        /**
         * Instrui o objeto paint a pintar usando preto
         */
        paint.color = Color.BLACK

        /**
         * Percorre os valores de ControllerSetting e desenha um label para
         * cada item da enum.
         */
        ControllerSetting.values().forEach {
            pointPosition.computeXYforSettingsText(it, this.barWidth, this.barHeight)
            val label = it.label.toString()
            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

    /**
     * Esse método lida com os aspectos visuais do clique na View.
     * Com isso o método setOnClickListener fica liberado para
     * lidar com os comportamentos da aplicação.
     */
    override fun performClick(): Boolean {
        if (super.performClick()) return true

        /**
         * Chama o método next() da enum para atualizar o valor de controllerSetting
         */
        controllerSetting = controllerSetting.next()

        /**
         * Invalida a View para forçar um redesenho
         */
        invalidate()
        return true
    }

    /**
     * Esse método calcula coordenadas X e Y para os textos
     */
    fun PointF.computeXYforSettingsText(pos: ControllerSetting, barWidth: Float, height: Float) {
        x = (1.5 * barWidth + LABEL_X_OFFSET).toFloat()

        val barHeight = height - 2 * PADDING_OFFSET

        y = when(pos.ordinal) {
            0 -> barHeight
            1 -> barHeight * 3 / 4
            2 -> barHeight / 2
            3 -> barHeight / 4
            4 -> 0.0f
            else -> { 0.0f}
        } + (1.5 * PADDING_OFFSET).toFloat()

    }

    /**
     * Esse método retorna um RectF conforme o valor de ControllerSetting, com base
     * nas dimensões totais da View
     */
    private fun PointF.createIndicatorRectF(
        pos: ControllerSetting,
        width: Float,
        height: Float
    ) : RectF {

        val left = x + width / 2
        val right = (x + width * 1.5).toFloat()
        val bottom = height - PADDING_OFFSET
        val barHeight = height - 2 * PADDING_OFFSET

        val top = when(pos.ordinal) {
            0 -> bottom
            1 -> bottom - barHeight / 4
            2 -> bottom - barHeight / 2
            3 -> bottom - barHeight * 3/ 4
            4 -> 0.0f + PADDING_OFFSET
            else -> { 0.0f}
        }

        return RectF(left, top, right, bottom)

    }

    /**
     * Um enumerador para organizar os valores do indicador.
     */
    enum class ControllerSetting(val label: Int) {
        OFF(label = 0),
        TWENTY_FIVE(label = 25),
        FIFTY(label = 50),
        SEVENTY_FIVE(label = 75),
        FULL(label = 100);

        /**
         * Um método de conveniência para ciclar pelos valores da enum
         */
        fun next() : ControllerSetting {
            return when (this) {
                OFF ->  TWENTY_FIVE
                TWENTY_FIVE -> FIFTY
                FIFTY -> SEVENTY_FIVE
                SEVENTY_FIVE -> FULL
                FULL -> OFF
            }
        }

    }


}

