package it.unibo.ai.didattica.competition.tablut.ainability.domain;

public class Position {

	private int Row;
	private int Column;
	
	public int getRow() {
		return Row;
	}
	public void setRow(int row) {
		Row = row;
	}
	public int getColumn() {
		return Column;
	}
	public void setColumn(int column) {
		Column = column;
	}
	public Position(int row, int column) {
		super();
		Row = row;
		Column = column;
	}
	
	
	
}