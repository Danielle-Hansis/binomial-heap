# Binomial-Heap
A Java implementation of a Binomial Heap supporting positive integer keys.

Provides efficient priority queue operations, including insertion, deletion, key updates, and heap merging.

## Implementation Details
Language: Java

Structure: Linked circular root list, parent/child/sibling pointers.

Tracks heap size, number of trees, and minimum pointer.

Uses rank-based merging of binomial trees.

### Allows the following:

Insert elements – O(log n), Find minimum – O(1), Delete minimum – O(log n),

Decrease key – O(log n), Delete arbitrary element – O(log n), Meld/merge two heaps – O(log n)
