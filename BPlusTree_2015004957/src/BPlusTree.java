import java.io.*;

public class BPlusTree implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int degree;
	Node root;
	
	public BPlusTree(int degree) {
		this.degree = degree;
		root = new LeafNode(degree);
	}
	
	/*
	 * Search whole tree 
	 * Return the value that fits with input key
	 */
	public int search(int key) {
		Node now = this.root;
		while(!now.isLeaf()) {
			boolean chk = false;
			for(int i = 0; i < ((TreeNode)now).data.size();i++){
				if(((TreeNode)now).data.get(i).first > key){
					now = ((TreeNode)now).data.get(i).second;
					chk = true;
					break;
				}
			}
			if(!chk)
				now = now.right;
		}
		return ((LeafNode)now).search(key);
	}
	
	/*
	 * Search whole tree 
	 * Return the leafnode that contain the value 
	 */
	public Node searchleaf(int key) {
		Node now = this.root;
		
		while(!now.isLeaf()) {
			boolean chk = false;
			for(int i = 0; i < ((TreeNode)now).data.size();i++){
				if(((TreeNode)now).data.get(i).first > key){
					now = ((TreeNode)now).data.get(i).second;
					chk = true;
					break;
				}
			}
			if(!chk)
				now = now.right;
		}
		return now;
	}
	
	
	/* 
	 * Insert the key and value pair in the fit postition
	 * If there are same key, insertion fail
	 */
	public boolean insert(int key, int value) {
		if(this.search(key) != -1) {
			return false;
		}
		
		Node now = searchleaf(key);
		
		Pair<Integer, Integer> temp = new Pair<Integer, Integer>();
		temp.first = key;
		temp.second = value;
		
		((LeafNode)now).data.add(temp);
		((LeafNode)now).data.sort((a1, a2) -> a1.first.compareTo(a2.first));
		
		if(((LeafNode)now).data.size() < now.degree) {
			return true;
		}
		else
			split(now);
		return true;
	}
	
	/* 
	 * If the node is full with the key or index
	 * Split the node into two node
	 */
	public void split(Node n) {
		Pair<Integer, Node> newparent = new Pair<Integer, Node>();
		TreeNode originparent = null;
		int mid = 0;
		if(n.degree == 2)
			mid = 0;
		else 
			mid = (n.degree)/2;
		
		//Leafnode split
		if(n.isLeaf()) {
			LeafNode newnode = new LeafNode(n.degree);
			
			for(int i = 0 ; i < mid; i++) {
				Pair<Integer, Integer> temp;
				temp = ((LeafNode)n).data.get(0);
				newnode.data.add(temp);
				((LeafNode)n).data.remove(0);
			}
			
			newparent.first = ((LeafNode)n).data.get(0).first;
			newparent.second = newnode;
			originparent = (TreeNode)((LeafNode)n).parent;
			
			//if current node is root
			if(originparent == null) {
				originparent = new TreeNode(n.degree);
				originparent.right = n;
				this.root = originparent;
				n.parent = originparent;
			}
			
			newnode.parent = originparent;
			
			//left child dealing
			if(((LeafNode)n).left != null) {
				((LeafNode)((LeafNode)n).left).right = newnode;
				newnode.left = ((LeafNode)n).left;
			}
			((LeafNode)n).left = newnode;
			newnode.right = n;
			
			originparent.data.add(newparent);
			originparent.data.sort((a1, a2) -> a1.first.compareTo(a2.first));
			
			if(originparent.data.size() < degree)
				return;
			else
				split(originparent);
		}
		//Treenode(non-leafnode) split
		else {
			TreeNode newnode = new TreeNode(n.degree);
			
			for(int i = 0 ; i < mid; i++) {
				Pair<Integer, Node> temp = null;
				temp = ((TreeNode)n).data.get(0);
				temp.second.parent = newnode;
				newnode.data.add(temp);
				((TreeNode)n).data.remove(0);
			}
			
			newparent.first = ((TreeNode)n).data.get(0).first;
			newparent.second = newnode;
			originparent = (TreeNode)((TreeNode)n).parent;
			
			//if current node is root
			if(originparent == null) {
				originparent = new TreeNode(n.degree);
				originparent.right = n;
				root = originparent;
				n.parent = originparent;
			}
			newnode.parent = originparent;
			
			//left sibling node deaing
			if(((TreeNode)n).left != null) {
				((TreeNode)((TreeNode)n).left).rightnode = newnode;
				newnode.left = ((TreeNode)n).left;
			}
			((TreeNode)n).left = newnode;
			newnode.rightnode = (TreeNode)n;
			
			newnode.right = ((TreeNode)n).data.get(0).second;
			((TreeNode)n).data.get(0).second.parent = newnode;
			((TreeNode)n).data.remove(0);	
			
			originparent.data.add(newparent);
			originparent.data.sort((a1, a2) -> a1.first.compareTo(a2.first));
			
			if(originparent.data.size() < degree)
				return ;
			else
				split(originparent);
		}
	}
	
	
	// use for finding parent position or key position
	public int searchpos(Node n, int key) {
		if(n.isLeaf()) {
			for(int i = 0 ; i < ((LeafNode)n).data.size(); i++) {
				if(((LeafNode)n).data.get(i).first == key)
					return i;
			}
			return -1;
		}
		for(int i = 0; i < ((TreeNode)n).data.size(); i++) {
			if(key < ((TreeNode)n).data.get(i).first)
				return i;
		}
		return ((TreeNode)n).data.size();
	}
	
	// Delete the value tha fit to the input key
	public boolean delete(int key) {
		if(this.search(key) == -1)
			return false;
		LeafNode dest = (LeafNode)searchleaf(key);
		
		//When Leafnode is root
		if(dest.parent == null) {
			int pos = searchpos(dest, key);
			dest.data.remove(pos);
			
			if(dest.data.size() == 0 ) {
				dest = null;
				root = dest;
			}
			return true;
		}
		//When deletion do not spoil the tree balance
		if(dest.data.size() > (degree-1)/2) {
			int pos = searchpos(dest,key);
			dest.data.remove(pos);
			
			// index change
			if(pos == 0) {
				TreeNode temp = (TreeNode)dest.parent;
				while(temp != null) {
					int temppos = searchpos(temp,key);
					if((temppos != 0) || (temppos == temp.data.size()))
						temppos--;
					if(temp.data.get(temppos).first == key)
						temp.data.get(temppos).first = dest.data.get(0).first;
					temp = (TreeNode)temp.parent;
				}
			}
			return true;
		}
		
		//When deletion spoil the tree balance
		else if(redistribute(dest, key)) {
			return true;
		}
		return false;
	}

	//Redistribution at LeafNode
	public boolean redistribute(Node n, int key) {
		LeafNode sibling;
		LeafNode now = (LeafNode)n;
		int pos = searchpos(now, key);
		
		//redistribution from left sibling
		if((now.left != null) && (now.left.parent == now.parent) && (((LeafNode)now.left).data.size() > (degree-1)/2)) {
			sibling = (LeafNode)now.left;
			int sibpos =  sibling.data.size()-1;
			
			now.data.remove(pos);
			
			int parentpos = searchpos(now.parent, key);
			if((parentpos != 0) || (parentpos == ((TreeNode)now.parent).data.size()))
				parentpos--;
			((TreeNode)now.parent).data.get(parentpos).first = sibling.data.get(sibpos).first;
			
			now.data.add(sibling.data.get(sibpos));
			sibling.data.remove(sibpos);
			
			now.data.sort((a1,a2)->a1.first.compareTo(a2.first));
			return true;
		}
		//redistribution from right sibling
		else if((now.right != null) && (now.right.parent == now.parent) && (((LeafNode)now.right).data.size() > (degree-1)/2)) {
			sibling = (LeafNode)now.right;
			int sibpos = 0;
			
			now.data.remove(pos);
			
			now.data.add(sibling.data.get(sibpos));
			sibling.data.remove(sibpos);
			now.data.sort((a1,a2)->a1.first.compareTo(a2.first));

			int parentpos = searchpos(now.parent, key);
			if((parentpos != 0) || (parentpos == ((TreeNode)now.parent).data.size()))
				parentpos--;
			((TreeNode)now.parent).data.get(parentpos).first = now.data.get(sibpos).first;
			
			parentpos = searchpos(now.parent, sibling.data.get(0).first);
			if((parentpos != 0) || (parentpos != ((TreeNode)now.parent).data.size()-1))
				parentpos--;
			((TreeNode)now.parent).data.get(parentpos).first = sibling.data.get(0).first;
			
			//index change
			if(pos == 0 ) {
				TreeNode temp = (TreeNode)now.parent;
				while(temp != null) {
					int temppos = searchpos(temp,key);
					if((temppos != 0) || (temppos == temp.data.size()))
						temppos--;
					if(temp.data.get(temppos).first == key)
						temp.data.get(temppos).first = now.data.get(0).first;
					temp = (TreeNode)temp.parent;
				}
			}
			return true;
		}
		else 
			return merge(now,key);
	}
	
	//redistribution at Nonleafnode
	public boolean redistribute(Node n) {
		TreeNode now = (TreeNode)n;
		TreeNode sibling;
		
		// redistribution from left sibling
		if((now.left != null) && (now.left.parent == now.parent) &&(((TreeNode)now.left).data.size() > (degree-1)/2)) {
			sibling = (TreeNode)now.left;
			int sibpos = sibling.data.size()-1;
			int parentpos = ((TreeNode)now.parent).data.size();
			
			//searching parent position
			for(int i = 0; i < ((TreeNode)now.parent).data.size(); i++) {
				if(((TreeNode)now.parent).data.get(i).second == now)
					parentpos = i;
			}
			if((parentpos != 0) || (parentpos == now.data.size()))
				parentpos--;
			int parentnum = ((TreeNode)now.parent).data.get(parentpos).first;
			
			Pair<Integer, Node> temp = new Pair<Integer, Node>();
			temp.first = parentnum;
			((TreeNode)now.parent).data.get(parentpos).first = sibling.data.get(sibpos).first;
			temp.second = sibling.right;
			temp.second.parent = now;
			sibling.right = sibling.data.get(sibpos).second;
			sibling.data.remove(sibpos);
			
			if(now.temp != null) {
				now.right = now.temp;
				now.data.clear();
				now.temp = null;
			}
			now.data.add(temp);
			now.data.sort((a1,a2)->a1.first.compareTo(a2.first));
			return true;
		}
		
		//redistribution from right sibling
		else if((now.rightnode != null) && (now.rightnode.parent == now.parent) && (((TreeNode)now.rightnode).data.size() > (degree-1)/2)) {
			sibling = (TreeNode)now.rightnode;
			int sibpos = 0;
			int parentpos = ((TreeNode)now.parent).data.size();
			
			//searching parent position
			for(int i = 0; i < ((TreeNode)now.parent).data.size(); i++) {
				if(((TreeNode)now.parent).data.get(i).second == now)
					parentpos = i;
			}
			if(parentpos == ((TreeNode)now.parent).data.size())
				parentpos--;
			
			int parentnum = ((TreeNode)now.parent).data.get(parentpos).first;
			
			Pair<Integer, Node> temp = new Pair<Integer, Node>();
			temp.first = parentnum;
			((TreeNode)now.parent).data.get(parentpos).first = sibling.data.get(sibpos).first;
			temp.second = now.right;
			now.right = sibling.data.get(sibpos).second;
			now.right.parent = now;
			sibling.data.remove(sibpos);
			
			if(now.temp != null) {
				temp.second = now.temp;
				now.data.clear();
				now.temp = null;
			}
			temp.second.parent = now;
			now.data.add(temp);
			now.data.sort((a1,a2)->a1.first.compareTo(a2.first));
			return true;
		}
		else
			return merge(now);
	}
	
	//Merge at Leafnode
	public boolean merge(Node n, int key) {
		LeafNode sibling;
		LeafNode now = (LeafNode)n;
		int pos = searchpos(now, key);
		
		//merge with left sibling
		if((now.left != null) && (now.parent == now.left.parent) && (((LeafNode)now.left).data.size() <= (degree-1)/2)) {
			sibling = (LeafNode)now.left;
			int parentpos = searchpos(now.parent,key);
			if ((parentpos != 0) || (parentpos == ((TreeNode)now.parent).data.size()))
				parentpos--;
			now.data.remove(pos);
			
			//merge
			for(int i = 0; i < sibling.data.size();) {
				now.data.add(sibling.data.get(0));
				sibling.data.remove(0);
			}
			
			now.data.sort((a1,a2)->a1.first.compareTo(a2.first));
			
			//right setting
			if(sibling.left != null) {
				sibling.left.right = now;
				now.left = sibling.left;
			}else
				now.left = null;
			
			//special case
			if(((TreeNode)now.parent).data.size() == 1){
				if(now.data.get(0).first != ((TreeNode)now.parent).data.get(0).first)
					((TreeNode)now.parent).data.remove(0);
				else
					((TreeNode)now.parent).data.get(0).second = null;
				((TreeNode)now.parent).temp = now;
				((TreeNode)now.parent).temp.parent = ((TreeNode)now.parent);
				return redistribute(now.parent);
			}
			// general case
			else {
				((TreeNode)now.parent).data.remove(parentpos);
				if(((TreeNode)now.parent).data.size() < (degree-1)/2)
					return redistribute(now.parent);
				return true;
			}	
		}
		
		//merge with right sibling
		else if((now.right != null) && (now.parent == now.right.parent) && (((LeafNode)now.right).data.size() <= (degree-1)/2)) {
			sibling = (LeafNode)now.right;
			
			now.data.remove(pos);
			
			//merge
			for(int i = 0; i < now.data.size();) {
				sibling.data.add(now.data.get(0));
				now.data.remove(0);
			}
			
			sibling.data.sort((a1,a2)->a1.first.compareTo(a2.first));
			
			//left setting
			if(now.left != null) {
				now.left.right = sibling;
				sibling.left = now.left;
			}
			else 
				sibling.left = null;
			
			//index change
			if(pos == 0) {
				TreeNode temp = (TreeNode)now.parent;
				while(temp != null) {
					int temppos = searchpos(temp,key);
					if(temppos != 0)
						temppos--;
					if(temp.data.get(temppos).first == key)
						temp.data.get(temppos).first = sibling.data.get(0).first;
					temp = (TreeNode)temp.parent;
				}
			}
			
			//special case
			if(((TreeNode)now.parent).data.size() == 1 ) {
				if(sibling.data.get(0).first != ((TreeNode)now.parent).data.get(0).first)
					((TreeNode)now.parent).data.remove(0);
				else
					((TreeNode)now.parent).data.get(0).second = null;
				((TreeNode)now.parent).temp = sibling;
				((TreeNode)now.parent).temp.parent = ((TreeNode)now.parent);
				return redistribute(now.parent);
			}
			//general case
			else {
				((TreeNode)now.parent).data.remove(0);
				if(((TreeNode)now.parent).data.size() < (degree-1)/2)
					return redistribute(now.parent);
				return true;
			}
		}
		else
			return false;
	}
	
	//Merge at Nonleafnode
	public boolean merge(Node n) {
		TreeNode sibling;
		TreeNode now = (TreeNode)n;
		
		//Merge with left sibling
		if((now.left != null) && (now.left.parent == now.parent) &&(((TreeNode)now.left).data.size() <= (degree-1)/2)) {
			sibling = (TreeNode)now.left;
			int parentpos = ((TreeNode)now.parent).data.size();
			
			//searching parent position
			for(int i = 0; i < ((TreeNode)now.parent).data.size(); i++) {
				if(((TreeNode)now.parent).data.get(i).second == now)
					parentpos = i;
			}
			if((parentpos != 0) || (parentpos == now.data.size()))
				parentpos--;
			int parentnum = ((TreeNode)now.parent).data.get(parentpos).first;
			
			Pair<Integer, Node> temp = new Pair<Integer, Node>();
			temp.first= parentnum;
			temp.second = sibling.right;
			
			if(now.temp != null) {
				now.right = now.temp;
				now.temp = null;
				now.data.clear();
			}
			
			//merge
			for(int i = 0; i < sibling.data.size();) {
				now.data.add(sibling.data.get(0));
				sibling.data.get(0).second.parent = now;
				sibling.data.remove(0);
			}
			temp.second.parent = now;
			now.data.add(temp);
			now.data.sort((a1,a2)->a1.first.compareTo(a2.first));
			
			//left setting
			if(sibling.left != null) {
				((TreeNode)sibling.left).rightnode = now;
				now.left = sibling.left;
			}else
				now.left = null;
			
			// special case
			if(((TreeNode)now.parent).data.size() == 1){
				if(now.data.get(0).first != ((TreeNode)now.parent).data.get(0).first)
					((TreeNode)now.parent).data.remove(0);
				else
					((TreeNode)now.parent).data.get(0).second = null;
				((TreeNode)now.parent).temp = now;
				((TreeNode)now.parent).temp.parent = ((TreeNode)now.parent);
				return redistribute(now.parent);
			}
			// general case
			else {
				((TreeNode)now.parent).data.remove(parentpos);
				if(((TreeNode)now.parent).data.size() < (degree-1)/2)
					return redistribute(now.parent);
				return true;
			}	
			
		}
		
		//merge whit right sibling
		else if((now.rightnode != null) && (now.rightnode.parent == now.parent) && (((TreeNode)now.rightnode).data.size() <= (degree-1)/2)) {
			sibling = (TreeNode)now.rightnode;
			int parentnum = ((TreeNode)now.parent).data.get(0).first;
			
			Pair<Integer, Node> temp = new Pair<Integer, Node>();
			temp.first = parentnum;
			temp.second = now.right;
			
			if(now.temp != null) {
				temp.second = now.temp;
				now.temp = null;
				now.data.clear();
			}
			
			//merge
			for(int i = 0; i < now.data.size();) {
				sibling.data.add(now.data.get(0));
				now.data.get(0).second.parent = sibling;
				now.data.remove(0);
			}
			temp.second.parent = sibling;
			sibling.data.add(temp);
			sibling.data.sort((a1,a2)->a1.first.compareTo(a2.first));
			
			//left setting
			if(now.left != null) {
				((TreeNode)now.left).rightnode = sibling;
				sibling.left = now.left;
			}
			else 
				sibling.left = null;
			
			//special case
			if(((TreeNode)now.parent).data.size() == 1 ) {
				if(sibling.data.get(0).first != ((TreeNode)now.parent).data.get(0).first)
					((TreeNode)now.parent).data.remove(0);
				else
					((TreeNode)now.parent).data.get(0).second = null;
				((TreeNode)now.parent).temp = sibling;
				((TreeNode)now.parent).temp.parent = ((TreeNode)now.parent);
				return redistribute(now.parent);
			}
			//general case
			else {
				((TreeNode)now.parent).data.remove(0);
				if(((TreeNode)now.parent).data.size() < (degree-1)/2)
					return redistribute(now.parent);
				return true;
			}
			
		}
		//root setting
		else if(now.parent == null) {
			if(now.temp != null) {
				root = now.temp;
				root.parent = null;
			}
			else
				root = now;
			return true;
		}
		return false;
	}
}
