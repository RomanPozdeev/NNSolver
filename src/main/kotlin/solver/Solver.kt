package solver

import kdtree.KdNode
import kdtree.KdPoint
import kdtree.KdTree

class Solver<T>(private val tree: KdTree<T>) where T : Number, T : Comparable<T> {
    /**
     *
     *
     * Returns the nearest tree point to the provided target point.
     *
     *
     * @param searchTargetPoint
     * @return
     */
    fun findNearestPoint(searchTargetPoint: KdPoint<T>): KdPoint<T> {
        return solveForNode(tree.rootNode, searchTargetPoint, CurrentBestResult()).currentBestPoint!!
    }

    private fun solveForNode(
        node: KdNode<T>,
        searchTargetPoint: KdPoint<T>,
        currentBestResult: CurrentBestResult<T>
    ): CurrentBestResult<T> {
        // Move down the tree recursively to get the starting leaf node and use it as an
        // initial "best point" if none was set yet.

        val leaf = findLeaf(node, searchTargetPoint)

        updateCurrentBestIfNeeded(leaf.point, searchTargetPoint, currentBestResult)

        // Then unwind from this leaf, possibly calling this node solver recursively for
        // different branches.

        unwindFrom(leaf, node, searchTargetPoint, currentBestResult)

        return currentBestResult
    }

    private tailrec fun findLeaf(
        node: KdNode<T>,
        searchTargetPoint: KdPoint<T>
    ): KdNode<T> {
        if (node.hasChildren()) {
            if (node.numberOfChildren() == 1) {
                // One children. Use the single sub node to continue.

                return if (node.hasLeftNode()) {
                    findLeaf(node.leftNode!!, searchTargetPoint)
                } else {
                    findLeaf(node.rightNode!!, searchTargetPoint)
                }
            } else {
                // Two children. Decide which sub node to follow.

                val searchValue = searchTargetPoint.getAxisValue(node.axisIndex)
                val nodeValue = node.point.getAxisValue(node.axisIndex)

                // If the axis value of the point we're searching for is greater than the axis
                // value of node we're looking at, continue with the right sub node.

                return if (searchValue > nodeValue) {
                    findLeaf(node.rightNode!!, searchTargetPoint)
                } else {
                    findLeaf(node.leftNode!!, searchTargetPoint)
                }
            }

        } else {
            // No children. We reached a leaf node, return it.

            return node
        }
    }

    /**
     * Updates the current best, if no current best is set or the provided point is
     * better than the current best.
     */
    private fun updateCurrentBestIfNeeded(
        point: KdPoint<T>,
        searchTargetPoint: KdPoint<T>,
        currentBestResult: CurrentBestResult<T>
    ) {
        // Don't use the actual search point as the best point.

        if (point == searchTargetPoint) {
            return
        }

        if (currentBestResult.currentBestPoint == null) {
            currentBestResult.currentBestPoint = point
            currentBestResult.currentBestDistanceSquared = point.getDistanceSquared(searchTargetPoint)
        } else {
            // Cache the leaf distance, to only do the distance calculation once.
            val leafDistanceSquared = point.getDistanceSquared(searchTargetPoint)
            if (leafDistanceSquared < currentBestResult.currentBestDistanceSquared) {
                currentBestResult.currentBestPoint = point
                currentBestResult.currentBestDistanceSquared = leafDistanceSquared
            }
        }
    }

    private fun unwindFrom(
        leafNode: KdNode<T>,
        topNode: KdNode<T>,
        searchTargetPoint: KdPoint<T>,
        currentBestResult: CurrentBestResult<T>
    ) {
        // Iteratively move up the node tree until we reach the top node.

        var workingNode = leafNode

        while (workingNode.parentNode != topNode.parentNode) {
            val parentNode = workingNode.parentNode

            updateCurrentBestIfNeeded(
                parentNode!!.point,
                searchTargetPoint, currentBestResult
            )

            // Check whether there could be any points on the other side of this parent
            // node.

            // To do this we check the intersection of the hypersphere with the hyperplane.
            // Simply put, because the final axis is aligned, we can final just compare some
            // values.

            val parentPointValue = parentNode.point.getAxisValue(parentNode.axisIndex).toDouble()
            val searchPointValue = searchTargetPoint.getAxisValue(parentNode.axisIndex).toDouble()

            val axisDistance = parentPointValue - searchPointValue

            // Because we need to compare apples to apples, we need to calculate the squared
            // distance.

            val axisDistanceSquared = axisDistance * axisDistance

            if (axisDistanceSquared < currentBestResult.currentBestDistanceSquared && parentNode.numberOfChildren() == 2) {
                // We want to traverse the other path, so we need to check which side we started
                // unwinding from.

                if (parentNode.leftNode == workingNode) {
                    // Start the full solver procedure for the right sub node.

                    solveForNode(parentNode.rightNode!!, searchTargetPoint, currentBestResult)
                } else {
                    // Start the full solver procedure for the left sub node.

                    solveForNode(parentNode.leftNode!!, searchTargetPoint, currentBestResult)
                }
            }

            workingNode = parentNode
        }
    }

    class CurrentBestResult<T>(
        var currentBestPoint: KdPoint<T>? = null,
        var currentBestDistanceSquared: Double = 0.toDouble()
    )
            where T : Number, T : Comparable<T>
}