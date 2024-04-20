package gr.uoa.di.entities.dictionary;

class _DictVar {
	
		private String varName;
	
		_DictVar(int i, int varId) {
			varName="?x"+i;
		}
				
		@Override
		public String toString() {
			return varName;
		}

}
