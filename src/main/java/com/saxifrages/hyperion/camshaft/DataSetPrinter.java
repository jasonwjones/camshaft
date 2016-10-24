package com.saxifrages.hyperion.camshaft;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssMdAxis;
import com.essbase.api.dataquery.IEssMdDataSet;
import com.essbase.api.dataquery.IEssMdMember;

public class DataSetPrinter {

	private static final Logger logger = LoggerFactory.getLogger(DataSetPrinter.class);
	
	private PrintOptions options;
	
	private DecimalFormat df;
	
	public DataSetPrinter(PrintOptions options) {
		this.options = options;
		this.df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		//df.setRoundingMode(RoundingMode.DOWN);
		df.setMaximumFractionDigits(options.getMaximumFractionDigits());
	}
	
	public void printMdDataSetInGridForm(IEssMdDataSet mddata, CamshaftOutputDelegate printStream) throws EssException {
		IEssMdAxis[] axis = mddata.getAllAxes();

		int cols = axis[1].getTupleCount();
		int rows = axis[2].getTupleCount();
				
		logger.info("Clusters on 2: {}", axis[2].getAllClusters().length);
		
		//axis[2].getAllClusters()[0].
		logger.info("Rows {}, columns {}", rows, cols);
		
		IEssMdMember[] mem = axis[2].getAllTupleMembers(0);
	
		logger.info("Tuples on axis: {}", mem.length);
		
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
					mem = axis[1].getAllTupleMembers(j);
					printTuple(mem, printStream);
				}
				printStream.endRow();
			} else {
				int lines = axis[1].getAllTupleMembers(0).length;
				for (int line = 0; line < lines; line++) {
					printStream.addRow();
					if (options.isIndentHeader()) {
						for (int i = 0; i < indent; i++) {
							printStream.addValue();
						}
					}
					for (int j = 0; j < cols; j++) {
						printStream.addValue(axis[1].getAllTupleMembers(j)[line].getName());
						//printStream.addValue(axis[1].getAllTupleMembers(j)[line].get);
					}
					printStream.endRow();
				}
			}
		}
		
		int k = 0;

		for (int i = 0; i < rows; i++) {
			printStream.addRow();
			mem = axis[2].getAllTupleMembers(i);
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
		
		for (int i = 0; i < mem.length - 1; i++) {
			sb.append(mem[i].getName());
			sb.append(options.getHeaderTupleSeparator());
		}
		sb.append(mem[mem.length - 1].getName());
		
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
				printStream.addValue(sb.toString());
				needCleanupPrint = false;
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
		
		if (needCleanupPrint) {
			printStream.addValue(sb.toString());
		}
	}
	
}
