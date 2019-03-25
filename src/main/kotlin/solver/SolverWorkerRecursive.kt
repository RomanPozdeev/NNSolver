package solver

import kdtree.KdPoint
import kdtree.KdTree
import java.util.*
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveTask


class SolverWorkerRecursive<T>(
    private val tree: KdTree<T>,
    private val inputPoints: List<KdPoint<T>>,
    private val threshold: Int = 100
) :
    RecursiveTask<List<KdPoint<T>>>() where T : Number, T : Comparable<T> {

    private val solver = Solver(tree)

    override fun compute(): List<KdPoint<T>> {
        return if (inputPoints.size > threshold) {
            ForkJoinTask.invokeAll(createSubTasks())
                .asSequence()
                .fold(ArrayList(inputPoints.size)) { acc, new ->
                    acc.apply {
                        addAll(new.get())
                    }
                }
        } else {
            processing(inputPoints)
        }
    }

    private fun createSubTasks(): Collection<SolverWorkerRecursive<T>> {
        val dividedTasks = ArrayList<SolverWorkerRecursive<T>>(2)
        dividedTasks.add(
            SolverWorkerRecursive(
                tree,
                inputPoints.subList(0, inputPoints.size / 2),
                threshold
            )
        )
        dividedTasks.add(
            SolverWorkerRecursive(
                tree,
                inputPoints.subList(inputPoints.size / 2, inputPoints.size),
                threshold
            )
        )
        return dividedTasks
    }

    private fun processing(data: List<KdPoint<T>>): List<KdPoint<T>> {
        return data.map { point ->
            solver.findNearestPoint(point)
        }
    }
}