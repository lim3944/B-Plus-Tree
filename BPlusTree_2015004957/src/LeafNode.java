import java.io.Serializable;
import java.util.*;

/*
 * Extends Node Class
 */

public class LeafNode extends Node implements Serializable{

	static final long serialVersionUID = 1L;
	public ArrayList<Pair<Integer, Integer>> data;  // key, value
	
	LeafNode (int x) {
		super(x);
		left = null;
		data = new ArrayList<Pair<Integer, Integer>>();
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	@Override
	public int search(int key) {
		for(int i = 0 ;i < data.size(); i++) {
			if(data.get(i).first == key) {
				return data.get(i).second;
			}
		}
		return -1;
	}
	
}
