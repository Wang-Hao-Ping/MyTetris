package l;

//��: СдӢ����ĸ��������д.��Ŀ��
/**
 * ��С�ĸ���
 */
//�෽�������Խ��е��ã������UML��ͼ

public class Cell {//����
	private int row;//������
	private int col;//������
	private int color;//��ɫ
	
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
	//������
	public void left() {
		col--;
	}
	//������
	public void right() {
		col++;
	}
	//������
	public void dorp() {
		row++;
	}
   //��дtoString()����
	public String toString() {
		return row + "," + col;
	}
}
