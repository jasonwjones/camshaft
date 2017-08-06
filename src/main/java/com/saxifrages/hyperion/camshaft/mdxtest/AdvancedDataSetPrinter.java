package com.saxifrages.hyperion.camshaft.mdxtest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssMdDataSet;
import com.essbase.api.dataquery.IEssMdMember;
import com.jasonwjones.jacombi.CartesianIndex;
import com.saxifrages.hyperion.camshaft.advanced.RenderCell;

public class AdvancedDataSetPrinter {

	private static final Logger logger = LoggerFactory.getLogger(AdvancedDataSetPrinter.class);
	
	public interface Delegate {
		
		public void printCell(RenderCell cell);
		
		public void printBlank();
		
		public void finishLine();
		
	}
	
	public static class BasicRenderCell implements RenderCell {
		
		private int row;

		private int renderRow;
		
		private String value;
		
		private int axisNumber;
		
		private boolean rendered;
		
		private int span;
		
		@Override
		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		@Override
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public AxisOrientation getAxisOrientation() {
			return AxisOrientation.ROW;
		}

		@Override
		public int getAxisNumber() {
			return axisNumber;
		}

		public void setAxisNumber(int axisNumber) {
			this.axisNumber = axisNumber;
		}

		@Override
		public boolean isRendered() {
			return rendered;
		}

		public void setRendered(boolean rendered) {
			this.rendered = rendered;
		}

		@Override
		public int getSpan() {
			return span;
		}

		public void setSpan(int span) {
			this.span = span;
		}
		
		@Override
		public int getRenderRow() {
			return renderRow;
		}
		
		public void setRenderRow(int renderRow) {
			this.renderRow = renderRow;
		}
		
	}
	
	public void print(IEssMdDataSet mdDataSet, AdvancedPrinterConfiguration configuration, Delegate delegate) throws EssException {
		
		// product
		
		// MEMBER_ALIAS
		//logger.info("Dims on axis: {}", mdDataSet.getAllAxes()[2].getAllTupleMembers(1)[0].);
		MdDataSetWrapper wrapper = new MdDataSetWrapper(mdDataSet);
		
		logger.info("Wrapper size: {}", wrapper.size());
		
		// based on sizes of dimensions, we should be able to calculate the micro/
		// macro cycle lengths so we know when borders change
		
		logger.info("===== Testing CI");
		CartesianIndex<IEssMdMember[]> ci = new CartesianIndex<IEssMdMember[]>(wrapper, false);
		
		logger.info("Sizes: {}", ci.getSizes());
		logger.info("This cycle lengths: {}", ci.getCycleLengths());
		logger.info("Element lengths: {}", ci.getElementLengthsReverse());
		
		boolean isMergeSpannedCells = true;
		
		// data is just number of tuples on axis1?
		int dimIndexForDataColumns = mdDataSet.getAllAxes()[0].isSlicerAxis() ? 1 : 0;
		int dataColumns = mdDataSet.getAllAxes()[dimIndexForDataColumns].getTupleCount();
		logger.info("Data columns: {}", dataColumns);
		
		int row = 0;
		int renderRow = 0;
		long[] elementLengths = ci.getElementLengthsReverse();
		
		// the for loop will nominally process a row for the cross product of all rows
		
		for (List<IEssMdMember[]> line : ci) {
			BasicRenderCell cell = new BasicRenderCell();
			cell.setRow(row);			
			int col = 0;
			// each tuple set in the row
			for (IEssMdMember[] element : line) {
		
//				if (col == 1) {
//					delegate.finishLine();
//					for (int spacer = 0; spacer < 1; spacer++) {
//						delegate.printBlank();
//					}
//				}
				
				StringBuilder sb = new StringBuilder();
				for (IEssMdMember member : element) {
					
					if (member.isDimension()) {
						sb.append("!");
					} 
					// means if it's an MDX calculation, not if it's a dynamic calc Essbase member
					if (member.isCalced()) {
						sb.append("%");
					}
					// turns out we don't inherit shit via IEssProperties
					// sb.append("(" + member.getCountProperties() + ")");

					sb.append(member.getName());
					sb.append(" ");
					sb.append("-- " + member.getPropertyValueAny("MEMBER_ALIAS"));
					cell.setSpan((int) elementLengths[col]);
					cell.setRow(row);
					cell.setRenderRow(renderRow);
					
					if (row % elementLengths[col] != 0 && isMergeSpannedCells) {
						cell.setRendered(false);
					} else {
						cell.setRendered(true);
					}
				}
				cell.setValue(sb.toString());
				cell.setAxisNumber(col);
				delegate.printCell(cell); // might want to batch up the entire row in case we want to handle suppression
				
				col++;
			}
			
			// print data
			
			
			for (int colIndex = 0; colIndex < dataColumns; colIndex++) {
				//logger.info("Row {} col {}", row, dataColumns);
				int dataCell = row * dataColumns + colIndex;
				if (mdDataSet.isMissingCell(dataCell)) {
					delegate.printBlank();
				} else if (mdDataSet.isNoAccessCell(dataCell)) {
					delegate.printBlank();
				} else {
					//System.out.println("Cell: " + mdDataSet.getCellValue(dataCell));
					BasicRenderCell renderDataCell = new BasicRenderCell();
					renderDataCell.setRendered(true);
					renderDataCell.setValue(Double.toString(mdDataSet.getCellValue(dataCell)));
					delegate.printCell(renderDataCell);
				}
			}
			
			
			row++;
			renderRow++;
			
			//z    mdDataSet.getC
			
			// print footers, if any
			for (int elementLengthIndex = 0; elementLengthIndex < elementLengths.length; elementLengthIndex++) {
				if (row % elementLengths[elementLengthIndex] == 0) {
					//System.out.printf("  %03d - on boundary for %d ", row, elementLengthIndex);
					if (elementLengthIndex < 2) {
						//delegate.finishLine();
						renderRow++;
					}
					//logger.info("On boundary for axis {}", elementLengthIndex);
				}
			}
			
			delegate.finishLine();
		}
		System.out.println();
		
	}
	
}
