package com.adlab.balda.model.move_search.tree.model;

import java.util.Arrays;

public class LexicalTree {

    protected static final Character ROOT_DEFAULT_CHAR = '#';
    protected Node root;

    public LexicalTree() {
        root = new Node(ROOT_DEFAULT_CHAR, false);
    }

    public void addAllMissingChildrenByPath(boolean isLastAddedWord, char... path){
        Node lastNode = getTargetNode(root, true, path);
        lastNode.setWord(isLastAddedWord);
    }

    public boolean addChildByPath(char character, boolean isWord, char... path) {
        Node currentNode = getChildByPath(path);
        if (currentNode != null){
            currentNode.addChild(character, isWord);
            return true;
        }
        return false;
    }

    public Node getChildByPath(char... path) {
        if (path.length > 0) {
            return getTargetNode(root, false, path);
        }
        return null;
    }

    public Node getChildByPath(Character... path) {
        if (path.length > 0) {
            char[] array = new char[path.length];
            for (int i = 0; i < path.length; i++){
                array[i] = path[i];
            }
            return getTargetNode(root, false, array);
        }
        return null;
    }

    /**
     * Recursive tree search function for the node by specified path.
     * If there is no such path and element should not be added in tree returns null
     * @param currentNode - node to start search
     * @param addNodesIfAbsent - true if absent element should be added, false otherwise
     * @param path - keys of tree nodes
     * @return last Node in the specified path or null if it was not found
     */
    private Node getTargetNode(Node currentNode, boolean addNodesIfAbsent, char... path) {
        Node newCurrentNode = currentNode.getChildByKey(path[0]);
        if (newCurrentNode == null) {
            if (addNodesIfAbsent) {
                newCurrentNode = new Node(path[0], false);
                currentNode.addChild(newCurrentNode);
            } else {
                return null;
            }
        }
        if (path.length == 1) {
            return newCurrentNode;
        } else {
            char[] pathRange = Arrays.copyOfRange(path, 1, path.length);
            return getTargetNode(newCurrentNode, addNodesIfAbsent, pathRange);
        }
    }

    public void printAll(){
         printRecurseTree(root, "   ");
    }

    private void printRecurseTree(Node currentNode, String indent){
        System.out.print("\n"+indent + currentNode.getCharacter());

        if (currentNode.getChildren() != null) {
            for (Node child: currentNode.getChildren()) {
                printRecurseTree(child, indent + "   ");
            }
        }

    }


}
