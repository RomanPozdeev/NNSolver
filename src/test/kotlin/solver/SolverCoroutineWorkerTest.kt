package solver

import kdtree.KdTree
import kdtree.helper.RandomDoubleKdTreeGenerator
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.Test

class SolverCoroutineWorkerTest {
    @Test
    fun testGetResultPoints() {
        val dimensionCount = 3

        val treeGenerator = RandomDoubleKdTreeGenerator()

        val inputPoints = treeGenerator.generatePoints(dimensionCount, POINT_COUNT)
        val tree = KdTree(inputPoints)

        val worker = SolverCoroutineWorker(tree, inputPoints)

        val resultPoints = runBlocking {
            worker.getResultPointsAsync().await()
        }

        Assertions.assertThat(inputPoints.size).isEqualTo(resultPoints.size)

        for (i in resultPoints.indices) {
            // Make sure each result point is different from its input point at that index.
            Assertions.assertThat(inputPoints[i]).isNotEqualTo(resultPoints[i])
        }
    }


    companion object {
        private const val POINT_COUNT = 100000
    }
}