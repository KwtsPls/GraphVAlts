package gr.uoa.di.entities.viewSelection.materialization;

import gr.uoa.di.entities.trie.TrieIndex;
import gr.uoa.di.entities.trie.TrieMetadata;
import gr.uoa.di.entities.viewSelection.queryRewriting.ViewForRewriting;
import gr.uoa.di.viewTemplates.View;

public class TrieIndexForMaterialization extends TrieIndex<View, TrieMetadata<View>> {

	public static TrieIndexForMaterialization create() {
		return new TrieIndexForMaterialization();
	}


	@Override
	public TrieMetadata<View> createMetadataInstance() {
		return null;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
