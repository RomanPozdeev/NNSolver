package kdtree.helper

import kotlin.random.Random

class RandomDoubleKdTreeGenerator : RandomKdTreeGenerator<Double>() {

    private val random = Random(0)

    override fun buildRandomValue(): Double {
        return stretch(random.nextDouble(), AXIS_POSITION_MIN, AXIS_POSITION_MAX)
    }

    private fun stretch(uniformDouble: Double, min: Double, max: Double): Double {
        return min + uniformDouble * (max - min)
    }

    companion object {
        /**
         * The minimum for point values on any axis.
         */
        private const val AXIS_POSITION_MIN = 100000.0

        /**
         * The maximum for random point values on any axis.
         */
        private const val AXIS_POSITION_MAX = 1000000.0
    }
}