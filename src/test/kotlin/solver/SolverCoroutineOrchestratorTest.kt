package solver

import kdtree.KdPoint
import kdtree.KdTree
import kdtree.helper.RandomDoubleKdTreeGenerator
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class SolverCoroutineOrchestratorTest {
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
    fun testFindNearestPointsWithCoroutineCountEqProcessorsCount() {
        val orchestrator = SolverCoroutineOrchestrator(tree)
        performOrchestratorTest(orchestrator, inputPoints)
    }

    @Test
    fun testFindNearestPointsDynamicCoroutineCount() {
        val orchestrator = SolverCoroutineOrchestrator(tree, 10000)
        performOrchestratorTest(orchestrator, inputPoints)
    }

    private fun performOrchestratorTest(
        orchestrator: SolverCoroutineOrchestrator<Double>,
        inputPoints: List<KdPoint<Double>>
    ) {
        // Request the result from the orchestrator.

        val resultPoints = runBlocking {
            orchestrator.findNearestPoints(inputPoints)
        }

        Assertions.assertThat(inputPoints.size).isEqualTo(resultPoints.size)
    }

    companion object {
        private const val POINT_COUNT = 100000
    }
}