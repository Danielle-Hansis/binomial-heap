/**
 * BinomialHeap
 *
 * An implementation of binomial heap over positive integers.
 *
 */

public class BinomialHeap {
	public int size;
	public HeapNode last;
	public HeapNode min;
	public int numOfTrees;				//Added



	/**
	 * pre: key > 0
	 * <p>
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 */
	public HeapItem insert(int key, String info) {			//O(log n)
		HeapItem item = new HeapItem(key, info);
		HeapNode node = new HeapNode();
		node.item = item;
		item.node = node;

		//No need to meld - just add node to binomial heap
		if (this.size == 0) {
			this.addHeapNode(node);
		}
		else {
			BinomialHeap tempHeap = new BinomialHeap();
			tempHeap.numOfTrees = 1;
			tempHeap.addHeapNode(node);
			this.meld(tempHeap);
		}
		return item;
	}



	public void addHeapNode(HeapNode node) {					// (O(1))
		if (this.empty())
		{
			this.min = node;
			this.last = node;
			this.size = (int)Math.pow(2, node.rank);
			this.numOfTrees = 1;
			node.next = node;
		}

		else
		{
			if (node.item.key < this.min.item.key)
				this.min = node;
			node.next = this.last.next;
			this.last.next = node;
			this.last = node;
			this.size += (int)Math.pow(2, node.rank);
			this.numOfTrees++;
		}
	}


	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin(){						//O(log n)
		BinomialHeap childrenHeap = this.createChildrenHeap(this.min);
		BinomialHeap detachedHeap = this.detachNode(this.min);
		childrenHeap.meld(detachedHeap);
		this.copyHeap(childrenHeap);
	}

	public void copyHeap(BinomialHeap heap){						//Pastes heap given by user to this heap - O(1)
			this.min = heap.min;
			this.last = heap.last;
			this.size = heap.size;
			this.numOfTrees = heap.numOfTrees;
	}

	public BinomialHeap createChildrenHeap(HeapNode node){				//O(log n)
		BinomialHeap childrenHeap = new BinomialHeap();

		//We don't have any children - return empty heap
		if (node.rank == 0){
			return new BinomialHeap();
		}
		HeapNode current = node.child.next;
		HeapNode currentNext;

		int count = 0;
		while (count < node.rank)
		{
			currentNext = current.next;
			current.parent = null;
			childrenHeap.addHeapNode(current);
			current = currentNext;
			count ++;
		}

		return childrenHeap;
	}



	public BinomialHeap detachNode(HeapNode node) {				//Returns heap after we "removed" the node needed to detach - O(log n)
		BinomialHeap newHeap = new BinomialHeap();
		int numOfTrees = this.numOfTrees;
		int count = 0;
		HeapNode current = this.last.next;
		HeapNode currentNext;
		while (count < numOfTrees - 1) {
			currentNext = current.next;
			if (current != node) {
				newHeap.addHeapNode(current);
				count ++;
			}
			current = currentNext;
		}
		return newHeap;
	}

	/**
	 * 
	 * Return the minimal HeapItem, null if empty.
	 *
	 */
	public HeapItem findMin()									//O(1)
	{
		if (this.min == null)
			return null;
		return this.min.item;
	} 

	/**
	 * 
	 * pre: 0<diff<item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) {			//O(log n)
		HeapNode current = item.node;
		current.item.key -= diff;

		while((current.parent != null) && (current.item.key < current.parent.item.key)) {
			current.item = current.parent.item;
			current.parent.item.node = current;                //Switch the items between parent and curr
			current.parent.item = item;                        //New parent's item.key is smaller than old one
			item.node = current.parent;
			current = current.parent;                          //Move up the tree
		}

		if (item.key < this.min.item.key) {					  //Update min if needed
			this.min = item.node;
		}
	}


	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 					     //O(log n)
	{    
		int delta = (item.key - this.min.item.key) + 1;
		this.decreaseKey(item, delta);
		deleteMin();
	}


	/**
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)						//O(log n)
	{
		//If heap2 is empty
		if (heap2.size == 0)
			return;

		//If current is empty
		if (this.size == 0)
			this.copyHeap(heap2);								//Pastes heap2 into empty current
		else {
			int unionUntilRank = this.merge(heap2);
			this.unionTrees(unionUntilRank);
		}
		while (this.min.parent != null)							//Updates min pointer
			this.min = this.min.parent;
	}



	public void unionTrees(int unionUntilRank) {				//O(log n)
		BinomialHeap unionedHeap = new BinomialHeap();
		int numTrees = this.numTrees();
		if (numTrees == 1)
			return;
		if (numTrees == 2) {
			if (this.last.rank != this.last.next.rank)
				return;
			else
			{
				HeapNode temp = HeapNode.link(this.last.next, this.last);
				unionedHeap.addHeapNode(temp);
				this.copyHeap(unionedHeap);
			}
			return;
		}

		HeapNode current = this.last.next;
		HeapNode next = current.next;
		while (((current.rank <= unionUntilRank) || (current.rank == next.rank)) && (current != this.last))
		{
			if (current.rank != next.rank)
			{
				unionedHeap.addHeapNode(current);
				current = next;
				next = next.next;
			}

			//There are 3 trees with the same rank
			else if (next != current && next.next != current && current.rank == next.next.rank && next != this.last)
			{
				//We add the current and union the next two
				unionedHeap.addHeapNode(current);
				current = next;
				next = next.next;
			}

			//There are 2 trees with the same rank and the third one has bigger rank, or they are the only trees
			else if(next != current)
			{
				//Check if this is the end of the heap
				if (next == this.last)
				{
					BinomialHeap lastTreeHeap = new BinomialHeap();
					lastTreeHeap.addHeapNode(HeapNode.link(current, next));
					this.last = lastTreeHeap.last;
					this.numOfTrees--;
					current = this.last;
					break;
				}

				//There more than 2 trees in heap - and current and next are the same rank
				else
				{
					HeapNode nextNext = next.next;
					current = HeapNode.link(current, next);
					current.next = nextNext; 		//Maintaining current as a valid start for the rest of the heap list
					next = current.next;
					this.numOfTrees--;
				}
			}
		}

		//Concatenating the unioned heap to the rest of this.heap
		if (unionedHeap.empty())
		{
			this.last.next = current;
			return;
		}
		HeapNode firstNode = unionedHeap.last.next;
		unionedHeap.last.next = current;
		this.last.next = firstNode;
	}



	public int merge(BinomialHeap heap2) {					//O(log n)

		BinomialHeap mergedHeap = new BinomialHeap();
		BinomialHeap smallRankHeap;
		BinomialHeap bigRankHeap;

		//Determines which rank is the smallest
		if(this.last.rank > heap2.last.rank){
			smallRankHeap = heap2;
			bigRankHeap = this;
		}
		else{
			smallRankHeap = this;
			bigRankHeap = heap2;
		}

		//Calculate new size and number of trees
		int newSize = smallRankHeap.size + bigRankHeap.size;
		int newNumOfTrees = smallRankHeap.numOfTrees + bigRankHeap.numOfTrees;
		int smallHeapCounter = 0;
		int smallRankHeapMaxTreeRank = smallRankHeap.last.rank;
		HeapNode currentSmallHeapTree = smallRankHeap.last.next;
		HeapNode currentBigHeapTree = bigRankHeap.last.next;

		//Merge both heaps
		while (smallHeapCounter < smallRankHeap.numTrees())
		{
			if(currentSmallHeapTree.rank <= currentBigHeapTree.rank){
				HeapNode nextSmallHeapTree = currentSmallHeapTree.next;
				mergedHeap.addHeapNode(currentSmallHeapTree);
				currentSmallHeapTree = nextSmallHeapTree;
				smallHeapCounter += 1;
			}
			else{
				HeapNode nextBigHeapTree = currentBigHeapTree.next;
				mergedHeap.addHeapNode(currentBigHeapTree);
				currentBigHeapTree = nextBigHeapTree;
			}
		}

		//Update min
		HeapNode newMin;
		if (mergedHeap.min.item.key <= bigRankHeap.min.item.key){
			newMin = mergedHeap.min;
		}
		else{
			newMin = bigRankHeap.min;
		}
		HeapNode firstMergedHeapNode = mergedHeap.last.next;
		mergedHeap.last.next = currentBigHeapTree;
		mergedHeap.last = bigRankHeap.last;
		mergedHeap.last.next = firstMergedHeapNode;
		this.last = mergedHeap.last;
		this.min = newMin;
		this.size = newSize;
		this.numOfTrees = newNumOfTrees;
		return smallRankHeapMaxTreeRank;
	}


	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()			//O(1)
	{
		return this.size;
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()			//O(1)
	{
		return (this.size == 0);

	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()					//O(1)
	{
		return this.numOfTrees;
	}

	/**
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public static class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;


		public static HeapNode link(HeapNode a, HeapNode b)			//O(1)
		{
			if (a.item.key >= b.item.key)
			{
				HeapNode temp = a;
				a = b;
				b = temp;
			}
			if (a.rank == 0)
			{
				a.child = b;
				b.parent = a;
				a.rank += 1;
				a.next = a;
				b.next = b;
			}
			else
			{
				b.next = a.child.next;
				a.child.next = b;
				b.parent = a;
				a.child = b;

				//When linking two same rank binomial trees the size increases by 1
				a.rank += 1;
			}
			return a;
		}

	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 *  
	 */
	public static class HeapItem{
		public HeapNode node;
		public int key;
		public String info;

		public HeapItem(int key, String info){				//Constructor
			this.node = null;
			this.key = key;
			this.info = info;
		}
	}
}
