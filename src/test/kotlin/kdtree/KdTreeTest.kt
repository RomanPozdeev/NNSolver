package kdtree

import kdtree.helper.RandomDoubleKdTreeGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class KdTreeTest {

    @Test
    fun testKdTree() {
        for (dimensionCount in MIN_DIMENSIONS..MAX_DIMENSIONS) {
            testKdTreeWithDimensionCount(dimensionCount)
        }
    }

    @Test(expected = InvalidKdPointCountException::class)
    fun testKdTreeWithNoPoints() {
        // Expected to throw.
        KdTree(listOf<KdPoint<Double>>())
    }

    private fun testKdTreeWithDimensionCount(dimensionCount: Int) {
        val treeGenerator = RandomDoubleKdTreeGenerator()

        val tree = treeGenerator.generate(dimensionCount, POINT_COUNT)

        assertThat(dimensionCount).isEqualTo(tree.dimensionCount)

        assertThat(tree.rootNode.hasParentNode()).isFalse()

        // To test the tree, we have to traverse each node, performing various checks.

        checkNode(tree, tree.rootNode)
    }

    private fun checkNode(tree: KdTree<Double>, node: KdNode<Double>) {
        val axisIndex = node.axisIndex

        val leftNode = node.leftNode

        if (leftNode != null) {
            checkNode(tree, leftNode)

            assertThat(node.hasLeftNode()).isTrue()

            // Assert that the children depth is greater that the current depth.

            assertThat(leftNode.depth).isEqualTo(node.depth + 1)

            assertThat(node.axisIndex).isEqualTo(tree.getAxisIndex(node.depth))
            assertThat(leftNode.axisIndex).isEqualTo(tree.getAxisIndex(leftNode.depth))

            // Assert that the value of the left node on the relevant axis index is always
            // smaller than or equal to the parent we're checking.

            assertThat(leftNode.point.getAxisValue(axisIndex)).isLessThanOrEqualTo(node.point.getAxisValue(axisIndex))

            // Assert that the children has a parent and the parent is the current node.

            assertThat(leftNode.hasParentNode()).isTrue()
            assertThat(node).isEqualTo(leftNode.parentNode)
        } else {
            assertThat(node.hasLeftNode()).isFalse()
        }

        val rightNode = node.rightNode

        if (rightNode != null) {
            checkNode(tree, rightNode)

            assertThat(node.hasRightNode()).isTrue()

            // Assert that the children depth is greater that the current depth.

            assertThat(rightNode.depth).isEqualTo(node.depth + 1)

            assertThat(node.axisIndex).isEqualTo(tree.getAxisIndex(node.depth))
            assertThat(rightNode.axisIndex).isEqualTo(tree.getAxisIndex(rightNode.depth))

            // Assert that the value of the right node on the relevant axis index is always
            // larger than the parent we're checking.

            assertThat(rightNode.point.getAxisValue(axisIndex)).isGreaterThan(node.point.getAxisValue(axisIndex))

            // Assert that the children has a parent and the parent is the current node.

            assertThat(rightNode.hasParentNode()).isTrue()
            assertThat(node).isEqualTo(rightNode.parentNode)
        } else {
            assertThat(node.hasRightNode()).isFalse()
        }

        // The children count of this node must correspond to the actual nodes instances
        // we requested earlier.

        if (leftNode != null && rightNode != null) {
            assertThat(node.hasChildren()).isTrue()
            assertThat(node.numberOfChildren()).isEqualTo(2)
        } else if (leftNode != null || rightNode != null) {
            assertThat(node.hasChildren()).isTrue()
            assertThat(node.numberOfChildren()).isEqualTo(1)
        } else {
            assertThat(node.hasChildren()).isFalse()
            assertThat(node.numberOfChildren()).isEqualTo(0)
        }
    }

    companion object {
        private const val POINT_COUNT = 100000

        private const val MIN_DIMENSIONS = 1
        private const val MAX_DIMENSIONS = 10
    }
}
