package l;

//包: 小写英文字母，域名倒写.项目名
/**
 * 最小的格子
 */
//类方法，可以进行调用，该类的UML类图

public class Cell {//格子
	private int row;//定义行
	private int col;//定义列
	private int color;//颜色
	
	public Cell(int row, int col, int color) {
		super();
		this.row = row;
		this.col = col;
		this.color = color;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	//向左走
	public void left() {
		col--;
	}
	//向右走
	public void right() {
		col++;
	}
	//向下走
	public void dorp() {
		row++;
	}
   //重写toString()方法
	public String toString() {
		return row + "," + col;
	}
}
