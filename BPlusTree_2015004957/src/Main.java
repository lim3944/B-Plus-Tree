import java.io.*;

public class Main {
	
	private static BufferedReader br;
	private static BufferedReader br2;

	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.out.println("File Error");
			return;
		}
		
		switch(args[0]) {
			case "-c":{
				create(args[1],Integer.parseInt(args[2]));
				break;
			}
			case "-i":{
				insert(args[1], args[2]);
				break;
			}
			case "-d":{
				delete(args[1], args[2]);
				break;
			}
			case "-s":{
				search(args[1], Integer.parseInt(args[2]));
				break;
			}
			case "-r":{
				if(Integer.parseInt(args[2]) > Integer.parseInt(args[3])) {
					System.out.println("Range setting error");
					break;
				}
				ranged(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				break;
			}
			default :{
				System.out.println("Command Error");
				return ;
			}
		}
	}
	
	//Create a new empty BPlusTree
	static void create(String indexfile, int degree) {
		BPlusTree tree = new BPlusTree(degree);
		saveTree(indexfile, tree);
		System.out.println("Data Created!");
	}
	
	//Insert key & value pairs to BPlusTree
	static void insert(String indexfile, String datafile) {
		BPlusTree tree = loadTree(indexfile);
		try {
			br2 = new BufferedReader(new FileReader(datafile));
			while(true){
				String line = br2.readLine();
				if(line == null)
					break;
				String s1 = line.split(",")[0];
				String s2 = line.split(",")[1];
				int key = Integer.parseInt(s1);
				int value = Integer.parseInt(s2);
				tree.insert(key, value);
				}
		}catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Insertion Complete");
		saveTree(indexfile, tree);
	}
	
	//Delete the value in BPlusTree
	static void delete(String indexfile, String datafile) {
		BPlusTree tree = loadTree(indexfile);
		
		try {
			br = new BufferedReader(new FileReader(datafile));
			while(true) {
				String input = br.readLine();
				if(input == null)
					break;
				int key = Integer.parseInt(input);
				
				if(tree == null){
					break;
				}
				tree.delete(key);
			} 
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("Deletion Complete");
		saveTree(indexfile, tree);
	}
	
	//Search the value with input key within the BPlusTree
	static void search(String indexfile, int key) {
		BPlusTree tree = loadTree(indexfile);
		int result = -1;
		Node now = tree.root;
		while(!now.isLeaf()) {
			boolean rightchk = false;
			int chk = -1;
			Node temp = now;
			for(int i = 0; i < ((TreeNode)temp).data.size();i++){
				System.out.printf("%d ", ((TreeNode)temp).data.get(i).first);
				if(((TreeNode)temp).data.get(i).first > key && chk == -1){
					now = ((TreeNode)temp).data.get(i).second;
					rightchk = true;
					chk = 0;
				}
			}
			System.out.println();
			if(!rightchk)
				now = now.right;
		}
		result = ((LeafNode)now).search(key);
		if(result == -1)
			System.out.println("Not Found!");
		else
			System.out.printf("%d\n",result);
	}
	
	//Search all values beteween start value and end value
	static void ranged(String indexfile, int start, int end) {
		BPlusTree tree = loadTree(indexfile);
		boolean loopchk = true;
		Node now = tree.root;
		
		//search leaf
		while(!now.isLeaf()) {
			boolean chk = false;
			for(int i = 0; i < ((TreeNode)now).data.size();i++){
				if(((TreeNode)now).data.get(i).first > start){
					now = ((TreeNode)now).data.get(i).second;
					chk = true;
					break;
				}
			}
			if(!chk)
				now = now.right;
		}
		
		//leaf node traversal
		while(loopchk) {
			for(int i = 0; i < ((LeafNode)now).data.size();i++) {
				if(end < ((LeafNode)now).data.get(i).first) {
					loopchk = false;
					break;
				}
				if(start <=((LeafNode)now).data.get(i).first) {
					System.out.printf("%d %d\n",((LeafNode)now).data.get(i).first,((LeafNode)now).data.get(i).second);
				}
			}
			now = now.right;
			if(now == null)
				break;
		}
	}
	
	//Save the BPlusTree object into the file
	static void saveTree(String fileName, BPlusTree tree) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(tree);
            oout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	//Load the BPlusTree object from the file
	static BPlusTree loadTree(String fileName) {
        BPlusTree tree = null;
        try {
            FileInputStream fin = new FileInputStream(fileName);
            ObjectInputStream oin = new ObjectInputStream(fin);
            tree = (BPlusTree) oin.readObject();
            oin.close();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tree;
    }
}
