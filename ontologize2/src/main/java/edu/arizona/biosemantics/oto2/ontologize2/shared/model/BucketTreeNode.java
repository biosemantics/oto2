package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.util.List;

import com.sencha.gxt.data.shared.TreeStore.TreeNode;

public class BucketTreeNode extends TextTreeNode {
	
	private Bucket bucket;

	public BucketTreeNode(Bucket bucket) {
		this.bucket = bucket;
	}

	@Override
	public String getText() {
		return bucket.getName();
	}
	
	@Override
	public String getId() {
		return "bucket-" + bucket.getId();
	}
	
	public Bucket getBucket() {
		return bucket;
	}
	
	
}