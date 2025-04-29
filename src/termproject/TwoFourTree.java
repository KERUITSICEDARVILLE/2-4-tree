package termproject;

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
     * on whether the key exists in the tree or not. This function assumes that the
     * tree is nonempty.
     */
    private TFNodeIndex find (Object key) {
        TFNode temp = treeRoot;
        while(true){//stops, if the tree is finite in length
            int i = 0;
            //find the index of the desired child
            while(i < temp.getNumItems() && treeComp.isGreaterThan(key, (temp.getItem(i)).key())){
                ++i;
                }
            //if we encounter the desired key
            if(i < temp.getNumItems() && treeComp.isEqual(key, (temp.getItem(i)).key())){
                //we care about the ith Item of temp
                return new TFNodeIndex(temp, i, true);
            }
            if(temp.getChild(i) == null){
                //the data would be the ith child of temp, if it existed in the tree
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
        if(!theNode.hasItem){//if key is not already in the tree
            (theNode.node).insertItem(theNode.index, new Item(key, element));
            (theNode.node).setChild(theNode.index, null);//might not be necessary
            //balancing the tree
            TFNode temp = theNode.node;
            while(temp != null){
                temp = split(temp);
            }
            return;
        }
        //otherwise, the key is duplicate.
        TFNode temp = theNode.node;
        temp = temp.getChild(theNode.index +1);//logic might be wrong here.
        //we want the "right child" of the item we found.
        
        //left child all the way down, to get to the in-order successor
        while(temp.getChild(0) != null){
            temp = temp.getChild(0);
        }
        temp.insertItem(0, new Item(key, element));
            //balancing the tree. we could probably reorganize so this is written only once
            TFNode temp2 = theNode.node;
            while(temp2 != null){
                temp2 = split(temp2);
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
        if(badNode.getChild(4) != null){
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
            while(i < (badNode.getParent()).getNumItems() &&
                    treeComp.isGreaterThan((badNode.getItem(0)).key(), ((badNode.getParent()).getItem(i)).key())){
                ++i;
                }
            badNode.getParent().insertItem(i, badNode.getItem(2));
            badNode.getParent().setChild(i + 1, right);
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
        return null;
    }

    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        Integer myInt1 = new Integer(47);
        myTree.insertElement(myInt1, myInt1);
        Integer myInt2 = new Integer(83);
        myTree.insertElement(myInt2, myInt2);
        Integer myInt3 = new Integer(22);
        myTree.insertElement(myInt3, myInt3);

        Integer myInt4 = new Integer(16);
        myTree.insertElement(myInt4, myInt4);

        Integer myInt5 = new Integer(49);
        myTree.insertElement(myInt5, myInt5);

        Integer myInt6 = new Integer(100);
        myTree.insertElement(myInt6, myInt6);

        Integer myInt7 = new Integer(38);
        myTree.insertElement(myInt7, myInt7);

        Integer myInt8 = new Integer(3);
        myTree.insertElement(myInt8, myInt8);

        Integer myInt9 = new Integer(53);
        myTree.insertElement(myInt9, myInt9);

        Integer myInt10 = new Integer(66);
        myTree.insertElement(myInt10, myInt10);

        Integer myInt11 = new Integer(19);
        myTree.insertElement(myInt11, myInt11);

        Integer myInt12 = new Integer(23);
        myTree.insertElement(myInt12, myInt12);

        Integer myInt13 = new Integer(24);
        myTree.insertElement(myInt13, myInt13);

        Integer myInt14 = new Integer(88);
        myTree.insertElement(myInt14, myInt14);

        Integer myInt15 = new Integer(1);
        myTree.insertElement(myInt15, myInt15);

        Integer myInt16 = new Integer(97);
        myTree.insertElement(myInt16, myInt16);

        Integer myInt17 = new Integer(94);
        myTree.insertElement(myInt17, myInt17);

        Integer myInt18 = new Integer(35);
        myTree.insertElement(myInt18, myInt18);

        Integer myInt19 = new Integer(51);
        myTree.insertElement(myInt19, myInt19);

        myTree.printAllElements();
        System.out.println("done");
/*
        myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;


        for (int i = 0; i < TEST_SIZE; i++) {
            myTree.insertElement(new Integer(i), new Integer(i));
            //          myTree.printAllElements();
            //         myTree.checkTree();
        }
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            int out = (Integer) myTree.removeElement(new Integer(i));
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > TEST_SIZE - 15) {
                myTree.printAllElements();
            }
        }
        System.out.println("done");*/
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
            int childIndex = 0;
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

