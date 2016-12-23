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
 * ����˹���� �� ����˹���� ��չ��(extends)ϵͳ����ʵ��壬������ǽ�� ��������ķ���
 */
public class Tetris extends JPanel {
	public static final int ROWS = 20;//��ʼ����Ϊ20��
	public static final int COLS = 10;//��ʼ����Ϊ10��
	/** ������������½��ǽ */
	private Cell[][] wall = new Cell[ROWS][COLS];
	/** ����������ط��� */
	private Tetromino tetromino;
	/** ��һ������ķ��� */
	private Tetromino nextOne;

	private int score;//����÷�
	private int lines;//����
	private static final int[] SCORE_LEVEL = { 0, 1, 4, 10, 100 };
	// 0 1 2 3 4
	private boolean pause = false;// ��ͣ
	private boolean gameOver = false;

	private Timer timer;

	/** ����Ϸ��ʼʱ�����,[s]����ʱ����� */
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

	/** ��Tetris��ӣ������Ϸ�Ƿ���� */
	private boolean gameOver() {
		gameOver = wall[0][4] != null;
		return gameOver;
	}

	/** ��Tetris����� ��ת���̿��ƣ��ڼ����¼��е��� */
	public void rotateRightAction() {
		tetromino.rotateRight();
		if (outOfBounds() || coincide()) {
			tetromino.rotateRight();
		}
	}

	public void emptyWall() {
		for (int row = 0; row < ROWS; row++) {
			Arrays.fill(wall[row], null);//��䷽��������������
		}
	}

	/** ���٣�destroy������ */
	public void destroy() {
		int lines = 0;// ͳ�Ʊ������ٵ�����
		for (int row = 0; row < ROWS; row++) {
			if (fullCells(row)) {
				clearLine(row);
				lines++;// ÿ���һ�о��ۼƼ�1
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

	public String toString() {// ��ʾȫ����ǽ
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
	 * 4�񷽿��½����� �����Ƶ����������·������ǣ��ŵص������������޷��ƶ�ʱ����
	 *  �ͻᣨ�̶��ڸô����������µķ������)�������Ϸ���ʼ���¡�
	 * ��������½����ͼ����½� ����ͣ���½��ǽ�ϣ������ң��������������һ�����飩
	 **/
	public void softDropAction() {
		if (canDrop()) {// ������½�
			tetromino.softDrop();// ��������½�
		} else {
			tetrominoLandToWall();// ��½��ǽ��
			destroy();// ������У����ҼǷ�
			if (gameOver()) {
				gameOverAction();
			}
			nextTetromino();// �������������һ������
		}
	}

	/** ������Ϸ�����ֳ�����:ֹͣ��ʱ���� */
	private void gameOverAction() {
		timer.cancel();// ֹͣ��ʱ��
	}

	/** �� Tetris ����ӷ��� */
	public void hardDorpAction() {
		while (canDrop()) {
			tetromino.softDrop();// ��������½�
		}
		tetrominoLandToWall();// ��½��ǽ��
		destroy();// ������У����ҼǷ�
		if (gameOver()) {
			gameOverAction();
		}
		nextTetromino();// �������������һ������
	}

	/**
	 * ��� ���� �Ƿ��ܹ��������䣺������ײ�������ǽ�� ���·��з���,����false�����½�������true�����½�
	 */
	public boolean canDrop() {
		// ��鵽�ײ�
		Cell[] cells = tetromino.getCells();
		for (Cell cell : cells) {
			if (cell.getRow() == ROWS - 1) {
				return false;
			}
		}
		// ���ǽ���·��Ƿ��з���
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
	 * ����"��½"��ǽ�� ȡ��ÿ��Сcell �ҵ�cell���к�row���к�col ��cell���õ�wall[row][col] λ����
	 * */
	public void tetrominoLandToWall() {
		Cell[] cells = tetromino.getCells();
		for (int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			int row = cell.getRow();
			int col = cell.getCol();
			wall[row][col] = cell;// ��cell���õ�wall[row][col] λ����
		}
	}
	/**
	 * �������������һ������ 1 ��һ����Ϊ��ǰ�� 2 ���������һ��
	 * */
	public void nextTetromino() {
		if (nextOne == null) {// ��һ��nextOne��nullʱ��������һ��
			nextOne = Tetromino.randomTetromino();
		}
		tetromino = nextOne;// ��һ����Ϊ��ǰ��
		nextOne = Tetromino.randomTetromino();// ���������һ��
	}

	/**
	 * �Ը���Ϊ��λ�����ƶ����� 1) ����������ұ߽�Ͳ����ƶ��� 2) �����ǽ�ϵĸ�����ײ�Ͳ����ƶ���
	 * 
	 * ��ͨΪ�� 1���Ƚ��������ƶ��� 2) ���(�ƶ�����Ƿ����)������(�غ�) 3�� ������ʧ�ܣ������ƵĻ���
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
				return true;// �غ�
			}
		}
		return false;
	}

	public static final int CELL_SIZE = 25;

	/** �� Tetris.java �����main���� ��Ϊ������������� */
	public static void main(String[] args) {
		JFrame frame = new JFrame("����˹����");
		int width = (COLS + 8) * CELL_SIZE + 100;
		int height = ROWS * CELL_SIZE + 100;
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);// ����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ���ùرմ��ھ͹ر����
		frame.setLayout(null);// ȡ��Ĭ�ϲ��֣�ȡ���Զ�����
		Tetris panel = new Tetris();
		// JPanel ������paint��Ϳ�����������ƽ���
		// ������дpaint���������޸Ļ�ͼ�߼�
		panel.setLocation(45, 35);
		panel.setSize((COLS + 8) * CELL_SIZE, ROWS * CELL_SIZE);
		panel.setBorder(new LineBorder(Color.black));
		frame.add(panel);// ������������
		frame.setVisible(true);// ��ʾ����ʱ�����paint()
		panel.action();
	}

	/** �����������������������ʼ���� */
	private void action() {
		// wall[18][2] = new Cell(18, 2, 0xff0000);
		// nextTetromino();
		// repaint();//�ػ淽��->�������paint()
		startGameAction();
		// this �ǵ�ǰTetris���
		this.requestFocus();// Ϊ��ǰ������������뽹��
		// this����ͻ�������뽹�㣬�Ժ��κε�
		// ��������(�������ҷ����)Ŀ���������������ˣ�
		// addKeyListener��Ӽ��̼�����������Щ����������
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();// key����
				if (gameOver) {
					if (key == KeyEvent.VK_S) {
						startGameAction();// ������Ϸ��ʼ����
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
				// ����->�����ƶ�����->�ı䷽������->repaint()
				// ->������� paint()->���������ݻ���
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
	 * JPanel ������paint��Ϳ�����������ƽ��� ������дpaint���������޸Ļ�ͼ�߼� g ������ڵ�ǰ����ϵĻ��� ���û����ڵ�ǰ �����
	 * ������һ���ַ��� g.drawString("Have a nice day!", 100, 100);
	 */
	public static final int BORDER_COLOR = 0x667799;
	public static final int BG_COLOR = 0xC3D5EA;

	public void paint(Graphics g) {
		paintBackground(g);// ��䱳��
		paintWall(g);// ����ǽ
		paintTetromino(g);// ���Ƶ�ǰ����
		paintNextOne(g);// ������һ������
		paintScore(g);// ���Ʒ���
		paintTetrisBorder(g);// �����Ʊ��ߣ�
	}

	public static final int FONT_COLOR = 0;

	private void paintScore(Graphics g) {
		int x = 12 * CELL_SIZE;
		int y = 6 * CELL_SIZE;
		Font font = new Font(getFont().getName(), Font.BOLD, 25);
		String str = "������" + score;
		g.setColor(new Color(FONT_COLOR));// ������ɫ
		g.setFont(font);// ��������
		g.drawString(str, x, y);
		y += 2 * CELL_SIZE;
		str = "������" + lines;
		g.drawString(str, x, y);
		if (gameOver) {
			str = "(T_T)[S]������";
			y += 2 * CELL_SIZE;
			g.drawString(str, x - CELL_SIZE, y);
		}
		if (pause) {
			str = "[C]������";
			y += 2 * CELL_SIZE;
			g.drawString(str, x, y);
		} else {
			str = "[P]��ͣ��";
			y += 2 * CELL_SIZE;
			g.drawString(str, x, y);
		}
	}

	private void paintNextOne(Graphics g) {
		if (nextOne == null) {// ���û��4�񷽿�ͷ��أ�������
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
		if (tetromino == null) {// ���û��4�񷽿�ͷ��أ�������
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
