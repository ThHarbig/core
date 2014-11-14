package mayday.clustering.hierarchical.pal;

//ClusterTree.java
//
// (c) 1999-2002 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

// 2009 fb: Merged with mayday's tree framework

/**
 * constructs a tree from pairwise distances using one of several clustering methods (UPGMA, WPGMA, Single Linkage, Complet Linkage - with room for extension)
 * Should work out to be O(n^2) but I'm not sure...
 * @version $Id: ClusterTree.java,v 1.3 2010/12/08 14:58:48 battke Exp $
 *
 * @author Matthew Goode
 *
 */
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;

public class ClusterTree {


	public static final ClusteringMethod UPGMA = new UPGMAClusterer();
	public static final ClusteringMethod WPGMA = new WPGMAClusterer();
	public static final ClusteringMethod SINGLE_LINKAGE = new SingleLinkageClusterer();
	public static final ClusteringMethod COMPLETE_LINKAGE = new CompleteLinkageClusterer();


	protected Node root;


	public ClusterTree(DistanceMatrix dm, ClusteringMethod cm) {
		if (dm.getIdCount() < 2) {
			new IllegalArgumentException("LESS THAN 2 TAXA IN DISTANCE MATRIX");
		}
		if (!dm.isSymmetric())		{
			new IllegalArgumentException("UNSYMMETRIC DISTANCE MATRIX");
		}
		BuildNode[] nodes = generateInitialNodes(dm);
		root = generateTree(nodes,cm);		
	}
	
	public Node getRoot() {
		return root;
	}

	private static final BuildNode[] generateInitialNodes(DistanceMatrix dm) {
		double[][] distances = dm.getClonedDistances();
		BuildNode[] nodes = new BuildNode[distances.length];
		for(int i= 0 ; i < nodes.length ; i++) {
			nodes[i] = new BuildNode(distances[i],dm.getIdentifier(i),i);
		}
		return nodes;
	}
	
	private static final Node generateTree(BuildNode[] nodes, ClusteringMethod cm) {
		int numberOfClusters = nodes.length;

		while(numberOfClusters!=1) {
			double closestPairDistance = nodes[0].getClosestDistance();
			int closestPairStart = 0;
			for(int i = 1 ; i < numberOfClusters ; i++) {
				final double clusterClosest = nodes[i].getClosestDistance();
				if(clusterClosest<closestPairDistance) {
					closestPairStart = i;
					closestPairDistance = clusterClosest;
				}

			}
			final int closestPairEnd = nodes[closestPairStart].getClosestIndex();
			if(closestPairStart == numberOfClusters-1) {
				//I don't think this should happen, but if it did it would muck things up
				throw new RuntimeException("Closest pair start is last node!");
				//closestPairStart = closestPairEnd;
				//closestPairEnd = numberOfClusters-1;
			}
			BuildNode closestPairStartNode = nodes[closestPairStart];
			BuildNode closestPairEndNode = nodes[closestPairEnd];
			//Start process of reducing cluster
			//At the moment the last node (at position numberOfClusters post the following line)
			// is in the wrong place... will be moved a few lines down...
			numberOfClusters--;
			//Closest pair end will be removed from current nodes
			//Closest pair start will be replaced by combined cluster

			//Swap out one of the nodes with end node
			//This is the bit that makes things confusing...
			nodes[closestPairEnd] = nodes[numberOfClusters];

			double[] newDistances = new double[numberOfClusters];
			int closestPairStartClusterSize = closestPairStartNode.getClusterSize();
			int closestPairEndClusterSize = closestPairEndNode.getClusterSize();
			//Remember the internal distances recorded by each node have not been rearranged yet
			for(int i = 0 ; i < numberOfClusters ; i++) {
				if(i!=closestPairStart) {
					final BuildNode n = nodes[i];
					double distance = cm.computeDistance(
							n.getClusterSize(),
							closestPairStartClusterSize,
							n.getDistanceTo(closestPairStart),
							closestPairEndClusterSize,
							n.getDistanceTo(closestPairEnd)
					);

					//The real index is to take into account the fact that the "end" node was swaped with closestPairEnd
					final int itsOldIndex = (i!=closestPairEnd ? i : numberOfClusters);
					//n.substituteDistance(closestPairStart,distance,itsOldIndex);
					n.substituteAndRemoveDistance(closestPairStart,distance,closestPairEnd,itsOldIndex);
					newDistances[i] = distance;
				}
			}
			BuildNode newCluster =
				new BuildNode(
						newDistances,
						closestPairStartNode,
						closestPairEndNode,
						cm.computeHeight(
								closestPairStartClusterSize,
								closestPairEndClusterSize,
								closestPairDistance),
								closestPairStart);
			nodes[closestPairStart] = newCluster;
			//Uncomment the following for integrity checks (not needed unless altering stuff)
			//			for(int i = 0 ; i < numberOfClusters ; i++) {
			//				if(!nodes[i].checkIntegrity()) {
			//					System.out.println("Fault at "+i);
			//				}
			//			}
		}
		return nodes[0].generateNode();
	}

	/**
	 * An interface for describing Clustering methods
	 * To be updated to cope with Ward's method and others...
	 */
	public static interface ClusteringMethod {

		public double computeDistance(
				int separateClusterSize,
				int firstToCombineClusterSize,
				double firstToCombineClusterToSeparateClusterDistance,
				int secondToCombineClusterSize,
				double secondToCombineClusterToSeparateClusterDistance
		);
		public double computeHeight(
				int firstToCombineClusterSize,
				int secondToCombineClusterSize,
				double distance);
		public String getMethodName();
	}

	//===========
	//=- UPGMA -=
	abstract private static class BaseClusterer implements ClusteringMethod {
		public double computeHeight(
				int firstToCombineClusterSize,
				int secondToCombineClusterSize,
				double distance) {
			return	distance/2.0;
		}
	}
	private static final class UPGMAClusterer extends BaseClusterer {
		public double computeDistance(
				int separateClusterSize,
				int firstToCombineClusterSize,
				double firstToCombineClusterToSeparateClusterDistance,
				int secondToCombineClusterSize,
				double secondToCombineClusterToSeparateClusterDistance
		) {
			return
			(
					firstToCombineClusterSize*
					firstToCombineClusterToSeparateClusterDistance+
					secondToCombineClusterSize*
					secondToCombineClusterToSeparateClusterDistance
			)/(firstToCombineClusterSize+secondToCombineClusterSize);
		}
		public String getMethodName() { return "UPGMA"; }
	}
	private static final class WPGMAClusterer extends BaseClusterer {
		public double computeDistance(
				int separateClusterSize,
				int firstToCombineClusterSize,
				double firstToCombineClusterToSeparateClusterDistance,
				int secondToCombineClusterSize,
				double secondToCombineClusterToSeparateClusterDistance
		) {
			return
			(
					firstToCombineClusterToSeparateClusterDistance+
					secondToCombineClusterToSeparateClusterDistance
			)/2;
		}
		public String getMethodName() { return "WPGMA"; }

	}
	private static final class SingleLinkageClusterer extends BaseClusterer {
		public double computeDistance(
				int separateClusterSize,
				int firstToCombineClusterSize,
				double firstToCombineClusterToSeparateClusterDistance,
				int secondToCombineClusterSize,
				double secondToCombineClusterToSeparateClusterDistance
		) {
			return
			Math.min(
					firstToCombineClusterToSeparateClusterDistance,
					secondToCombineClusterToSeparateClusterDistance
			);
		}
		public String getMethodName() { return "Single Linkage"; }

	}
	private static final class CompleteLinkageClusterer extends BaseClusterer {
		public double computeDistance(
				int separateClusterSize,
				int firstToCombineClusterSize,
				double firstToCombineClusterToSeparateClusterDistance,
				int secondToCombineClusterSize,
				double secondToCombineClusterToSeparateClusterDistance
		) {
			return
			Math.max(
					firstToCombineClusterToSeparateClusterDistance,
					secondToCombineClusterToSeparateClusterDistance
			);
		}
		public String getMethodName() { return "Complete Linkage"; }

	}
	//================
	//=- Build Node -=
	private static final class BuildNode {
		double[] distanceStore_;
		int numberOfDistances_;
		final BuildNode leftChild_;
		final BuildNode rightChild_;
		final Identifier id_;
		int closestIndex_;
		double closestDistance_;
		int clusterSize_;
		final double height_;

		public BuildNode(double[] initialDistances, Identifier id, int myIndex) {
			this.clusterSize_ = 1;
			setDistances(initialDistances,myIndex);
			this.id_ = id;
			this.height_ = 0;
			leftChild_ = rightChild_ = null;
		}
		public BuildNode(double[] initialDistances, BuildNode left, BuildNode right, double relativeHeight, int myIndex) {
			this.clusterSize_ = left.clusterSize_+right.clusterSize_;
			this.leftChild_ = left;
			this.rightChild_ = right;
			this.height_ = relativeHeight; //Math.max(left.height_,right.height_)+relativeHeight;
			setDistances(initialDistances,myIndex);
			id_ = null;
		}

		private final void setDistances(double[] distances, final int myIndex) {
			this.distanceStore_ = distances;
			this.numberOfDistances_ = distances.length;
			updateClosest(myIndex);
		}
		private void updateClosest(final int myIndex) {
			int closestIndex = -1;
			double closestDistance = Double.POSITIVE_INFINITY;
			for(int i = 0 ; i < numberOfDistances_ ; i++) {
				if((i!=myIndex)&&(distanceStore_[i]<closestDistance)) {
					closestDistance = distanceStore_[i];
					closestIndex = i;
				}
			}
			this.closestIndex_ = closestIndex;
			this.closestDistance_ = closestDistance;
		}
		@SuppressWarnings("unused")
		public final double getNodeHeight() { return height_; }
		public final int getClusterSize() { return clusterSize_; }
		/**
		 * removes a distance by swapping in the end distance
		 */
		@SuppressWarnings("unused")
		public final void removeDistance(final int distanceIndex, final int myIndex) {
			distanceStore_[distanceIndex] = distanceStore_[--numberOfDistances_];
			if(closestIndex_==distanceIndex) {
				updateClosest(myIndex==numberOfDistances_ ? distanceIndex : myIndex );
			}
			//updateClosest(myIndex==numberOfDistances_ ? distanceIndex : myIndex );

			//updateClosest(myIndex);
		}
		/**
		 * Assumes toSubtituteIndex != toRemoveIndex;
		 */
		public final void substituteAndRemoveDistance(
				//This is the code that should make everything O(n^2)

				final int toSubtituteIndex, final double newDistance,
				final int toRemoveIndex,
				final int myIndex) {
			distanceStore_[toSubtituteIndex] = newDistance;
			if(toSubtituteIndex==toRemoveIndex) {
				throw new IllegalArgumentException("To substitute = to remove!");
			}
			if(toSubtituteIndex>=numberOfDistances_) {
				throw new IllegalArgumentException("To substitute invalid!");
			}
			if(toRemoveIndex>=numberOfDistances_) {
				throw new IllegalArgumentException("To remove invalid!");
			}
			distanceStore_[toRemoveIndex] = distanceStore_[--numberOfDistances_];
			//updateClosest(myIndex==numberOfDistances_ ? toRemoveIndex : myIndex );

			if(newDistance<closestDistance_) {
				closestDistance_ = newDistance;
				closestIndex_ = toSubtituteIndex;
			} else 	if((closestIndex_==toRemoveIndex)||(closestIndex_ == toSubtituteIndex)) {
				//This should only happen a max of two times per iteration!
				updateClosest(myIndex==numberOfDistances_ ? toRemoveIndex : myIndex );
			} else if(closestIndex_ == numberOfDistances_) {
				closestIndex_ = toRemoveIndex;
			}

		}

		public final double getClosestDistance() {  return closestDistance_; }
		public final int getClosestIndex() { return closestIndex_; }
		public final double getDistanceTo(final int distanceIndex) {
			return distanceStore_[distanceIndex];
		}
		public final boolean isChild() { return leftChild_==null; }
		
		// changed: fb
		protected Node generateNode() {
			Node me = new Node(id_!=null?id_.toString():"", null);
			if(isChild()) {  // a leaf node
				return me;
			}
			Node c1 = leftChild_.generateNode();
			Node c2 = rightChild_.generateNode();
			Edge e1 = new Edge(height_-leftChild_.height_, me, c1);
			Edge e2 = new Edge(height_-rightChild_.height_, me, c2);
			me.addEdge(e1);
			me.addEdge(e2);
			c1.addParentEdge(e1);
			c2.addParentEdge(e2);
			return me;
		}
		
		@SuppressWarnings("unused")
		public boolean checkIntegrity() {
			return(closestIndex_<numberOfDistances_);
		}
		
		@SuppressWarnings("unused")
		public static final double getMaxHeight(BuildNode[] nodes) {
			double maxHeight = nodes[0].height_;
			for(int i = 1 ; i < nodes.length ; i++) {
				final double nodeHeight = nodes[i].height_;
				if(nodeHeight>maxHeight) {  maxHeight = nodeHeight; }
			}
			return maxHeight;
		}
	} //End of class BuildNode

}