package com.saxifrages.hyperion.camshaft;

import com.saxifrages.hyperion.camshaft.advanced.RenderCell;
import com.saxifrages.hyperion.camshaft.mdxtest.AdvancedDataSetPrinter.Delegate;
import com.saxifrages.hyperion.camshaft.mdxtest.MdxMember;

public class TextDataSetDelegate implements Delegate {

	@Override
	public void printCell(MdxMember member, RenderCell cell) {
		if (cell.isRendered()) {
			//System.out.print(cell.getValue());
			System.out.printf("[%d/%03d,%03d/%03d/%2d%30s]", cell.getAxisNumber(), cell.getRow(), cell.getColumn(), cell.getRenderRow(), cell.getSpan(), cell.getValue());
		} else {
			printBlank();
		}
	}

	@Override
	public void printDataCell(RenderCell cell) {
		if (cell.isRendered()) {
			//System.out.print(cell.getValue());
			System.out.printf("|%d/%03d,%03d/%03d/%2d%30s]", cell.getAxisNumber(), cell.getRow(), cell.getColumn(), cell.getRenderRow(), cell.getSpan(), cell.getValue());
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
		System.out.printf("                 %30s ", "");
	}
	
}
