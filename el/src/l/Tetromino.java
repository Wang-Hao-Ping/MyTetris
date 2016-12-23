package l;

import java.util.Arrays;
import java.util.Random;

/**
 * 四格方块 类，有7种子类: I T S Z J L O
 * 
 */
public abstract class Tetromino {
	public static final int I_COLOR = 0xff6600;
	public static final int T_COLOR = 0xffff00;
	public static final int S_COLOR = 0x66ccff;
	public static final int Z_COLOR = 0x00ff00;
	public static final int L_COLOR = 0x0000ff;
	public static final int J_COLOR = 0xcc00ff;
	public static final int O_COLOR = 0xff0000;
	// Tetromino由4个Cell组成
	protected Cell[] cells = new Cell[4];

	protected Offset[] states;// 旋转的状态

	protected class Offset {
		int row0, col0;
		int row1, col1;
		int row2, col2;
		int row3, col3;

		public Offset(int row0, int col0, int row1, int col1, int row2, int col2,
				int row3, int col3) {
			this.row0 = row0;
			this.col0 = col0;
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.row3 = row3;
			this.col3 = col3;
		}
	}

	/**
	 * cells[0] = new Cell(0, 4, T_COLOR); cells[1] = new Cell(0, 3, T_COLOR);
	 * cells[2] = new Cell(0, 5, T_COLOR); cells[3] = new Cell(1, 4, T_COLOR);
	 * axis = (0,4) 1 2 3 offset = (0, 0, -1, 0, 1, 0, 0,-1), cells[1].row = 0+-1
	 * cells[1].col = 4+0 cells[2].row = 0+1 cells[2].col = 4+0 cells[2].row = 0+0
	 * cells[2].col = 4+-1
	 */
	private int index = 10000 - 1;

	/** 向右转 */
	public void rotateRight() {
		index++;
		Offset offset = states[index % states.length];
		//System.out.println( states.length);//2
		Cell axis = cells[0];// 找到轴(axis)的位置
		cells[1].setRow(offset.row1 + axis.getRow());
		cells[1].setCol(offset.col1 + axis.getCol());
		cells[2].setRow(offset.row2 + axis.getRow());
		cells[2].setCol(offset.col2 + axis.getCol());
		cells[3].setRow(offset.row3 + axis.getRow());
		cells[3].setCol(offset.col3 + axis.getCol());
	}
//向左转
	public void rotateLeft() {
		index--;
		Offset offset = states[index % states.length];
		Cell axis = cells[0];// 找到轴(axis)的位置
		cells[1].setRow(offset.row1 + axis.getRow());
		cells[1].setCol(offset.col1 + axis.getCol());
		cells[2].setRow(offset.row2 + axis.getRow());
		cells[2].setCol(offset.col2 + axis.getCol());
		cells[3].setRow(offset.row3 + axis.getRow());
		cells[3].setCol(offset.col3 + axis.getCol());
	}

	/** 随机生成 一个 具体方块 */
	public static Tetromino randomTetromino() {
		Random random = new Random();
		int type = random.nextInt(7);// 0～6
		switch (type) {
		case 0:
			return new I();
		case 1:
			return new T();
		case 2:
			return new S();
		case 3:
			return new J();
		case 4:
			return new Z();
		case 5:
			return new L();
		case 6:
			return new O();
		}
		return null;
	}

	/** 检查方块中是否包含 row,col 格子 */
	public boolean contains(int row, int col) {
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			if (cell.getRow() == row && cell.getCol() == col) {
				return true;
			}
		}
		return false;
	}
	/** 四格方块的下落，是四个格子一起下落 */
	public void softDrop() {
		for (int i = 0; i < cells.length; i++) {
			cells[i].dorp();
		}
	}
	/** 向左移动一步 */
	public void moveLeft() {
		// 传统数组的迭代
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];// 引用赋值
			cell.left();
		}
	}

	public void moveRight() {
		// 增强for循环：是传统数组迭代的"简化版本"，
		// 也称为foreach循环（foreach迭代）(java 5以后)
		for (Cell cell : cells) {// 底层实现就是传统迭代
			cell.right();
		}
	}

	public Cell[] getCells() {
		return cells;
	}

	public String toString() {
		return Arrays.toString(cells);
	}

}

class I extends Tetromino {
	public I() {
		cells[0] = new Cell(0, 4, I_COLOR);
		cells[1] = new Cell(0, 3, I_COLOR);
		cells[2] = new Cell(0, 5, I_COLOR);
		cells[3] = new Cell(0, 6, I_COLOR);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, 2, 0),
				new Offset(0, 0, 0, -1, 0, 1, 0, 2) };
	}
}

class T extends Tetromino {
	public T() {
		cells[0] = new Cell(0, 4, T_COLOR);
		cells[1] = new Cell(0, 3, T_COLOR);
		cells[2] = new Cell(0, 5, T_COLOR);
		cells[3] = new Cell(1, 4, T_COLOR);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, 0, -1),
				new Offset(0, 0, 0, 1, 0, -1, -1, 0),
				new Offset(0, 0, 1, 0, -1, 0, 0, 1),
				new Offset(0, 0, 0, -1, 0, 1, 1, 0) };
	}
}

class L extends Tetromino {
	public L() {
		cells[0] = new Cell(0, 4, L_COLOR);
		cells[1] = new Cell(0, 3, L_COLOR);
		cells[2] = new Cell(0, 5, L_COLOR);
		cells[3] = new Cell(1, 3, L_COLOR);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, -1, -1),
				new Offset(0, 0, 0, 1, 0, -1, -1, 1),
				new Offset(0, 0, 1, 0, -1, 0, 1, 1),
				new Offset(0, 0, 0, -1, 0, 1, 1, -1) };
	}
}

class J extends Tetromino {
	public J() {
		cells[0] = new Cell(0, 4, J_COLOR);
		cells[1] = new Cell(0, 3, J_COLOR);
		cells[2] = new Cell(0, 5, J_COLOR);
		cells[3] = new Cell(1, 5, J_COLOR);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 0, 1, -1),
				new Offset(0, 0, 0, 1, 0, -1, -1, -1),
				new Offset(0, 0, 1, 0, -1, 0, -1, 1),
				new Offset(0, 0, 0, -1, 0, 1, 1, 1) };
	}
}

class S extends Tetromino {
	public S() {
		cells[0] = new Cell(0, 4, S_COLOR);
		cells[1] = new Cell(0, 5, S_COLOR);
		cells[2] = new Cell(1, 3, S_COLOR);
		cells[3] = new Cell(1, 4, S_COLOR);
		states = new Offset[] { new Offset(0, 0, -1, 0, 1, 1, 0, 1),
				new Offset(0, 0, 0, 1, 1, -1, 1, 0) };
	}
}

class Z extends Tetromino {
	public Z() {
		cells[0] = new Cell(1, 4, Z_COLOR);
		cells[1] = new Cell(0, 3, Z_COLOR);
		cells[2] = new Cell(0, 4, Z_COLOR);
		cells[3] = new Cell(1, 5, Z_COLOR);
		states = new Offset[] { new Offset(0, 0, -1, 1, 0, 1, 1, 0),
				new Offset(0, 0, -1, -1, -1, 0, 0, 1) };
	}
}

class O extends Tetromino {
	public O() {
		cells[0] = new Cell(0, 4, O_COLOR);
		cells[1] = new Cell(0, 5, O_COLOR);
		cells[2] = new Cell(1, 4, O_COLOR);
		cells[3] = new Cell(1, 5, O_COLOR);
		states = new Offset[] { new Offset(0, 0, 0, 1, 1, 0, 1, 1),
				new Offset(0, 0, 0, 1, 1, 0, 1, 1) };
	}
}
