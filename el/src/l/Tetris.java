package l;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * 俄罗斯方块 类 俄罗斯方块 扩展了(extends)系统的现实面板，增加了墙和 正在下落的方块
 */
public class Tetris extends JPanel {
	public static final int ROWS = 20;//初始化行为20行
	public static final int COLS = 10;//初始化列为10列
	/** 代表方块下落着陆的墙 */
	private Cell[][] wall = new Cell[ROWS][COLS];
	/** 是正在下落地方块 */
	private Tetromino tetromino;
	/** 下一个进入的方块 */
	private Tetromino nextOne;

	private int score;//定义得分
	private int lines;//定义
	private static final int[] SCORE_LEVEL = { 0, 1, 4, 10, 100 };
	// 0 1 2 3 4
	private boolean pause = false;// 暂停
	private boolean gameOver = false;

	private Timer timer;

	/** 在游戏开始时候调用,[s]按下时候调用 */
	private void startGameAction() {
		gameOver = false;
		pause = false;
		score = 0;
		lines = 0;
		emptyWall();
		nextTetromino();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
			}
		}, 500, 500);
	}

	/** 在Tetris添加，检查游戏是否结束 */
	private boolean gameOver() {
		gameOver = wall[0][4] != null;
		return gameOver;
	}

	/** 在Tetris中添加 旋转流程控制，在键盘事件中调用 */
	public void rotateRightAction() {
		tetromino.rotateRight();
		if (outOfBounds() || coincide()) {
			tetromino.rotateRight();
		}
	}

	public void emptyWall() {
		for (int row = 0; row < ROWS; row++) {
			Arrays.fill(wall[row], null);//填充方法，填充数组的行
		}
	}

	/** 销毁（destroy）满行 */
	public void destroy() {
		int lines = 0;// 统计本次销毁的行数
		for (int row = 0; row < ROWS; row++) {
			if (fullCells(row)) {
				clearLine(row);
				lines++;// 每清除一行就累计加1
			}
		}
		score += SCORE_LEVEL[lines];
		this.lines += lines;
	}

	public void clearLine(int row) {
		for (int i = row; i >= 1; i--) {
			System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);
		}
		Arrays.fill(wall[0], null);
	}

	public boolean fullCells(int row) {
		Cell[] line = wall[row];
		for (int i = 0; i < line.length; i++) {
			Cell cell = line[i];
			if (cell == null) {
				return false;
			}
		}
		return true;
	}

	public String toString() {// 显示全部的墙
		String str = "";
		for (int row = 0; row < ROWS; row++) {
			Cell[] line = wall[row];
			for (int col = 0; col < COLS; col++) {
				Cell cell = line[col];
				if (tetromino.contains(row, col)) {
					str += row + "," + col + " ";
				} else {
					str += cell + " ";
				}
			}
			str += "\n";
		}
		return str;
	}

	/**
	 * 4格方块下降流程 方块移到（区域最下方）或是（着地到其他方块上无法移动时），
	 *  就会（固定在该处），而（新的方块出现)在区域上方开始落下。
	 * 如果（能下降）就继续下降 否则就（着陆到墙上），并且（生产（随机）下一个方块）
	 **/
	public void softDropAction() {
		if (canDrop()) {// 如果能下降
			tetromino.softDrop();// 方块继续下降
		} else {
			tetrominoLandToWall();// 着陆到墙上
			destroy();// 清除满行，并且记分
			if (gameOver()) {
				gameOverAction();
			}
			nextTetromino();// 生产（随机）下一个方块
		}
	}

	/** 清理游戏结束现场，如:停止定时器等 */
	private void gameOverAction() {
		timer.cancel();// 停止定时器
	}

	/** 在 Tetris 中添加方法 */
	public void hardDorpAction() {
		while (canDrop()) {
			tetromino.softDrop();// 方块继续下降
		}
		tetrominoLandToWall();// 着陆到墙上
		destroy();// 清除满行，并且记分
		if (gameOver()) {
			gameOverAction();
		}
		nextTetromino();// 生产（随机）下一个方块
	}

	/**
	 * 检查 方块 是否能够继续下落：到底最底部，或者墙上 的下方有方块,返回false不能下降，返回true可以下降
	 */
	public boolean canDrop() {
		// 检查到底部
		Cell[] cells = tetromino.getCells();
		for (Cell cell : cells) {
			if (cell.getRow() == ROWS - 1) {
				return false;
			}
		}
		// 检查墙上下方是否有方块
		for (Cell cell : cells) {
			int row = cell.getRow();
			int col = cell.getCol();
			Cell block = wall[row + 1][col];
			if (block != null) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 方块"着陆"到墙上 取出每个小cell 找到cell的行号row和列号col 将cell放置到wall[row][col] 位置上
	 * */
	public void tetrominoLandToWall() {
		Cell[] cells = tetromino.getCells();
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			int row = cell.getRow();
			int col = cell.getCol();
			wall[row][col] = cell;// 将cell放置到wall[row][col] 位置上
		}
	}
	/**
	 * 生产（随机）下一个方块 1 下一个变为当前的 2 随机产生下一个
	 * */
	public void nextTetromino() {
		if (nextOne == null) {// 第一次nextOne是null时候先生产一个
			nextOne = Tetromino.randomTetromino();
		}
		tetromino = nextOne;// 下一个变为当前的
		nextOne = Tetromino.randomTetromino();// 随机产生下一个
	}

	/**
	 * 以格子为单位左右移动方块 1) 如果遇到左右边界就不能移动了 2) 如果与墙上的格子相撞就不能移动了
	 * 
	 * 变通为： 1）先将方块左移动， 2) 检查(移动结果是否出界)，或者(重合) 3） 如果检查失败，就右移的回来
	 */
	public void moveLeftAction() {
		tetromino.moveLeft();
		if (outOfBounds() || coincide()) {
			tetromino.moveRight();
		}
	}

	public void moveRightAction() {
		tetromino.moveRight();
		if (outOfBounds() || coincide()) {
			tetromino.moveLeft();
		}
	}

	private boolean outOfBounds() {
		for (Cell cell : tetromino.getCells()) {
			int row = cell.getRow();
			int col = cell.getCol();
			if (row >= ROWS || col < 0 || col >= COLS)
				return true;
		}
		return false;
	}

	private boolean coincide() {
		for (Cell cell : tetromino.getCells()) {
			int row = cell.getRow();
			int col = cell.getCol();
			if (row >= 0 && row < ROWS && col >= 0 && col < COLS
					&& wall[row][col] != null) {
				return true;// 重合
			}
		}
		return false;
	}

	public static final int CELL_SIZE = 25;

	/** 在 Tetris.java 中添加main方法 作为软件的启动方法 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("俄罗斯方块");
		int width = (COLS + 8) * CELL_SIZE + 100;
		int height = ROWS * CELL_SIZE + 100;
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);// 居中
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置关闭窗口就关闭软件
		frame.setLayout(null);// 取消默认布局，取消自动充满
		Tetris panel = new Tetris();
		// JPanel 类利用paint（涂画）方法绘制界面
		// 子类重写paint方法可以修改绘图逻辑
		panel.setLocation(45, 35);
		panel.setSize((COLS + 8) * CELL_SIZE, ROWS * CELL_SIZE);
		panel.setBorder(new LineBorder(Color.black));
		frame.add(panel);// 窗口中添加面板
		frame.setVisible(true);// 显示窗口时候调用paint()
		panel.action();
	}

	/** 动作方法，这里是让软件开始动作 */
	private void action() {
		// wall[18][2] = new Cell(18, 2, 0xff0000);
		// nextTetromino();
		// repaint();//重绘方法->尽快调用paint()
		startGameAction();
		// this 是当前Tetris面板
		this.requestFocus();// 为当前面板请求获得输入焦点
		// this对象就获得了输入焦点，以后任何的
		// 键盘输入(包括左右方向键)目标就是这个面板对象了！
		// addKeyListener添加键盘监听，监听那些按键输入了
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();// key按键
				if (gameOver) {
					if (key == KeyEvent.VK_S) {
						startGameAction();// 启动游戏开始流程
					}
					return;
				}
				if (pause) {
					if (key == KeyEvent.VK_C) {
						continueAction();
					}
					return;
				}
				switch (key) {
				case KeyEvent.VK_RIGHT:
					moveRightAction();
					break;
				case KeyEvent.VK_LEFT:
					moveLeftAction();
					break;
				case KeyEvent.VK_DOWN:
					softDropAction();
					break;
				case KeyEvent.VK_UP:
					rotateRightAction();
					break;
				case KeyEvent.VK_SPACE:
					hardDorpAction();
					break;
				case KeyEvent.VK_P:
					pauseAction();
					break;
				}
				// 按键->方块移动方法->改变方块数据->repaint()
				// ->尽快调用 paint()->利用新数据绘制
				repaint();
			}

			private void pauseAction() {
				pause = true;
				timer.cancel();
			}

			private void continueAction() {
				pause = false;
				timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						softDropAction();
						repaint();
					}
				}, 500, 500);
			}
		});
	}

	/**
	 * JPanel 类利用paint（涂画）方法绘制界面 子类重写paint方法可以修改绘图逻辑 g 代表绑定在当前面板上的画笔 利用画笔在当前 面板上
	 * 绘制了一串字符！ g.drawString("Have a nice day!", 100, 100);
	 */
	public static final int BORDER_COLOR = 0x667799;
	public static final int BG_COLOR = 0xC3D5EA;

	public void paint(Graphics g) {
		paintBackground(g);// 填充背景
		paintWall(g);// 绘制墙
		paintTetromino(g);// 绘制当前方块
		paintNextOne(g);// 绘制下一个方块
		paintScore(g);// 绘制分数
		paintTetrisBorder(g);// （绘制边线）
	}

	public static final int FONT_COLOR = 0;

	private void paintScore(Graphics g) {
		int x = 12 * CELL_SIZE;
		int y = 6 * CELL_SIZE;
		Font font = new Font(getFont().getName(), Font.BOLD, 25);
		String str = "分数：" + score;
		g.setColor(new Color(FONT_COLOR));// 字体颜色
		g.setFont(font);// 设置字体
		g.drawString(str, x, y);
		y += 2 * CELL_SIZE;
		str = "行数：" + lines;
		g.drawString(str, x, y);
		if (gameOver) {
			str = "(T_T)[S]再来！";
			y += 2 * CELL_SIZE;
			g.drawString(str, x - CELL_SIZE, y);
		}
		if (pause) {
			str = "[C]继续！";
			y += 2 * CELL_SIZE;
			g.drawString(str, x, y);
		} else {
			str = "[P]暂停！";
			y += 2 * CELL_SIZE;
			g.drawString(str, x, y);
		}
	}

	private void paintNextOne(Graphics g) {
		if (nextOne == null) {// 如果没有4格方块就返回，不绘制
			return;
		}
		for (Cell cell : nextOne.getCells()) {
			int row = cell.getRow() + 1;
			int col = cell.getCol() + 9;
			int x = col * CELL_SIZE;
			int y = row * CELL_SIZE;
			g.setColor(new Color(cell.getColor()));
			g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
			g.setColor(new Color(BORDER_COLOR));
			g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
		}
	}

	private void paintTetromino(Graphics g) {
		if (tetromino == null) {// 如果没有4格方块就返回，不绘制
			return;
		}
		for (Cell cell : tetromino.getCells()) {
			int row = cell.getRow();
			int col = cell.getCol();
			int x = col * CELL_SIZE;
			int y = row * CELL_SIZE;
			g.setColor(new Color(cell.getColor()));
			g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
			g.setColor(new Color(BORDER_COLOR));
			g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
		}
	}

	private void paintWall(Graphics g) {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Cell cell = wall[row][col];
				int x = col * CELL_SIZE;
				int y = row * CELL_SIZE;
				if (cell == null) {
					// g.setColor(new Color(BORDER_COLOR));
					// g.drawRect(x,y,CELL_SIZE, CELL_SIZE);
				} else {
					g.setColor(new Color(cell.getColor()));
					g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
					g.setColor(new Color(BORDER_COLOR));
					g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
				}
			}
		}
	}

	private void paintBackground(Graphics g) {
		g.setColor(new Color(BG_COLOR));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	private void paintTetrisBorder(Graphics g) {
		g.setColor(new Color(BORDER_COLOR));
		g.drawRect(0, 0, CELL_SIZE * COLS, getHeight() - 1);
		g.drawRect(CELL_SIZE * COLS, 0, CELL_SIZE * 8 - 1, getHeight() - 1);
	}
}
