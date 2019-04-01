package kdtree

import java.util.*


class KdPoint<T> where T : Number, T : Comparable<T> {
    private val axisValues: List<T>

    val dimensions: Int
        get() = axisValues.size

    constructor(vararg axisValues: T) {
        this.axisValues = axisValues.asList()
    }

    constructor(axisValues: List<T>) {
        this.axisValues = axisValues
    }

    constructor(sourcePoint: KdPoint<T>) {
        this.axisValues = ArrayList(sourcePoint.axisValues)
    }

    /**
     * Returns the value on the axis with the provided index.
     *
     * @throws IndexOutOfBoundsException
     * if the index is out of range (index < 0 || index >= size())
     */
    fun getAxisValue(axisIndex: Int): T {
        return axisValues[axisIndex]
    }

    override fun toString(): String {
        return axisValues.toString()
    }

    fun getDistanceSquared(other: KdPoint<T>): Double {
        // Calculate the squared distance to the other point via euclidean distance.

        val dimensions = axisValues.size

        return (0 until dimensions)
            .asSequence()
            .map { axisValues[it].toDouble() - other.axisValues[it].toDouble() }
            .sumByDouble { it * it }

    }
}