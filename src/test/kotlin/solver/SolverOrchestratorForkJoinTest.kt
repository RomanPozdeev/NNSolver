package solver

import kdtree.KdPoint
import kdtree.KdTree
import kdtree.helper.RandomDoubleKdTreeGenerator
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ForkJoinPool

class SolverOrchestratorForkJoinTest {
    private lateinit var inputPoints: List<KdPoint<Double>>
    private lateinit var tree: KdTree<Double>

    @Before
    fun setup() {
        val dimensionCount = 3
        val treeGenerator = RandomDoubleKdTreeGenerator()
        inputPoints = treeGenerator.generatePoints(dimensionCount, POINT_COUNT)
        tree = KdTree(inputPoints)
    }

    @Test
    fun testFindNearestPointsWithCustomForkJoinPoll() {
        val orchestrator = SolverOrchestratorForkJoin(tree, ForkJoinPool(Runtime.getRuntime().availableProcessors()))
        performOrchestratorTest(orchestrator, inputPoints)
    }

    @Test
    fun testFindNearestPointsWithDefaultForkJoinPool() {
        val orchestrator = SolverOrchestratorForkJoin(tree)
        performOrchestratorTest(orchestrator, inputPoints)
    }

    private fun performOrchestratorTest(
        orchestrator: SolverOrchestratorForkJoin<Double>,
        inputPoints: List<KdPoint<Double>>
    ) {
        // Request the result from the orchestrator.

        val resultPoints = orchestrator.findNearestPoints(inputPoints)

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