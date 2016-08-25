package com.saxifrages.hyperion.camshaft;

public interface CamshaftOutputDelegate {

	public void startOutput();
	
	public void startHeader();
	
	public void endHeader();
	
	public void addRow();
	
	public void addHeaderSpace();
	
	public void addHeaderTuple(String[] values);
	
	public void addTuple(String[] values); 
	
	public void addValue(String value);
	
	public void addValue();
	
	public void endRow();
	
	public void endOutput();
	
}
