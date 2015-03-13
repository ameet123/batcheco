package com.att.datalake.loco.offercriteria.model;

import java.util.ArrayList;
import java.util.List;

/**
 * data structure for pre processing syntax
 * @author ac2211
 *
 */
public class PreProcSpec {

	private String offerId;
	private List<ProcDetail> procDetail = new ArrayList<ProcDetail>();
	
	public String getOfferId() {
		return offerId;
	}
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public List<ProcDetail> getProcDetail() {
		return procDetail;
	}

	public void setProcDetail(List<ProcDetail> procDetail) {
		this.procDetail = procDetail;
	}

	public static class ProcDetail {
		private int step;
		private String leftTable;
		private List<String> leftColumns;
		private String rightTable;
		private List<String> rightColumns;
		private char op;
		private String opColumn;
		private String matchingTable;
		private String output;
		/**
		 * @return the step
		 */
		public int getStep() {
			return step;
		}
		/**
		 * @param step the step to set
		 */
		public void setStep(int step) {
			this.step = step;
		}
		/**
		 * @return the leftTable
		 */
		public String getLeftTable() {
			return leftTable;
		}
		/**
		 * @param leftTable the leftTable to set
		 */
		public void setLeftTable(String leftTable) {
			this.leftTable = leftTable;
		}
		/**
		 * @return the leftColumns
		 */
		public List<String> getLeftColumns() {
			return leftColumns;
		}
		/**
		 * @param leftColumns the leftColumns to set
		 */
		public void setLeftColumns(List<String> leftColumns) {
			this.leftColumns = leftColumns;
		}
		/**
		 * @return the rightTable
		 */
		public String getRightTable() {
			return rightTable;
		}
		/**
		 * @param rightTable the rightTable to set
		 */
		public void setRightTable(String rightTable) {
			this.rightTable = rightTable;
		}
		/**
		 * @return the rightColumns
		 */
		public List<String> getRightColumns() {
			return rightColumns;
		}
		/**
		 * @param rightColumns the rightColumns to set
		 */
		public void setRightColumns(List<String> rightColumns) {
			this.rightColumns = rightColumns;
		}
		/**
		 * @return the op
		 */
		public char getOp() {
			return op;
		}
		/**
		 * @param op the op to set
		 */
		public void setOp(char op) {
			this.op = op;
		}
		/**
		 * @return the opColumn
		 */
		public String getOpColumn() {
			return opColumn;
		}
		/**
		 * @param opColumn the opColumn to set
		 */
		public void setOpColumn(String opColumn) {
			this.opColumn = opColumn;
		}
		/**
		 * @return the matchingTable
		 */
		public String getMatchingTable() {
			return matchingTable;
		}
		/**
		 * @param matchingTable the matchingTable to set
		 */
		public void setMatchingTable(String matchingTable) {
			this.matchingTable = matchingTable;
		}
		/**
		 * @return the output
		 */
		public String getOutput() {
			return output;
		}
		/**
		 * @param output the output to set
		 */
		public void setOutput(String output) {
			this.output = output;
		}
	}
}
