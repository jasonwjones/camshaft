package com.saxifrages.hyperion.camshaft;

import com.saxifrages.hyperion.camshaft.advanced.RenderCell;
import com.saxifrages.hyperion.camshaft.mdxtest.AdvancedDataSetPrinter.Delegate;

public class TextDataSetDelegate implements Delegate {

	@Override
	public void printCell(RenderCell cell) {
		if (cell.isRendered()) {
			System.out.printf("[%d/%03d/%03d/%2d%30s]", cell.getAxisNumber(), cell.getRow(), cell.getRenderRow(), cell.getSpan(), cell.getValue());
		} else {
			printBlank();
		}
	}

	@Override
	public void finishLine() {
		System.out.println();
	}

	@Override
	public void printBlank() {
		System.out.printf("             %30s ", "");
	}
	
}
