import java.io.Serializable;

/*
 * Abstract Node class
 * Leafnode and Tree node will extend this class
 */

abstract class Node implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// arraylist will be 
	public Node right; 		     //right child or sibling
	public Node left;
	public Node parent;			 //parent node
	public int degree;			 //nuber of child
	
	abstract public boolean isLeaf();
	abstract public int search(int key);
	Node (int x) {
		parent = null;
		right = null;
		left = null;
		degree = x;
	}
	
}
