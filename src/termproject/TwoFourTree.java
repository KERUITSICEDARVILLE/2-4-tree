package termproject;

import java.util.Random;

/**
 * Title:        Term Project 2-4 Trees
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
public class TwoFourTree implements Dictionary {
    
    private class TFNodeIndex {
        public TFNode node;
        public int index;
        public boolean hasItem;//if the node contains the item of interest
        
        public TFNodeIndex(TFNode n, int i, boolean b){
            node = n;
            index = i;
            hasItem = b;
        }
    }

    private Comparator treeComp;
    private int size = 0;
    private TFNode treeRoot = null;

    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    private TFNode root() {
        return treeRoot;
    }

    private void setRoot(TFNode root) {
        treeRoot = root;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }
   
    /**
     * 
     * @param key
     * @return TFNodeIndex. Either returns parent of desired node or the desired node depending
     * on whether the key exists in the tree or not. Returns null if the tree is empty.
     */
    private TFNodeIndex find (Object key) {
        if(isEmpty()){
            return null;
        }
        TFNode temp = treeRoot;
        while(true){//stops, if the tree is finite in length
            int i = 0;
            //find the index of the desired child
            while(i < temp.getNumItems() && 
                    treeComp.isGreaterThan(key, (temp.getItem(i)).key())) {
                ++i;
            }           
            //if we encounter the desired key
            if(i < temp.getNumItems() && treeComp.isEqual(key, (temp.getItem(i)).key())){
                //we care about the ith Item of temp
                return new TFNodeIndex(temp, i, true);
            }
            if(temp.getChild(i) == null){
                //the data would be the ith child of temp, if it existed in the tree
                
                System.out.println("Node not found!\n\nKey: " + key);
                return new TFNodeIndex(temp, i, false);
            }
            
            temp = temp.getChild(i);
            
        }

    }
    
    /**
     * Searches dictionary to determine if key is present
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    @Override
    public Object findElement(Object key) {
        TFNodeIndex theNode = find(key);
        if(theNode.hasItem){
            return ((theNode.node).getItem(theNode.index)).element();
        }
        //if we do not find the desired key
        return null;//indistinguishable from the case when element is null
    }

    /**
     * Inserts provided element into the Dictionary
     * @param key of object to be inserted
     * @param element to be inserted
     */
    @Override
    public void insertElement(Object key, Object element) {
        if(isEmpty()){
            treeRoot = new TFNode();
            treeRoot.addItem(0, new Item(key, element));
            size ++;
            return;
        }
        TFNodeIndex theNode = find(key);
        TFNode temp = theNode.node;
        if(!theNode.hasItem){//if key is not already in the tree. Then we are at an external node
            (theNode.node).insertItem(theNode.index, new Item(key, element));
            ++size;
            //balancing the tree
            while(temp != null && temp.getNumItems() > temp.getMaxItems()){
                temp = split(temp);
            }
            return;
        }
        //otherwise, the key is duplicate
        if(temp.getChild(theNode.index + 1) == null){//there is no node at the desired location
            //only happens at an external node
           temp.insertItem(theNode.index + 1, new Item(key, element));
           ++size;
           //balancing the tree
            while(temp != null && temp.getNumItems() > temp.getMaxItems()){
                temp = split(temp);
            }
            return;
        }
        //otherwise, there is a child at the desired location
        temp = temp.getChild(theNode.index + 1);
        //left child all the way down, to get to the in-order successor
        while(temp.getChild(0) != null){
            temp = temp.getChild(0);
        }
        temp.insertItem(0, new Item(key, element));
        ++size;
        //balancing the tree
        while(temp != null && temp.getNumItems() > temp.getMaxItems()){
            temp = split(temp);
        }
    }
    
    /**
     * 
     * @param badNode
     * @param index of badNode in its parent's arrays
     * @returns parent of the split node
     * balances the tree at badNode, pushes the issue upward.
     * this assumes MAXITEMS == 3 and it splits the node at index 2
     */
    private TFNode split(TFNode badNode){
        if(badNode.getNumItems() <= badNode.getMaxItems()){
            //everything is as it should be
            return null;
        }
        TFNode right = new TFNode();
        right.addItem(0, badNode.getItem(3));
        if(badNode.getChild(3) != null){
             right.setChild(0, badNode.getChild(3));
            (right.getChild(0)).setParent(right);
        }
        if(badNode.getChild(4) != null){//should be an equivalent condition to
            //the previous, since all external nodes are the same depth
            right.setChild(1, badNode.getChild(4));
            (right.getChild(1)).setParent(right);
        }
        if(badNode == treeRoot){
            treeRoot = new TFNode();
            treeRoot.addItem(0, badNode.getItem(2));
            treeRoot.setChild(0, badNode);
            treeRoot.setChild(1, right);
            right.setParent(treeRoot);
            badNode.setParent(treeRoot);
        }
        else{
            int i = 0;//index of badNode in its Parent's child array
            while(((badNode.getParent()).getChild(i)) != badNode){
                ++i;
            }
            (badNode.getParent()).insertItem(i, badNode.getItem(2));
            (badNode.getParent()).setChild(i + 1, right);
            right.setParent(badNode.getParent());
        }
        badNode.deleteItem(3);
        badNode.setChild(4, null);
        badNode.deleteItem(2);
        badNode.setChild(3, null);
        return badNode.getParent();
    }

    /**
     * Searches dictionary to determine if key is present, then
     * removes and returns corresponding object
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */
    @Override
    public Object removeElement(Object key) throws ElementNotFoundException {
        TFNodeIndex theNode = find(key);
        if(!theNode.hasItem){
           throw new ElementNotFoundException("Threw Exception at node: " + theNode);
        }
        Item result = ((theNode.node).getItem(theNode.index));
        if(size == 1){
            treeRoot = null;
            --size;
            return result.element();
        }
        
        if((theNode.node).getChild(0) == null){//theNode.node is an external node
            (theNode.node).removeItem(theNode.index);
            --size;
            TFNode temp = theNode.node;
            while(temp.getNumItems() == 0){
                temp = underflow(temp);
            }
            return result.element();
        }
        
        //put in-order successor at the removal location
        TFNode temp = (theNode.node).getChild(theNode.index + 1);
        while(temp.getChild(0) != null){//not an external node
            temp = temp.getChild(0);
        }
        (theNode.node).replaceItem(theNode.index, temp.getItem(0));
        temp.removeItem(0);
        --size;
        while (temp.getNumItems() == 0 ){
            temp = underflow(temp);
        }
        return result.element();
    }
    
    /**
     * 
     * @param node which has no elements
     * @param i is the index of node in its parent's array
     * This function assumes node has a parent and that node is empty
     */
    private TFNode underflow(TFNode node){
        if(node == treeRoot){
            treeRoot = treeRoot.getChild(0);
            treeRoot.setParent(null);
            return treeRoot;
        }
        TFNode parent = node.getParent();
        int i = 0;//index of node in parent's child array
        while((parent.getChild(i)) != node){
            ++i;
        }
        TFNode leftSib = null;
            if(i > 0){//if a left sibling exists
                leftSib = parent.getChild(i-1);
            }
        TFNode rightSib = null;
            if(i < parent.getNumItems()){//if a right sibling exists
                rightSib = parent.getChild(i+1);
            }
        
        //left transfer        
        if(leftSib!= null && leftSib.getNumItems() > 1){//left sibling has elements to spare
            //put the correct element in the currently empty node
            node.insertItem(0, parent.getItem(i-1));
            //put the last element of node's left sibling into the correct place in the parent's array
            parent.replaceItem(i-1, leftSib.getItem(leftSib.getNumItems() - 1));
            node.setChild(0, leftSib.getChild(leftSib.getNumItems()));
            if(node.getChild(0) != null){
                (node.getChild(0)).setParent(node);
            }
            //remove the last element of node's left sibling
            leftSib.setChild(leftSib.getNumItems(), null);
            leftSib.deleteItem(leftSib.getNumItems() -1);
            return parent;
        }
        //right transfer
        if(rightSib != null && rightSib.getNumItems() > 1){//right sibling has elements to spare
            //put the correct element in the currently empty node
            node.addItem(0, parent.getItem(i));
            //put the first element of node's right sibling into the correct place in the parent's array
            parent.replaceItem(i, rightSib.getItem(0));
            node.setChild(1, rightSib.getChild(0));
            if(node.getChild(1) != null){
                node.getChild(1).setParent(node);
            }
            //remove the last element of node's left sibling
            rightSib.removeItem(0);
            return parent;
        }
        //perform fusion
            //right fusion, if node has a right sibling
            if(rightSib != null){
                rightSib.insertItem(0, parent.getItem(i));
                parent.removeItem(i);
                rightSib.setChild(0, node.getChild(0));
                if(rightSib.getChild(0) != null){
                    (rightSib.getChild(0)).setParent(rightSib);
                }
            }
            else{
                //we know left fusion is possible, now
                leftSib.insertItem(leftSib.getNumItems(), parent.getItem(i-1));
                parent.deleteItem(i-1);
                
                leftSib.setChild(leftSib.getNumItems(), node.getChild(0));
                parent.setChild(i, null);
                if(leftSib.getChild(leftSib.getNumItems()) != null){
                    (leftSib.getChild(leftSib.getNumItems())).setParent(leftSib);
                }
            }
            return parent;       
        }
    

    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        final int TEST_SIZE = 10000;


        for (int i = 0; i < TEST_SIZE; i++) {
            myTree.insertElement(1,1);
                      //myTree.printAllElements();
                     myTree.checkTree();
        }
        
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            System.out.println("removing " + i);
            int out = (Integer) myTree.removeElement(1);
            if(i==240){
                myTree.printAllElements();
            }
            myTree.checkTree();
            if (out != 1) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > TEST_SIZE -15  ) {
                myTree.printAllElements();
            }
        }
        System.out.println("done");

    /*    for(int i = 0; i < 255; ++i){ 
            myTree.insertElement(1,1);
            System.out.println(myTree.size);
        }
        myTree.printAllElements();
        int i = 0;
        while(myTree.treeRoot != null){
            System.out.println(i + " " + myTree.size());
            myTree.removeElement(1);
            ++i;
            myTree.checkTree();
        }*/
    
        Comparator myCompTest = new IntegerComparator();
        TwoFourTree myTreeTest = new TwoFourTree(myCompTest);
        myTreeTest = new TwoFourTree(myCompTest);

        final int TEST_SIZE_OFFICIAL = 10000;
        final int TEST_RANGE_OFFICIAL = 1000;

        Random rand = new Random(12345);

        System.out.println("==================================");
        System.out.println("==================================");
        System.out.println("========= ADDING =================");
        System.out.println("==================================");
        System.out.println("==================================");
        for (int i = 0; i < TEST_SIZE_OFFICIAL; i++) {
            int randInt = rand.nextInt(TEST_RANGE_OFFICIAL);
            myTreeTest.insertElement(randInt, randInt);
            System.out.println("=== Inserted " + randInt);
            if (myTreeTest.size() < 50)
            {
                myTreeTest.printAllElements();
            }
            myTreeTest.checkTree();
        }

        System.out.println("==================================");
        System.out.println("==================================");
        System.out.println("========= REMOVING ===============");
        System.out.println("==================================");
        System.out.println("==================================");

        rand = new Random(12345);
        for (int i = 0; i < TEST_SIZE_OFFICIAL; i++) {
            int randInt = rand.nextInt(TEST_RANGE_OFFICIAL);
            int out = (Integer) myTreeTest.removeElement(randInt);
            System.out.println("=== Removed " + randInt);
            if (myTreeTest.size() < 50)
            {
                myTreeTest.printAllElements();
            }

            if (out != randInt) {
                throw new TwoFourTreeException("main: wrong element removed");
            }

            myTreeTest.checkTree();
        }

        System.out.println("done");
    }

    public void printAllElements() {
        int indent = 0;
        if (root() == null) {
            System.out.println("The tree is empty");
        }
        else {
            printTree(root(), indent);
        }
    }

    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        printTFNode(start);
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
    }

    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        System.out.println();
    }

    // checks if tree is properly hooked up, i.e., children point to parents
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }

    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        if (start.getParent() != null) {
            TFNode parent = start.getParent();
            int childIndex;
            for (childIndex = 0; childIndex <= parent.getNumItems(); childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // if child wasn't found, print problem
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems(); childIndex++) {
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                }
                else {
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }
            }
        }

        int numChildren = start.getNumItems() + 1;
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }
    }
}

