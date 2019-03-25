package solver

import kdtree.KdPoint
import kdtree.KdTree
import kdtree.helper.RandomDoubleKdTreeGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*


class SolverTest {

    @Test
    fun testFindNearestPointForAllDimensions() {
        for (dimensionCount in MIN_DIMENSIONS..MAX_DIMENSIONS) {
            testFindNearestPointForDimensionCount(dimensionCount)
        }
    }

    @Test
    fun testFindNearestPointForExplicitData() {
        // Since the other test simply used random points to check some of the promises,
        // we need to use explicit data to make sure the returned nearest point is
        // actually correct.
        val inputPoints = listOf(
            KdPoint(0.0, 0.0),
            KdPoint(5.0, 5.0),
            KdPoint(8.0, 5.0),
            KdPoint(-30.0, -30.0),
            KdPoint(-40.0, -40.0),
            KdPoint(0.01, 0.01)
        )

        val kdTree = KdTree(inputPoints)
        val solver = Solver(kdTree)

        val nearestToIndex = ArrayList<KdPoint<Double>>()

        // Find the nearest point for all input points and store them in an array.

        for (i in inputPoints.indices) {
            nearestToIndex.add(solver.findNearestPoint(inputPoints[i]))
        }

        // Perform various neighbour checks on the array.
        // Keep in mind the notation is assertThat(expected, actual).

        assertThat(inputPoints[5]).isEqualTo(nearestToIndex[0])
        assertThat(inputPoints[2]).isEqualTo(nearestToIndex[1])
        assertThat(inputPoints[1]).isEqualTo(nearestToIndex[2])
        assertThat(inputPoints[4]).isEqualTo(nearestToIndex[3])
        assertThat(inputPoints[3]).isEqualTo(nearestToIndex[4])
        assertThat(inputPoints[0]).isEqualTo(nearestToIndex[5])
    }

    private fun testFindNearestPointForDimensionCount(dimensionCount: Int) {
        val treeGenerator = RandomDoubleKdTreeGenerator()
        val inputPoints = treeGenerator.generatePoints(dimensionCount, POINT_COUNT)
        val kdTree = KdTree(inputPoints)
        val solver = Solver(kdTree)

        for (inputPoint in inputPoints) {
            // Build a list of axis values for the requested point to check.

            val axisValues = ArrayList<Double>(dimensionCount)

            for (dimensionIndex in 0 until dimensionCount) {
                axisValues.add(inputPoint.getAxisValue(dimensionIndex))
            }

            // Assert the returned point from the NNSolver is located at our axis values.

            assertNearestPointIsPointAt(solver, axisValues)

            // Assert that providing the point itself, it will NOT return the exact same
            // point instance.

            assertNearestPointIsNotSelf(solver, inputPoint)
        }
    }

    private fun assertNearestPointIsPointAt(solver: Solver<Double>, axisValues: List<Double>) {
        // By creating a new KdPoint instance for the request, we should get the
        // input point instance at this exact location (assuming we added a point there
        // during tree setup of course).

        val nearest = solver.findNearestPoint(KdPoint(axisValues))

        // Check that the returned axis values of the nearest point equal to the input
        // axis values.

        for (dimensionIndex in axisValues.indices) {
            assertThat(nearest.getAxisValue(dimensionIndex)).isEqualTo(axisValues[dimensionIndex])
        }
    }

    private fun assertNearestPointIsNotSelf(solver: Solver<Double>, point: KdPoint<Double>) {
        val nearest = solver.findNearestPoint(point)
        assertThat(point).isNotEqualTo(nearest)
    }

    companion object {
        private const val POINT_COUNT = 100000

        private const val MIN_DIMENSIONS = 3
        private const val MAX_DIMENSIONS = 3
    }
}