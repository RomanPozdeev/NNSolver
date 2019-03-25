package kdtree

import java.util.ArrayList
import kotlin.Comparator
import kotlin.random.Random

/**
 * Creates a new KdTree instance based on the provided data points. The number
 * of axis values of the first data point determines the number of dimensions.
 *
 * @param points
 * the points to include in the tree data.
 * @throws InvalidKdPointCountException
 * if the provided list of points was null or did not contain at
 * least one point.
 */
class KdTree<T>(points: List<KdPoint<T>>) where T : Number, T : Comparable<T> {

    val dimensionCount: Int

    val rootNode: KdNode<T>

    init {
        // Make sure at least one point was provided.

        if (points.isEmpty()) {
            throw InvalidKdPointCountException()
        }

        this.dimensionCount = points[0].dimensions

        this.rootNode = buildNode(null, points, 0)!!
    }

    /**
     * Returns the axis index for the provided depth, based on the dimension count
     * of this tree.
     *
     * @param depth
     * the requested tree node depth.
     */
    fun getAxisIndex(depth: Int): Int {
        return depth % dimensionCount
    }

    private fun buildNode(parentNode: KdNode<T>?, points: List<KdPoint<T>>, depth: Int): KdNode<T>? {
        if (points.isEmpty()) {
            return null
        }

        val axisIndex = getAxisIndex(depth)

        val medianPoint = getFastApproximatedMedianPoint(points, axisIndex)

        // Create node and construct subtrees.

        val newNode = KdNode(medianPoint, depth, axisIndex)

        // Assume both sides have approximately half the number of points.

        val leftOfMedian = ArrayList<KdPoint<T>>(points.size / 2)
        val rightOfMedian = ArrayList<KdPoint<T>>(points.size / 2)

        points
            .asSequence()
            .filter { it != medianPoint }
            .forEach {
                if (it.getAxisValue(axisIndex) > medianPoint.getAxisValue(axisIndex)) {
                    rightOfMedian.add(it)
                } else {
                    leftOfMedian.add(it)
                }
            }

        val leftNode = buildNode(newNode, leftOfMedian, depth + 1)
        val rightNode = buildNode(newNode, rightOfMedian, depth + 1)

        newNode.leftNode = leftNode
        newNode.rightNode = rightNode
        newNode.parentNode = parentNode

        return newNode
    }

    /**
     * Uses a small subset of the points to choose a median point approximating the
     * median of all provided points.
     */
    private fun getFastApproximatedMedianPoint(points: List<KdPoint<T>>, axisIndex: Int): KdPoint<T> {
        val numberOfElements = Math.max(points.size * MEDIAN_APPROXIMATION_POINTS_PERCENTAGE, 1f).toInt()

        val subset = pickRandomSubset(points, numberOfElements)

        sortByAxisIndex(subset, axisIndex)

        return subset[subset.size / 2]
    }

    private fun sortByAxisIndex(points: List<KdPoint<T>>, axisIndex: Int) {
        points.sortedWith(Comparator { left, right ->
            left.getAxisValue(axisIndex).compareTo(right.getAxisValue(axisIndex))
        })
    }

    /**
     *
     *
     * Picks a random subset of points from the given list.
     *
     *
     * **Important:**
     *
     *
     *
     * This method uses a simple select-one-of-N approach to choose each random
     * point. This may result in points being included multiple times in the
     * returned list.
     *
     */
    private fun pickRandomSubset(points: List<KdPoint<T>>, numberOfElements: Int): List<KdPoint<T>> {
        val subset = ArrayList<KdPoint<T>>(numberOfElements)

        for (i in 0 until numberOfElements) {
            val randomIndex = random.nextInt(numberOfElements)

            subset.add(points[randomIndex])
        }

        return subset
    }

    companion object {
        /**
         * Use 1% of all relevant points to choose a median approximation.
         */
        private const val MEDIAN_APPROXIMATION_POINTS_PERCENTAGE = 0.01f

        /**
         * Random instance used for the median approximation.
         */
        private val random = Random(0)
    }

}