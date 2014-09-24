package org.sfm.csv;

public interface DelayedCellSetter<T, P> {
	
	void set(byte[] bytes, int offset, int length) throws Exception;
	void set(char[] chars, int offset, int length) throws Exception;

	public P getValue();
	public void set(T t) throws Exception;
	public boolean isSettable();

}
