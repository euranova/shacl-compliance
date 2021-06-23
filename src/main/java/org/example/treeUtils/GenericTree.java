package org.example.treeUtils;

import com.google.gson.*;
import lombok.Data;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.*;


@Data
public class GenericTree<T> {

    private GenericTreeNode<T> root;

    public int getNumberOfNodes() {
        int numberOfNodes = 0;

        if (root != null) {
            numberOfNodes = auxiliaryGetNumberOfNodes(root) + 1; //1 for the root!
        }

        return numberOfNodes;
    }

    private int auxiliaryGetNumberOfNodes(GenericTreeNode<T> node) {
        int numberOfNodes = node.getNumberOfChildren();

        for (GenericTreeNode<T> child : node.getChildren()) {
            numberOfNodes += auxiliaryGetNumberOfNodes(child);
        }

        return numberOfNodes;
    }

    public boolean exists(T dataToFind) {
        return (find(dataToFind) != null);
    }

    public GenericTreeNode<T> find(T dataToFind) {
        GenericTreeNode<T> returnNode = null;

        if (root != null) {
            returnNode = auxiliaryFind(root, dataToFind);
        }

        return returnNode;
    }

    private GenericTreeNode<T> auxiliaryFind(GenericTreeNode<T> currentNode, T dataToFind) {
        GenericTreeNode<T> returnNode = null;
        int i = 0;

        if (currentNode.getData().equals(dataToFind)) {
            returnNode = currentNode;
        } else if (currentNode.hasChildren()) {
            i = 0;
            while (returnNode == null && i < currentNode.getNumberOfChildren()) {
                returnNode = auxiliaryFind(currentNode.getChildAt(i), dataToFind);
                i++;
            }
        }

        return returnNode;
    }

    public boolean isEmpty() {
        return (root == null);
    }

    public List<GenericTreeNode<T>> build(GenericTreeTraversalOrderEnum traversalOrder) {
        List<GenericTreeNode<T>> returnList = null;

        if (root != null) {
            returnList = build(root, traversalOrder);
        }

        return returnList;
    }

    public List<GenericTreeNode<T>> build(GenericTreeNode<T> node, GenericTreeTraversalOrderEnum traversalOrder) {
        List<GenericTreeNode<T>> traversalResult = new ArrayList<GenericTreeNode<T>>();

        if (traversalOrder == GenericTreeTraversalOrderEnum.PRE_ORDER) {
            buildPreOrder(node, traversalResult);
        } else if (traversalOrder == GenericTreeTraversalOrderEnum.POST_ORDER) {
            buildPostOrder(node, traversalResult);
        }

        return traversalResult;
    }

    private void buildPreOrder(GenericTreeNode<T> node, List<GenericTreeNode<T>> traversalResult) {
        traversalResult.add(node);

        for (GenericTreeNode<T> child : node.getChildren()) {
            buildPreOrder(child, traversalResult);
        }
    }

    private void buildPostOrder(GenericTreeNode<T> node, List<GenericTreeNode<T>> traversalResult) {
        for (GenericTreeNode<T> child : node.getChildren()) {
            buildPostOrder(child, traversalResult);
        }

        traversalResult.add(node);
    }

    public Map<GenericTreeNode<T>, Integer> buildWithDepth(GenericTreeTraversalOrderEnum traversalOrder) {
        Map<GenericTreeNode<T>, Integer> returnMap = null;

        if (root != null) {
            returnMap = buildWithDepth(root, traversalOrder);
        }

        return returnMap;
    }

    public Map<GenericTreeNode<T>, Integer> buildWithDepth(GenericTreeNode<T> node, GenericTreeTraversalOrderEnum traversalOrder) {
        Map<GenericTreeNode<T>, Integer> traversalResult = new LinkedHashMap<GenericTreeNode<T>, Integer>();

        if (traversalOrder == GenericTreeTraversalOrderEnum.PRE_ORDER) {
            buildPreOrderWithDepth(node, traversalResult, 0);
        } else if (traversalOrder == GenericTreeTraversalOrderEnum.POST_ORDER) {
            buildPostOrderWithDepth(node, traversalResult, 0);
        }

        return traversalResult;
    }

    private void buildPreOrderWithDepth(GenericTreeNode<T> node, Map<GenericTreeNode<T>, Integer> traversalResult, int depth) {
        traversalResult.put(node, depth);

        for (GenericTreeNode<T> child : node.getChildren()) {
            buildPreOrderWithDepth(child, traversalResult, depth + 1);
        }
    }

    private void buildPostOrderWithDepth(GenericTreeNode<T> node, Map<GenericTreeNode<T>, Integer> traversalResult, int depth) {
        for (GenericTreeNode<T> child : node.getChildren()) {
            buildPostOrderWithDepth(child, traversalResult, depth + 1);
        }

        traversalResult.put(node, depth);
    }

    public String toString() {
        /*
        We're going to assume a pre-order traversal by default
         */

        String stringRepresentation = "";

        if (root != null) {
            stringRepresentation = String.valueOf(build(GenericTreeTraversalOrderEnum.PRE_ORDER));

        }

        return stringRepresentation;
    }

    public String toStringWithDepth() {
        /*
        We're going to assume a pre-order traversal by default
         */

        String stringRepresentation = "";

        if (root != null) {
            stringRepresentation = String.valueOf(buildWithDepth(GenericTreeTraversalOrderEnum.PRE_ORDER));
        }

        return stringRepresentation;
    }

    public JSONObject treeToJSON() {
        JSONObject json = new JSONObject();

        JSONArray jsonArrayChildren = new JSONArray();
        for (GenericTreeNode<T> subnode : root.getChildren()) {
            jsonArrayChildren.add(getChildValue(subnode));
            json.put(String.valueOf(subnode.getParent().getData()), jsonArrayChildren);
        }
        return json;
    }

    private JSONObject getChildValue(GenericTreeNode<T> node) {

        JSONObject json = new JSONObject();
        JSONArray childArray = new JSONArray();
        if (node.hasChildren()) {
            for (int idx = 0; idx < node.getNumberOfChildren(); idx++) {
                childArray.add(getChildValue(node.getChildAt(idx)));
            }
        }
        json.put(String.valueOf(node.getData()), childArray);
        return json;
    }

    public String toJsonTree() {
        JsonArray arrayTree= getChildValueTree(root);
        Gson gson = new Gson();
        String json = gson.toJson(arrayTree);
        return json;
    }

    private JsonArray getChildValueTree(GenericTreeNode<T> node){
        JsonArray nodeChild = new JsonArray();
        for (GenericTreeNode<T> subnode : node.getChildren()) {
            JsonObject subNodeObj = new JsonObject();
            subNodeObj.addProperty("text",String.valueOf(subnode.getData()));
            if(subnode.hasChildren()){
                subNodeObj.add("nodes", getChildValueTree(subnode));
            }
            nodeChild.add(subNodeObj);
        }
        return nodeChild;
    }
}

