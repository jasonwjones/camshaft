package com.saxifrages.hyperion.camshaft.mdxtest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssMdAxis;
import com.essbase.api.dataquery.IEssMdDataSet;
import com.essbase.api.dataquery.IEssMdMember;

public class MdDataSetWrapper implements List<List<MdxMember[]>> {

	private static final Logger logger = LoggerFactory.getLogger(MdDataSetWrapper.class);
	
	private IEssMdDataSet mdDataSet;
	
	private int startingAxis;

	private int endingAxis;
	
	private boolean treatPropertiesAsSeparateAxis = true;
	
	private List<IEssMdAxis> axes;
	
	public MdDataSetWrapper(IEssMdDataSet mdDataSet) throws EssException {
		this.mdDataSet = mdDataSet;
		if (mdDataSet.getAllAxes()[0].isSlicerAxis()) {
			this.startingAxis = 2;
		} else {
			this.startingAxis = 1;
		}
		this.endingAxis = this.mdDataSet.getAllAxes().length - 1;
		
		
		int axisIndex = 0; 
		axes = new ArrayList<IEssMdAxis>();
		for (IEssMdAxis axis : mdDataSet.getAllAxes()) {
			logger.info("Processing axis {}, slicer?: {}", axisIndex, axis.isSlicerAxis());
			logger.info("Has {} dimensions", axis.getDimensionCount());
			for (IEssMdMember dimMember : axis.getAllDimensions()) {
				logger.info("    has dimension {}", dimMember.getName());
				for (String property : dimMember.getPropertyNames()) {
					logger.info("        has property: {} as {}", property, dimMember.getPropertyDataType(property));
				}
			}
			++axisIndex;
		}
		
	}

	@Override
	public int size() {
		return endingAxis - startingAxis + 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<List<MdxMember[]>> iterator() {
		return new Iterator<List<MdxMember[]>>(){
			protected int i = -1;
			
			private int size = size();
			
			@Override
			public boolean hasNext() {
				return (i+1) < size && size > 0;
			}

			@Override
			public List<MdxMember[]> next() {
				i++; 
				if (i >= size) {
					throw new NoSuchElementException();
				}
				return get(i);
			}};
	}

	// critical impl
	@Override
	public List<MdxMember[]> get(int index) {
		try { 
			// Offset to ignore slicer, if any
			IEssMdAxis axis = this.mdDataSet.getAllAxes()[this.endingAxis - index];
			List<MdxMember[]> tuples = new ArrayList<MdxMember[]>(axis.getTupleCount());
			
			// things we pluck off an axis:
			// getTupleCount
			// getAllTupleMembers(int)  --> IEssMdMember[]
			
			// depending on axis pre-processing, should be either wrapper on a real IEssMdAxis
			// or a synthetic axis
			
			for (int i = 0; i < axis.getTupleCount(); i++) {
				tuples.add(MdxEssMember.from(axis.getAllTupleMembers(i)));
			}
			/*
			if (index == 0) {
				IEssMdMember[] syntheticMembers = new IEssMdMember[1];
				syntheticMembers[0] = new SyntheticEssMdMember("Dyn Scenario");
				tuples.add(syntheticMembers);
			}
			if (index == 1) {
				IEssMdMember[] syntheticMembers = new IEssMdMember[1];
				syntheticMembers[0] = new SyntheticEssMdMember("Dyn Time Period");
				tuples.add(syntheticMembers);				
			}
			if (index == 2) {
				IEssMdMember[] syntheticMembers = new IEssMdMember[2];
				syntheticMembers[0] = new SyntheticEssMdMember("My Product");
				syntheticMembers[1] = new SyntheticEssMdMember("My Margin");
				tuples.add(syntheticMembers);				
			}
			*/
			return tuples;
		} catch (EssException e) {
			System.err.println("Error: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends List<MdxMember[]>> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends List<MdxMember[]>> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}


	/////////////////////////////////////////////////
	
	@Override
	public List<MdxMember[]> set(int index, List<MdxMember[]> element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, List<MdxMember[]> element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MdxMember[]> remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<List<MdxMember[]>> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<List<MdxMember[]>> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<MdxMember[]>> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(List<MdxMember[]> e) {
		// TODO Auto-generated method stub
		return false;
	}
	/*
	public static class PropertyWrapper implements MdxMember {
		
		private IEssMdAxis axis;
		
		private String propertyName;
		
		public PropertyWrapper(IEssMdAxis axis, String propertyName) {
			this.axis = axis;
			this.propertyName = propertyName;
		}
		
	}*/
	
	
}
