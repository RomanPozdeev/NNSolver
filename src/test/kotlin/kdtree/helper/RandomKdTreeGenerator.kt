package kdtree.helper

import kdtree.KdPoint
import kdtree.KdTree
import java.util.*

abstract class RandomKdTreeGenerator<T> where T : Number, T : Comparable<T> {

    fun generate(dimensionCount: Int, pointCount: Int): KdTree<T> {
        return KdTree(generatePoints(dimensionCount, pointCount))
    }

    fun generatePoints(dimensionCount: Int, pointCount: Int): List<KdPoint<T>> {
        val points = ArrayList<KdPoint<T>>(pointCount)

        for (i in 0 until pointCount) {
            val position = ArrayList<T>(dimensionCount)

            for (axisIndex in 0 until dimensionCount) {
                position.add(buildRandomValue())
            }

            points.add(KdPoint(position))
        }

        return points
    }

    abstract fun buildRandomValue(): T
}