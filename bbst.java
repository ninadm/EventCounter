import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class bbst {
	
	public enum Color {
        RED,
        BLACK
    }
	
	public enum Arguments {
        INCREASE,
        REDUCE,
        COUNT,
        INRANGE,
        NEXT,
        PREVIOUS,
        QUIT,
        INVALID
    }
	
	private static Node root;
	private Node successor;
	private Node predecessor;
	private int totalInRangeCount;
	private Node lastInsertedNode;
	private Node lastDeletedNode;
	private static List<EventPair> eventPairs = new ArrayList<EventPair>();
	private class Node {
		
		Node left, right, parent;
		Color color;
		int ID;
		int count;
		boolean isNull;
	}
	
	private static class EventPair {
		int ID;
		int count;
		
		EventPair(int id, int count) {
			this.ID = id;
			this.count = count;
		}
	}
	
	static int numberOfPairs = 0;
	
	public static void main(String[] args) {
		bbst ec = new bbst();
		System.out.println("Initializing Tree...");
		initializeEventCounter(args);
		root = ec.sortedArrayToBST(eventPairs, 0 , numberOfPairs - 1, root);
		List<Node> listToColor = ec.findDeepestNodes(root);
		ec.changeColor(listToColor, Color.RED);
		ec.showMenu();
	}
	
	/**
	 * Creates a new Node
	 * @param ep Event Pair
	 * @param c Color Information
	 * @param parent
	 * @return
	 */
	private Node createNode(EventPair ep, Color c, Node parent) {
        Node node = new Node();
        node.ID = ep.ID;
        node.count = ep.count;
        node.color = c;
        node.left = createNullLeaf(node);
        node.right = createNullLeaf(node);
        node.parent = parent;
        return node;
    }
	
	/**
	 * Create a new null Node
	 * @param parent
	 * @return
	 */
    private Node createNullLeaf(Node parent) {
        Node leaf = new Node();
        leaf.color = Color.BLACK;
        leaf.isNull = true;
        leaf.parent = parent;
        leaf.count = Integer.MAX_VALUE;
        leaf.ID = Integer.MAX_VALUE;
        return leaf;
    }
	
	/**
	 * Show command formats and accept commands on System.in
	 */
	private void showMenu() {
	    Scanner sc = new Scanner(System.in);
	    Arguments argument = Arguments.INVALID;
	    System.out.println("Enter command of your choice in the correct format");
	    do
	    {
	    	String command = sc.nextLine();
	    	String[] arguments = command.split(" +");
	    	argument = validateAndReturnType(arguments);
	    	switch(argument) {
		    	case INCREASE:
		    		increase(arguments);
		    		break;
		    	case REDUCE:
		    		reduce(arguments);
		    		break;
				case COUNT:
					count(arguments);
					break;
				case INRANGE:
					inRange(arguments);
					break;
				case NEXT:
					next(arguments);
					break;
				case PREVIOUS:
					previous(arguments);
					break;
				case QUIT:
					System.exit(0);
				case INVALID:
					System.out.println("Please enter a valid command");
					break;
				default:
					System.out.println("Invalid command. Enter a command in the correct format");
	    	}
	    } while(argument != Arguments.QUIT);
	}
	
	/**
	 * Reads file and make EventPairList
	 * @param contains command line arguments
	 */
	private static void initializeEventCounter(String[] args) {
		File inFile = null;
		if (0 < args.length) {
		   inFile = new File(args[0]);
		} else {
		   System.err.println("Invalid arguments count:" + args.length);
		   System.exit(0);
		}
		BufferedReader br = null;
	    try {
	        String sCurrentLine;
	        br = new BufferedReader(new FileReader(inFile));
	        numberOfPairs = Integer.parseInt(br.readLine());
	        int readCount = 0;
	        while ((sCurrentLine = br.readLine()) != null && readCount < numberOfPairs) {
	        	String[] pair = sCurrentLine.split(" ");
	            if(Integer.parseInt(pair[1]) > 0)
	            	eventPairs.add(new EventPair(Integer.parseInt(pair[0]), Integer.parseInt(pair[1])));
	            readCount++;
	        }
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    } 
	    finally {
	        try {
	            if (br != null)br.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

	/**
	 * Creates a Balanced BST
	 * @param allEvents - all pairs of ID and count
	 * @param start - start index
	 * @param end - end index
	 * @param root1 - parent node
	 * @return current added node
	 */
	private Node sortedArrayToBST(List<EventPair> allEvents, int start, int end, Node root1) {
		if (start > end) {
            return createNullLeaf(root1);
        }
		int mid = (start + end) / 2;
		Node node = createNode(allEvents.get(mid), Color.BLACK, root1);
		node.left = sortedArrayToBST(allEvents, start, mid-1, node);
		node.right = sortedArrayToBST(allEvents, mid+1, end, node);
	    return node;
	}
	
	/**
	 * Finds Nodes at max level
	 * @param root
	 * @return
	 */
	private List findDeepestNodes(Node root)
	 {
		 Object[] levelInformation = new Object[2];
		 levelInformation[0] = 0;
		 levelInformation[1] = new ArrayList();
		 findDeepestNodes(root, 1, levelInformation);
		 return (List) levelInformation[1];
	 }

	/**
	 * 
	 * @param root
	 * @param level
	 * @param levelInformation
	 */
	 private void findDeepestNodes(Node root, int level, Object[] levelInformation)
	 {
	    if (root == null)
	       return;
	    if((Integer)levelInformation[0]<=level)
	    {
	    	if((Integer)levelInformation[0] < level)
	    		((List)levelInformation[1]).clear();
	    	levelInformation[0]=level;
	    	((List)levelInformation[1]).add(root);
	    }
	    findDeepestNodes(root.left, level+1, levelInformation);
	    findDeepestNodes(root.right, level+1, levelInformation); 
	 }
	 
	 /**
	  * Changes color of list of nodes
	  * @param listToColor
	  * @param red
	  */
	 private void changeColor(List<Node> listToColor, Color red) {
			for(Node n: listToColor)
				n.color = red;
	 }
	
	/**
	 * Validates number of arguments according to type of command
	 * @param arguments
	 * @return
	 */
	private Arguments validateAndReturnType(String[] arguments) {
		if(arguments == null || arguments.length == 0 || arguments.length > 3)
    		return Arguments.INVALID;
		String arg1 = arguments[0].toLowerCase();
		if(arg1.equals("increase") || arg1.equals("reduce") || arg1.equals("inrange")){
			if(arguments.length != 3)
				return Arguments.INVALID;
			else
			{
				if(arg1.equals("increase"))
					return Arguments.INCREASE;
				else if(arg1.equals("reduce"))
					return Arguments.REDUCE;
				else if(arg1.equals("inrange"))
					return Arguments.INRANGE;
			}
		} else if(arg1.equals("next") || arg1.equals("previous") || arg1.equals("count")) {
			if(arguments.length != 2)
				return Arguments.INVALID;
			else
			{
				if(arg1.equals("next"))
					return Arguments.NEXT;
				else if(arg1.equals("previous"))
					return Arguments.PREVIOUS;
				else if(arg1.equals("count"))
					return Arguments.COUNT;
			}
		} else if(arg1.equals("quit")) {
			return Arguments.QUIT;
		} else
			return Arguments.INVALID;
			
		return null;
	}

	/**
	 * Print Count of provided ID
	 * @param arguments
	 */
	private void count(String[] arguments) {
		try { 
	        int ID = Integer.parseInt(arguments[1]);
	        Node curr = searchID(root, ID);
	        if(curr != null)
	        	System.out.printf("%d\n", curr.count);
	        else
	        	System.out.println("0");
	    } catch(NumberFormatException e) { 
	        System.out.println("Argument not an Integer");; 
	    }
	}

	/**
	 * Seach for Node with Given ID
	 * @param root
	 * @param ID
	 * @return
	 */
	private Node searchID(Node root, int ID) {
		if(root == null || root.isNull) {
			return null;
		} else {
			if(root.ID > ID) {
				return searchID(root.left, ID);
			} else if(root.ID < ID)
				return searchID(root.right, ID);
			else
				return root;
		}
	}

	/**
	 * Reduce function which searches for the given ID and subtracts the count by the given value and deletes node if count 
	 * goes below 1
	 * @param arguments
	 */
	private void reduce(String[] arguments) {
		try { 
			lastDeletedNode = null;
			int ID = Integer.parseInt(arguments[1]);
	        int m = Integer.parseInt(arguments[2]);
	        Node curr = searchID(root, ID);
	        if(curr != null)
	        {
	        	curr.count -= m;
	        	if(curr.count > 0)
	        		System.out.printf("%d\n", curr.count);
	        	else
	        	{
	        		delete(root, ID);
	        		System.out.println("0");
	        	}
	        }
	        else
	        	System.out.println("0");
	    } catch(NumberFormatException e) { 
	        System.out.println("Argument not an Integer");
	    }
	}
	
	/**
	 * 
	 * @param curr
	 * @param ID
	 * @return
	 */

	private Node delete(Node curr, int ID) {
		AtomicReference<Node> rootRef = new AtomicReference<>();
        delete(curr, ID, rootRef);
        if(rootRef.get() == null) {
            return curr;
        } else {
            return rootRef.get();
        }
	}
	
	/**
	 * Delete Node with given ID by searching for it recursively
	 * @param curr
	 * @param ID
	 * @param rootRef
	 */

	private void delete(Node curr, int ID, AtomicReference<Node> rootRef) {
		if(curr == null || curr.isNull) {
            return;
        }
        if(curr.ID == ID) {
            if(curr.right.isNull || curr.left.isNull) {
                deleteDegreeOneChild(curr, rootRef);
            } else {
                Node n = findSmallest(curr.right);
            	curr.ID = n.ID;
                curr.count = n.count;
                delete(curr.right, n.ID, rootRef);
            }
        }
        if(curr.ID < ID) {
            delete(curr.right, ID, rootRef);
        } else {
            delete(curr.left, ID, rootRef);
        }
	}
	
	/**
	 * Find smallest node in the right subtree which will be the leftmost node
	 * @param curr
	 * @return
	 */
	private Node findSmallest(Node curr) {
        Node prev = null;
        while(curr != null && !curr.isNull) {
            prev = curr;
            curr = curr.left;
        }
        return prev != null ? prev : curr;
    }
	
	/**
	 * Recoloring and deletion of a node that has 1 or 0 children
	 * @param curr
	 * @param rootRef
	 */

	private void deleteDegreeOneChild(Node curr, AtomicReference<Node> rootRef) {
		Node child = curr.right.isNull ? curr.left : curr.right;
        replaceNode(curr, child, rootRef);
        if(curr.color == Color.BLACK) {
            if(child.color == Color.RED) {
                child.color = Color.BLACK;
            } else {
                adjustRootDeficiency(child, rootRef);
            }
        }
	}
	
	/**
	 * Replace 2 given nodes with each other depending on if it is right or left subtree
	 * @param curr
	 * @param child
	 * @param rootRef
	 */
	private void replaceNode(Node curr, Node child, AtomicReference<Node> rootRef) {
		child.parent = curr.parent;
        if(curr.parent == null) {
        	rootRef.set(child);
        }
        else {
            if(isLeftChild(curr)) {
            	curr.parent.left = child;
            } else {
            	curr.parent.right = child;
            }
        }
	}
	
	/**
	 * Change root reference if root is deleted
	 * @param deficientNode
	 * @param rootRef
	 */
	private void adjustRootDeficiency(Node deficientNode, AtomicReference<Node> rootRef) {
		if(deficientNode.parent == null) {
            rootRef.set(deficientNode);
            return;
        }
        adjustDeficientRedSiblingNode(deficientNode, rootRef);
	}
	
	/**
	 * Do a right or a left rotate depending on if the sibling is left child or right child and proceed to further cases
	 * @param deficientNode
	 * @param rootRef
	 */
	private void adjustDeficientRedSiblingNode(Node deficientNode, AtomicReference<Node> rootRef) {
		Node sibling = getSibling(deficientNode);
        if(sibling.color == Color.RED) {
            if(isLeftChild(sibling)) {
                rotateRight(sibling, true);
            } else {
            	rotateLeft(sibling, true);
            }
            if(sibling.parent == null) {
                rootRef.set(sibling);
            }
        }
        adjustDeficientBlackSiblingNode(deficientNode, rootRef);
	}
	
	/**
	 * Change siblings color to become red and push the problem further up nd start checking all cases
	 * @param deficientNode
	 * @param rootRef
	 */
	private void adjustDeficientBlackSiblingNode(Node deficientNode, AtomicReference<Node> rootRef) {
		Node sibling = getSibling(deficientNode);
        if(deficientNode.parent.color == Color.BLACK && sibling.color == Color.BLACK && sibling.left.color == Color.BLACK
                && sibling.right.color == Color.BLACK) {
        	sibling.color = Color.RED;
            adjustRootDeficiency(deficientNode.parent, rootRef);
        } else {
            adjustRedParentNode(deficientNode, rootRef);
        }
	}
	
	/**
	 * Exchange the color of parent and sibling to maintain red black tree properties
	 * @param deficientNode
	 * @param rootRef
	 */
	private void adjustRedParentNode(Node deficientNode, AtomicReference<Node> rootRef) {
		Node sibling = getSibling(deficientNode);
        if(deficientNode.parent.color == Color.RED && sibling.color == Color.BLACK && sibling.left.color == Color.BLACK
        && sibling.right.color == Color.BLACK) {
        	sibling.color = Color.RED;
        	deficientNode.parent.color = Color.BLACK;
            return;
        } else {
        	adjustRedChildBlackSibling(deficientNode, rootRef);
        }
	}
	
	/**
	 * Do a rotation around sibling depending on if its a right child or left child.
	 * @param deficientNode
	 * @param rootRef
	 */
	private void adjustRedChildBlackSibling(Node deficientNode, AtomicReference<Node> rootRef) {
		Node sibling = getSibling(deficientNode);
		if(sibling.color == Color.BLACK) {
            if (isLeftChild(deficientNode) && sibling.right.color == Color.BLACK && sibling.left.color == Color.RED) {
                rotateRight(sibling.left, true);
            } else if (!isLeftChild(deficientNode) && sibling.left.color == Color.BLACK && sibling.right.color == Color.RED) {
                rotateLeft(sibling.right, true);
            }
        }
		adjustBlackSiblingRedChild(deficientNode, rootRef);
	}
	
	/**
	 * Left or right rotation centered at the parent and color changes as below
	 * @param deficientNode
	 * @param rootRef
	 */
	private void adjustBlackSiblingRedChild(Node deficientNode, AtomicReference<Node> rootRef) {
		Node sibling = getSibling(deficientNode);
		sibling.color = sibling.parent.color;
		sibling.parent.color = Color.BLACK;
        if(isLeftChild(deficientNode)) {
        	sibling.right.color = Color.BLACK;
            rotateLeft(sibling, false);
        } else {
        	sibling.left.color = Color.BLACK;
            rotateRight(sibling, false);
        }
        if(sibling.parent == null) {
        	rootRef.set(sibling);
        }
	}
	
	/**
	 * Increase the count of the node with given Id or insert if it doesnt exist
	 * @param arguments
	 */
	private void increase(String[] arguments) {
		try { 
			lastInsertedNode = null;
			int ID = Integer.parseInt(arguments[1]);
	        int m = Integer.parseInt(arguments[2]);
	        Node curr = searchID(root, ID);
	        if(curr != null)
	        {
	        	curr.count += m;
	        	System.out.printf("%d\n", curr.count);
	        }
	        else
	        {
	        	insertNode(ID, m);
	        	System.out.printf("%d\n", lastInsertedNode.count);
	        }
	    } catch(NumberFormatException e) { 
	        System.out.println("Argument not an Integer");; 
	    }
	}
	
	/**
	 * 
	 * @param ID
	 * @param m
	 * @return
	 */
	private Node insertNode(int ID, int m) {
		return insertNode(null, root, new EventPair(ID, m));
	}
	
	/**
	 * Recursively look for the right place to insert and do rotations and color flips to maintain properties
	 * @param parent
	 * @param curr
	 * @param eventPair
	 * @return Node
	 */
	private Node insertNode(Node parent, Node curr, EventPair eventPair) {
		if(curr == null || curr.isNull)
		{
			if(parent != null)
			{
				lastInsertedNode =  createNode(eventPair, Color.RED, parent);
				return lastInsertedNode;
			}
			else
			{
				lastInsertedNode =  createNode(eventPair, Color.BLACK, parent);
				return lastInsertedNode;
			}
		}
		else
		{
			boolean toLeft = false;
			if(curr.ID > eventPair.ID)
			{
				Node n = insertNode(curr, curr.left, eventPair);
				if (n == curr.parent)
					return n;
				curr.left = n;
				toLeft = true;
			}
			else
			{
				Node n = insertNode(curr, curr.right, eventPair);
				if (n == curr.parent)
					return n;
				curr.right = n;
				toLeft = false;
			}
			if(toLeft) {
				if(curr.color == Color.RED && curr.left.color == Color.RED) {
					Node sibling = getSibling(curr);
					if(sibling == null || sibling.color == Color.BLACK) {
	                    if(isLeftChild(curr)) {
	                        rotateRight(curr, true);
	                    } else {
	                    	rotateRight(curr.left, false);
	                        curr = curr.parent;
	                        rotateLeft(curr, true);
	                    }
					} else
					{
						curr.color = Color.BLACK;
	                    sibling.color = Color.BLACK;
	                    if(curr.parent.parent != null) {
	                        curr.parent.color = Color.RED;
	                    }
					}
				}
			}
			else
			{
				if(curr.color == Color.RED && curr.right.color == Color.RED) {
	                Node sibling = getSibling(curr);
	                if(sibling == null || sibling.color == Color.BLACK) {
	                    if(!isLeftChild(curr)) {
	                    	rotateLeft(curr, true);
	                    } else {
	                    	rotateLeft(curr.right, false);
	                    	curr = curr.parent;
	                        rotateRight(curr, true);
	                    }
	                } else {
	                	curr.color = Color.BLACK;
	                    sibling.color = Color.BLACK;
	                    if(curr.parent.parent != null) {
	                    	curr.parent.color = Color.RED;
	                    }
	                }
	            }
			}
		}
		return curr;
	}
	
	/**
	 * Rotate left centered at Node curr
	 * @param curr
	 * @param colorChangeNeeded
	 */
	private void rotateLeft(Node curr, boolean colorChangeNeeded) {
		Node parent = curr.parent;
		curr.parent = parent.parent;
        if(parent.parent != null) {
            if(parent.parent.right == parent) {
            	parent.parent.right = curr;
            } else {
            	parent.parent.left = curr;
            }
        }
        Node left = curr.left;
        curr.left = parent;
        parent.parent = curr;
        parent.right = left;
        if(left != null) {
            left.parent = parent;
        }
        if(colorChangeNeeded) {
            curr.color = Color.BLACK;
            parent.color = Color.RED;
        }
	}
	
	/**
	 * Rotate Right centered at Node curr
	 * @param curr
	 * @param colorChangeNeeded
	 */
	private void rotateRight(Node curr, boolean colorChangeNeeded) {
		Node parent = curr.parent;
		curr.parent = parent.parent;
        if(parent.parent != null) {
            if(parent.parent.right == parent) {
            	parent.parent.right = curr;
            } else {
            	parent.parent.left = curr;
            }
        }
        Node right = curr.right;
        curr.right = parent;
        parent.parent = curr;
        parent.left = right;
        if(right != null) {
            right.parent = parent;
        }
        if(colorChangeNeeded) {
            curr.color = Color.BLACK;
            parent.color = Color.RED;
        }
	}
	
	/**
	 * check if given node is left of its parent
	 * @param curr
	 * @return
	 */
	private boolean isLeftChild(Node curr) {
		if(curr.parent.left == curr) {
            return true;
        } else {
            return false;
        }
	}
	
	/**
	 * get a non null sibling
	 * @param curr
	 * @return
	 */
	private Node getSibling(Node curr) {
		Node parent = curr.parent;
		if(isLeftChild(curr)) {
            return parent.right.isNull ? null : parent.right;
        } else {
            return parent.left.isNull ? null : parent.left;
        }
	}
	
	/**
	 * Print count of node events in range
	 * @param arguments
	 */
	private void inRange(String[] arguments) {
		try { 
	        int IDLow = Integer.parseInt(arguments[1]);
	        int IDHigh = Integer.parseInt(arguments[2]);
	        totalInRangeCount = 0;
	        countInRange(root, IDLow, IDHigh);
	        System.out.printf("%d\n",totalInRangeCount);
	    } catch(NumberFormatException e) { 
	        System.out.println("Argument not an Integer");; 
	    }
		
	}
	
	/**
	 * summation of counts of nodes in range
	 * @param curr
	 * @param IDLow
	 * @param IDHigh
	 */
	private void countInRange(Node curr, int IDLow, int IDHigh) {
		if(curr == null || curr.isNull)
			return;
		if(IDLow < curr.ID)
			countInRange(curr.left, IDLow, IDHigh);
		if(IDLow <= curr.ID && IDHigh >= curr.ID)
			totalInRangeCount += curr.count;
		if(IDHigh > curr.ID)
			countInRange(curr.right, IDLow, IDHigh);
	}
	
	/**
	 * Print successor of given ID
	 * @param arguments
	 */
	private void next(String[] arguments) {
		try {
	        int ID = Integer.parseInt(arguments[1]);
	        successor = null;
	        findSuccessor(root, ID);
	        if(successor == null)
	        	System.out.println("0 0");
	        else
	        	System.out.printf("%d %d\n", successor.ID, successor.count);
	    } catch(NumberFormatException e) { 
	        System.out.println("Argument not an Integer");; 
	    }
	}
	
	/**
	 * Find inorder successor
	 * @param root
	 * @param ID
	 */
	private void findSuccessor(Node root, int ID) {
		if(root == null || root.isNull) {
			return;
		}
		if(root.ID == ID) {
			// the minimum value in right subtree is successor
	        if (root.right != null && !root.right.isNull)
	        {
	            Node tmp = root.right;
	            while (tmp.left != null && !tmp.left.isNull)
	                tmp = tmp.left;
	            successor = tmp;
	        }
		}
		else if (root.ID > ID)
	    {
			successor = root;
	        findSuccessor(root.left, ID);
	    }
		else
			findSuccessor(root.right, ID);
	}
	
	/**
	 * print previous node to given ID
	 * @param arguments
	 */
	private void previous(String[] arguments) {
		try {
	        int ID = Integer.parseInt(arguments[1]);
	        predecessor = null;
	        findPredecessor(root, ID);
	        if(predecessor == null)
	        	System.out.println("0 0");
	        else
	        	System.out.printf("%d %d\n", predecessor.ID, predecessor.count);
	    } catch(NumberFormatException e) { 
	        System.out.println("Argument not an Integer");; 
	    }
	}
	
	/**
	 * Find in-order predecessor
	 * @param root
	 * @param ID
	 */
	private void findPredecessor(Node root, int ID) {
		if(root == null || root.isNull) {
			return;
		}
		if(root.ID == ID) {
	        if (root.left != null && !root.left.isNull)
	        {
	            Node tmp = root.left;
	            while (tmp.right != null && !tmp.right.isNull)
	                tmp = tmp.right;
	            predecessor = tmp;
	        }
		}
		else if (root.ID > ID)
			findPredecessor(root.left, ID);
		else
		{
			predecessor = root;
			findPredecessor(root.right, ID);
		}
	}
}
