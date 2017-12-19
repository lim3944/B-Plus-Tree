import java.io.Serializable;
import java.util.*;

public class TreeNode extends Node implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public ArrayList<Pair<Integer, Node>> data;	 //key, child
	TreeNode rightnode;
	Node temp;

	TreeNode(int x){
		super(x);
		rightnode = null;
		temp = null;
		data = new ArrayList<Pair<Integer, Node>>();
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
	
	@Override
	public int search(int key) {
		for(int i = 0 ;i < data.size(); i++) {
			if(data.get(i).first == key) {
				return 1;
			}
		}
		return 0;
	}
}
