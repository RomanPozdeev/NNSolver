package solver

import kdtree.KdPoint
import kdtree.KdTree
import kdtree.helper.RandomDoubleKdTreeGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


class SolverOrchestratorTest {
    private lateinit var inputPoints: List<KdPoint<Double>>
    private lateinit var tree: KdTree<Double>

    @Before
    fun setup() {
        val dimensionCount = 3
        val treeGenerator = RandomDoubleKdTreeGenerator()
        inputPoints = treeGenerator.generatePoints(dimensionCount, POINT_COUNT)
        tree = KdTree(inputPoints)
    }

    @Test(expected = SolverInterruptedException::class)
    fun testInterruptException() {
        val orchestrator = SolverOrchestrator(tree)

        Thread.currentThread().interrupt()

        orchestrator.findNearestPoints(inputPoints)
    }

    @Test
    fun testFindNearestPointsFixedWorkerThreadCount() {
        val workerThreadsCount = 6
        val orchestrator = SolverOrchestrator(tree, workerThreadsCount)

        performOrchestratorTest(orchestrator, inputPoints)
    }

    @Test
    fun testFindNearestPointsDynamicWorkerThreadCount() {
        val orchestrator = SolverOrchestrator(tree)

        performOrchestratorTest(orchestrator, inputPoints)
    }

    private fun performOrchestratorTest(
        orchestrator: SolverOrchestrator<Double>,
        inputPoints: List<KdPoint<Double>>
    ) {
        // Request the result from the orchestrator.

        val resultPoints = orchestrator.findNearestPoints(inputPoints)

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