package solver

import kdtree.KdPoint
import kdtree.KdTree
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class SolverCoroutineWorker<T>(
    private val tree: KdTree<T>,
    private val inputPoints: List<KdPoint<T>>
) where T : Number, T : Comparable<T> {
    fun getResultPointsAsync(): Deferred<List<KdPoint<T>>> {
        return GlobalScope.async {
            val solver = Solver(tree)

            inputPoints.map { point ->
                solver.findNearestPoint(point)
            }
        }
    }
}