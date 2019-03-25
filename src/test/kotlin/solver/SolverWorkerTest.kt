package solver

import kdtree.KdTree
import kdtree.helper.RandomDoubleKdTreeGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class SolverWorkerTest {

    @Test
    @Throws(InterruptedException::class)
    fun testGetResultPoints() {
        val dimensionCount = 3

        val treeGenerator = RandomDoubleKdTreeGenerator()

        val inputPoints = treeGenerator.generatePoints(dimensionCount, POINT_COUNT)
        val tree = KdTree(inputPoints)

        val worker = SolverWorker(tree, inputPoints)

        // Kick off the worker and request the result.

        worker.start()

        val resultPoints = worker.getResultPoints()

        assertThat(inputPoints.size).isEqualTo(resultPoints.size)

        for (i in resultPoints.indices) {
            // Make sure each result point is different from its input point at that index.
            assertThat(inputPoints[i]).isNotEqualTo(resultPoints[i])
        }
    }

    companion object {
        private const val POINT_COUNT = 100000
    }

}