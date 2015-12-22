package mayday.clustering.hierarchical;

import mayday.core.structures.trees.io.PlainNewick;
import mayday.core.structures.trees.tree.Node;

public class TreeInfo {

	protected Node tree;
	protected HierarchicalClusterSettings settings;
	protected String name;
	
	public TreeInfo(Node Tree, HierarchicalClusterSettings Settings) {
		tree = Tree;
		settings = Settings;
	}
	
	public String toString() {
		if (name!=null)
			return name;
		return super.toString();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public TreeInfo(TreeInfo other) {
		tree=other.tree;
		settings=other.settings;
	}
	
	public Node getTree() {
		return tree;
	}
	
	public void setTree(Node Tree) {
		tree=Tree;
	}

//	public Node getRoot() {
//        Node upgma_root = tree.getRoot();
//        if (settings.getClustering_method().ordinal() >= CLUSTER_METHOD.NJ.ordinal()) {
//            upgma_root = null;
//        }
//        return upgma_root;
//	}

	public HierarchicalClusterSettings getSettings() {
		return settings;
	}
	
	public int compareTo(TreeInfo other) {
		return (tree == other.tree && settings == other.settings)?0:1;
	}
	
	/* PAL tree parser dies on just about any special character (even space!), without documentation on this
	 * Safe seem to be: 0-9a-zA-Z():';+-,.
	 * everything else will be replaced by "~ASCII~"
	 * '(): 39-41
	 * +,-.: 43-46
	 * 0-9: 48-57
	 * ":": 58
	 * ";": 59
	 * A-Z: 65-90
	 * a-z: 97-122
	 */
	
//	protected static String makeStringSafe(String unsafeString) {
//		StringBuilder safeString = new StringBuilder();
//		for (int i=0; i!=unsafeString.length(); ++i) {
//			char unsafe = unsafeString.charAt(i);			
//			if ((int)unsafe<39 ||
//				(int)unsafe>41 && (int)unsafe<43 ||
//				(int)unsafe>46 && (int)unsafe<48 ||
//				(int)unsafe>59 && (int)unsafe<65 ||
//				(int)unsafe>90 && (int) unsafe<97 || 
//				(int)unsafe>122)
//			{ // this is unsafe!
//				safeString.append("~"+(int)unsafe+"~");				
//			} else { // this is safe				
//				safeString.append(unsafe);
//			}
//		}
//		return safeString.toString();
//	}
//	
//	protected static String restoreSafeString(String safeString) {
//		StringBuilder restoredString = new StringBuilder();
//		StringBuilder group = new StringBuilder();
//		for (int i=0; i!=safeString.length(); ++i /* ++i also happens inside the loop!*/) {			
//			char c = safeString.charAt(i);			 
//			if (c=='~') { // start of an scaped group				
//				++i;
//				while ((c = safeString.charAt(i))!='~') {
//					group.append(c);
//					++i;
//				}
//				int encodedChar = Integer.parseInt(group.toString());
//				group.setLength(0);
//				restoredString.append((char)encodedChar);
//			} else {
//				restoredString.append(c);
//			}
//		}
//		return restoredString.toString();
//	}
	
	public static Node parseNewick(String newick) {
		return new PlainNewick().parse(newick);
//		try {
//			String safeNewick = makeStringSafe(newick);
//			// parse tree
//			Tree t = TreeTool.readTree(new StringReader(safeNewick));
//			// restore identifier names
//			for(int i=0; i!=t.getIdCount(); ++i) {
//				Identifier id = t.getIdentifier(i);
//				// PAL tree parser dies on just about any special character
//				id.setName(restoreSafeString(id.getName()));
//			}
//			return t;
//		} catch (IOException e) {
//			// Rethrow the exception as non-declared runtime exception 
//			RuntimeException e2 = new RuntimeException(e.getMessage());
//			e2.setStackTrace(e.getStackTrace());
//			e2.printStackTrace();
//			throw e2;
//		}		
	}
	
	public TreeInfo(String s) {	
		String[] ess = s.split("\n");
		String newick = //"(A:1.7724385,(B:1.5136364,C:1.5136364):0.2588021);"
		                ess[0];
		String sett = s.substring(ess[0].length()+1);
		
		tree = parseNewick(newick);
		
//		PushbackReader pbr = new PushbackReader(new StringReader(newick));
//		try {
//			ReadTree workAround = new ReadTree(pbr);
//			for(int i=0; i!=workAround.getIdCount(); ++i) {
//				Identifier id = workAround.getIdentifier(i);
//				id.setName(restoreSafeString(id.getName()));
//			}
//			SimpleTree tree = new SimpleTree(workAround);
//			this.tree=tree;
//		} catch (Exception e) { 
//			// Rethrow the exception as non-declared runtime exception 
//			RuntimeException e2 = new RuntimeException(e.getMessage());
//			e2.setStackTrace(e.getStackTrace());
//			e2.printStackTrace();
//			throw e2;
//		}
		
		
		settings = new HierarchicalClusterSettings();		
		settings.deserialize(sett); 
	}			
	
	public String serialize() throws Exception {
		String sett = settings.serialize();
		String newick = tree.toNewick();
		return newick+"\n"+sett;
	}
	
}
