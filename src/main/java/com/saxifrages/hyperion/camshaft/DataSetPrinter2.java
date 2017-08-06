package com.saxifrages.hyperion.camshaft;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssMdAxis;
import com.essbase.api.dataquery.IEssMdDataSet;
import com.essbase.api.dataquery.IEssMdMember;

public class DataSetPrinter2 {

	private static final Logger logger = LoggerFactory.getLogger(DataSetPrinter2.class);
	
	private PrintOptions options;
	
	private DecimalFormat df;
	
	public DataSetPrinter2(PrintOptions options) {
		this.options = options;
		this.df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		//df.setRoundingMode(RoundingMode.DOWN);
		if (options.getMaximumFractionDigits() != -1) {
			df.setMaximumFractionDigits(options.getMaximumFractionDigits());
		}
		df.setMinimumFractionDigits(2);
	}
	
	public void printMdDataSetInGridForm(IEssMdDataSet mddata, CamshaftOutputDelegate printStream) throws EssException {
		
		IEssMdAxis[] axis = mddata.getAllAxes();
		logger.info("Size of all axes: {}", mddata.getAllAxes().length);
		logger.info("Total cell count: {}", mddata.getCellCount());

		for (int cell = 0; cell < mddata.getCellCount(); cell++) {
			Double cellValue = mddata.isMissingCell(cell) ? null : mddata.getCellValue(cell); 
			System.out.printf("Cell %d has value: %f %n", cell, cellValue);
		}
		
		// Although an MDX set is a collection of tuples, internally, Essbase represents sets using 
		// clusters and tuples. A cluster is a type of set derived using the CrossJoin function, 
		// where the arguments to CrossJoin are sets from one dimension only.
		// A cluster can also be thought of as a symmetric set. The following set is a symmetric 
		// set and can be stored as one cluster.
		
//		Axis 0: Clusters=1, dimensions=1, tuple=1 			budget
//		Axis 1: Clusters=1, dimensions=1, tuple=3 			cols: east west south
//		Axis 2: Clusters=10, dimensions=2, tuple=10			rows (not including time)
//		Axis 3: Clusters=1, dimensions=1, tuple=12			time periods
		
		// Clusters, true to the description, appear to be put in play when the results
		// are symetrical. It appears that we can just take the cross product of the upper axes
		// in order to determine the number of overall cells to generate. For example, with
		// the sample axes we can say that 12 * 15 = 180, then for 3 time periods we have the 540
		//
		// We should be able to 'naively' process the tuples, irrespective of the clusters, in
		// order to create the proper results
		
		for (int i = 0; i < axis.length; i++) {
			String format = "Axis %d: Clusters=%d, dimensions=%d, tuple=%d";
			System.out.println(String.format(format, i, axis[i].getClusterCount(), axis[i].getDimensionCount(), axis[i].getTupleCount()));
			for (int cluster = 0; cluster < axis[i].getClusterCount(); cluster++) {
				int tupleCount = axis[i].getAllClusters()[cluster].getTupleCount();
				//axis[i].getT
				
				// the full product is our tuple count
				//axis[i].get
//				axis[i].getAl
				
				int dimCount = axis[i].getAllClusters()[cluster].getDimensionCount();
				System.out.printf(" Cluster %d tuple count %d, dim count %d %n", cluster, tupleCount, dimCount);
				
				for (int dim = 0; dim < dimCount; dim++) {
					IEssMdMember dimMembers[] = axis[i].getAllClusters()[cluster].getAllDimensionMembers(dim);
					for (IEssMdMember dimMember : dimMembers) {
						System.out.printf("  %s %n", dimMember.getName());
					}
				}
			}
		}
		
		List<List<Integer>> sizes = null;
		
		
		
		//new CartesianIndex<Integer>(r)
		
//		for (int i = 0; i < axis.length; i++) {
//			String format = "Axis %d: Clusters=%d, dimensions=%d, tuple=%d";
//			System.out.println(String.format(format, i, axis[i].getClusterCount(), axis[i].getDimensionCount(), axis[i].getTupleCount()));
//		}
		
		IEssMdAxis axis1 = axis[0];
		IEssMdAxis axis2 = axis[1];
		
		if (axis1.isSlicerAxis()) {
			axis1 = axis[1];
			axis2 = axis[2];
		}
		
		int cols = axis1.getTupleCount();
		int rows = axis2.getTupleCount();
						
		IEssMdMember[] mem = axis2.getAllTupleMembers(0);
	
		logger.info("DS2 Tuples on axis: {}", mem.length);
		
		printStream.startOutput();
		
		if (!options.isSuppressHeader()) {
			int indent = mem.length;
			if (!options.isLinePerHeaderTupleMember()) {
				// tabs over for the number of columns
				if (options.isIndentHeader()) {
					for (int i = 0; i < indent; i++) {
						printStream.addValue();
					}
				}
				// prints the headers (columns) -- always axis 1, apparently
				for (int j = 0; j < cols; j++) {
					mem = axis1.getAllTupleMembers(j);
					printTuple(mem, printStream);
				}
				printStream.endRow();
			} else {
				int lines = axis1.getAllTupleMembers(0).length;
				for (int line = 0; line < lines; line++) {
					printStream.addRow();
					if (options.isIndentHeader()) {
						for (int i = 0; i < indent; i++) {
							printStream.addValue();
						}
					}
					for (int j = 0; j < cols; j++) {
						printStream.addValue(axis1.getAllTupleMembers(j)[line].getName());
						//printStream.addValue(axis[1].getAllTupleMembers(j)[line].get);
					}
					printStream.endRow();
				}
			}
		}

		// Now print the actual data in the result set
		int k = 0;

		for (int i = 0; i < rows; i++) {
			printStream.addRow();
			mem = axis2.getAllTupleMembers(i);
			printTuple(mem, printStream);
			for (int j = 0; j < cols; j++) {
				if (mddata.isMissingCell(k)) {
					printStream.addValue(options.getMissingText());
				} else if (mddata.isNoAccessCell(k)) {
					printStream.addValue(options.getNoAccessText());
				} else {
					String fmtdCellTxtVal = mddata.getFormattedValue(k);
					if (fmtdCellTxtVal != null && fmtdCellTxtVal.length() > 0) {
						printStream.addValue(fmtdCellTxtVal);
					} else {
						double val = mddata.getCellValue(k);
						printStream.addValue(df.format(val));
					}
				}
				k++;
			}
			printStream.endRow();
		}
		printStream.endOutput();
	}

	private void printTuple(IEssMdMember[] mem, CamshaftOutputDelegate printStream) throws EssException {
		StringBuilder sb = new StringBuilder();
		
		if (options.isPrintTupleMemberSeparately()) {
			for (int i = 0; i < mem.length; i++) {
				printStream.addValue(mem[i].getName());
			}
		} else {
			for (int i = 0; i < mem.length - 1; i++) {
				sb.append(mem[i].getName());
				sb.append(options.getHeaderTupleSeparator());
			}
			sb.append(mem[mem.length - 1].getName());
		}		
		
		boolean needCleanupPrint = true;
		
		if (options.isPrintProperties()) {
			IEssMdMember mdMember = mem[mem.length - 1];
			if (!options.isPrintPropertiesSeparately()) {
				if (mdMember.getCountProperties() > 0) {
					sb.append(options.getHeaderTupleSeparator());
					String[] propNames = mdMember.getPropertyNames();
					for (int index = 0; index < propNames.length; index++) {
						if (options.isPrintPropertyNames()) {
							sb.append(propNames[index]);
							sb.append(options.getHeaderTupleSeparator());
						}
						sb.append(mdMember.getPropertyValueAny(index));
						sb.append(options.getHeaderTupleSeparator());
					}
				}
			} else {
				if (!options.isPrintTupleMemberSeparately()) {
					printStream.addValue(sb.toString());
					needCleanupPrint = false;
				}
				
				if (mdMember.getCountProperties() > 0) {
					String[] propNames = mdMember.getPropertyNames();
					for (int index = 0; index < propNames.length; index++) {
						if (options.isPrintPropertyNames()) {
							printStream.addValue(propNames[index]);
						}
						printStream.addValue(mdMember.getPropertyValueAny(index).toString());
					}

				}
			}
		}
		
		if (needCleanupPrint && !options.isPrintTupleMemberSeparately()) {
			printStream.addValue(sb.toString());
		}
	}
	
}
