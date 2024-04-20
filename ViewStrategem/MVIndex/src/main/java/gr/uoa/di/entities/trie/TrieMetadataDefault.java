package gr.uoa.di.entities.trie;

public class TrieMetadataDefault<I> implements TrieMetadata<I>{
	private I info = null;
	
	@Override
	public void updateOn(I info) {
		if(this.info==null) {
			this.info = info;
		}
	}
	
	public I getInfo() {
		return info;
	}
}
