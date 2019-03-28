package source;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		System.out.println("Game Start");
		GameFrame gameframe = new GameFrame(); // 게임실행객체 생성

	}

}

@SuppressWarnings("serial")
class GameFrame extends Frame implements Runnable, KeyListener {

	// 키설정
	boolean KeyUp = false; // 위
	boolean KeyDown = false; // 아래
	boolean KeyLeft = false; // 왼쪽
	boolean KeyRight = false; // 오른쪽
	boolean KeyZ = false; // 투사체 발사
	boolean KeyX = false; // 폭탄

	// !!중요!!
	// 비트 연산자
	// 왜 써야 하는지??
	// https://elkeipy.tistory.com/entry/%EB%B9%84%ED%8A%B8-%EC%97%B0%EC%82%B0%EC%9E%90
	public final static int UP_PRESSED = 0x001;
	public final static int DOWN_PRESSED = 0x002;
	public final static int LEFT_PRESSED = 0x004;
	public final static int RIGHT_PRESSED = 0x008;
	public final static int FIRE_PRESSED = 0x010;
	public final static int BOMB_PRESSED = 0x020;

	// 사용되는 변수 선언
	int screenWidth; // 전체화면 가로크기
	int screenHeight; // 전체화면 세로크기
	int playerx; // 플레이어의 X좌표 위치
	int playery; // 플레이어의 Y좌표 위치
	int enemyWidht; // 적의 넓이
	int enemyHeight; // 적의 높이
	int missileWidht; // 투사체의 넓이
	int missileHeight; // 투사체의 높이
	int backgroundMove = 0; // 배경화면 이동속도
	int playerSpeed; // 플레이어 이동속도
	int missileSpeed; // 투사체 이동속도
	int fireSpeed; // 투사체 발사속도
	int enemySpeed; // 적 이동속도
	int playerStatus = 0; // 플레이어의 상태(평상시, 발사시 등)
	int gameScore; // 점수
	int playerLife; // 목숨
	int loopCounter; // run()함수의 루프 횟수를 기록하여 객체들의 행동 시간 개념 구현

	// 이미지 파일을 받아올 변수 선언
	Image backgroundImg;
	Image backgroundImg2;
	Image UIImg;
	Image[] playerImg;
	Image missileImg;
	Image enemyImg;
	Image enemyMissileImg;
	Image[] explosionImg;

	Missile missile; // 미사일 객체 타입 생성
	Enemy enemy; // 적 객체 타입 생성
	Explosion explosion; // 폭팔 모션 객체 타입 생성
	Toolkit toolkit = Toolkit.getDefaultToolkit(); // !!주의!! 뭔지 모르겠음

	ArrayList<Missile> missileList = new ArrayList<>(); // 생성되는 미사일 객체를 저장하기 위한 ArrayList 생성
	ArrayList<Enemy> enemyList = new ArrayList<>(); // 생성되는 적 객체를 저장하기 위한 ArrayList 생성
	ArrayList<Explosion> explosionList = new ArrayList<>(); // 생성되는 폭팔모션 객체를 저장하기 위한 ArrayList 생성

	// !!주의!! 이중 버퍼링 구현에 필요한 함수 선언
	Image buffImage;
	Graphics buffGraphics;

	gameFrame() {

		init(); // init()함수 실행
		start(); // start()함수 실행

		// JFrame 클래스가 상속하고 있는 Frame이 가지고 있는 setTitle 함수 실행
		// 팝업되는 프레임의 제목을 설정
		setTitle("Song Dragon Flight 2");

		// java.awt.Image 클래스를 참조하는 ImageObserver 인터페이스의 Component가 상속하는
		// Container.winows클래스의 setSize 함수를 실행
		// 팝업되는 프레임의 가로,세로크기를 설정
		setSize(screenWidth, screenHeight); // init()함수에서 설정된 가로세로크기 변수를 받아옴

		// !!주의!! 프레임이 윈도우에 표시될 때 위치를 세팅하기 위해 현재 해상도 값을 받아옴
		Dimension screen = toolkit.getScreenSize();

		// 프레임을 모니터 화면 정중앙에 배치시키기 위한 좌표값 계산식
		int fXpos = (int) (screen.getWidth() / 2 - screenWidth / 2);
		int fYpos = (int) (screen.getHeight() / 2 - screenHeight / 2);

		// java.awt.Image 클래스를 참조하는 ImageObserver 인터페이스의 Component가 상속하는
		// Container.winows클래스의 setLocation 함수를 실행
		// 프레임을 지정된 좌표로 화면에 배치
		setLocation(fXpos, fYpos);

		// java.awt.Image 클래스를 참조하는 ImageObserver 인터페이스의 Component가 상속하는
		// Container.winows클래스의 setVisible 함수를 실행
		// 프레임이 눈에 보이는지 여부에 true설정(false시 프레임이 보이지 않음)
		setVisible(true);

		// JFrame 클래스가 상속하고 있는 Frame이 가지고 있는 setResizable 함수 실행
		// 프레임의 크기 변경여부에 false를 설정(true시 창의 크기를 마우스로 조절 가능)
		setResizable(false);

	}

	// 게임 실행(메인 스택에 gameFrame 객체 생성 시)시 게임에 적용되는 변수값을 초기화하는 함수
	public void init() { // gameFrame함수에서 실행됨

		// 선언된 변수값 설정
		screenWidth = 1280;
		screenHeight = 1024;
		playerx = 410;
		playery = 900;
		playerSpeed = 10;
		missileSpeed = 11;
		fireSpeed = 5;
		enemySpeed = 2;
		gameScore = 0;
		playerLife = 3;

		// 이미지 불러오기
		backgroundImg = new ImageIcon("C:\\workspace\\Java\\src\\private_190313\\Image\\background.png").getImage();

		UIImg = new ImageIcon("C:\\workspace\\Java\\src\\private_190313\\Image\\UIImage.png").getImage();

		playerImg = new Image[5];

		for (int i = 0; i < playerImg.length; ++i) {

			playerImg[i] = new ImageIcon("C:\\workspace\\Java\\src\\private_190313\\Image\\player" + i + ".png")
					.getImage();

		}

		missileImg = new ImageIcon("C:\\Workspace\\Java\\src\\private_190313\\Image\\missile.png").getImage();

		enemyImg = new ImageIcon("C:\\workspace\\Java\\src\\private_190313\\Image\\enemy.png").getImage();

		enemyMissileImg = new ImageIcon("C:\\Workspace\\Java\\src\\private_190313\\Image\\enemyMissile.png").getImage();

		explosionImg = new Image[3];

		for (int i = 0; i < explosionImg.length; ++i) {

			explosionImg[i] = new ImageIcon("C:\\workspace\\Java\\src\\private_190313\\Image\\explosion" + i + ".png")
					.getImage();

		}

		// 소리 불러오기
		// true는 지속적으로 출력
		// false는 한번만 출력
		Sound("C:\\Workspace\\Java\\src\\private_190313\\Sound\\backGroundSound.wav", true);

	}

	// 게임 실행(메인 스택에 gameFrame 객체 생성 시)시 프레임을 시작하는 함수
	public void start() { // gameFrame함수에서 실행됨

		// JFrame 클래스의 setDefaultCloseOperation함수를 실행
		// 프레임 우측 상단의 종료 버튼을 눌렀을 때 프로그램을 종료
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Component 클래스의 addKeyListener 함수를 실행
		// 키보드 입력값을 얻음
		addKeyListener(this);

		// 새로운 스레드 객체 생성
		Thread thread = new Thread(this);

		// Thread 클래스의 start() 함수를 실행
		// start()함수로 연산 시작하며 아래 run()함수를 계산하기 시작함
		thread.start();

	}

	// !!중요!!
	// implement된 Runnable에 동적바인딩 되어있는 Thread의 run()함수 내용 설정
	// 프로그램이 실행되는 구조
	// gameFrame클래스 객체 생성 → gameFrame()함수 실행 → 내부의 start()함수 실행 → 내부의 스레드 실행
	// → 내부의 run()함수 실행
	@Override
	public void run() {

		try {

			// 게임이 종료되기 전까지 계속 반복됨
			while (true) {

				// 각 함수들을 동작시킴
				playerMoveProcess();
				missileProcess();
				enemyProcess();
				explosionProcess();

				// paint()함수가 가지고 있으며 호출 시 repaint() → update() → paint() 순서로 함수가 호출된다
				repaint();
				// 이 반복문에 그래픽을 바로 출력하면 스레드에서 각각의 이미지들을 그리기 시작해서 끝나는 부분까지
				// 모두 보여주기 때문에 이미지들이 깜빡이기게 된다
				// 이를 해결하기 위해 repaint()로 update()를 호출하여 이미지를 메모리에 모두 업데이트한 후
				// paint()함수로 메모리에 최종적으로 그려진 그래픽을 한번만 출력한다
				// (더블버퍼링 개념)

				// 스레드를 잠깐 일시정지, (숫자)/1000초만큼 정지하며, 12/1000초는 약 80프레임으로 화면을 연산한다
				Thread.sleep(100);

				loopCounter++;

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	// !!중요!!
	// java.awt.Image 클래스를 참조하는 ImageObserver 인터페이스의 Component가 상속하는
	// Container.winows클래스의 paint 함수를 실행
	// 그래픽 출력 이벤트를 처리함
	@Override
	public void paint(Graphics graphics) {

		buffImage = createImage(screenWidth, screenHeight); // createImage() : 버퍼링용 화면 크기 설정
		buffGraphics = buffImage.getGraphics(); // 생성된 이미지를 그릴 그래픽 객체를 생성한다
		update(graphics); // update()가 paint()를 호출하기 때문에 반복적으로 함수가 실행된다

	}

	// JFrame 클래스의 update함수를 실행
	// paint()함수가 가지고 있으며 처리한 이미지를 갱신함
	@Override
	public void update(Graphics graphics) {

		// 이미지들을 그리는 함수
		drawBackground();
		drawPlayer();
		drawEnemy();
		drawMissile();
		drawExplosion();
		drawUI();
		drawStatusText();

		// 버퍼링용 화면에 그려진 이미지를 실제 화면으로 옮긴다
		graphics.drawImage(buffImage, 0, 0, this);

	}

	// implement된 KeyListener에 동적바인딩 되어있는 KeyEventDemo의 keyPressed를 실행
	// 키보드가 눌러질 때 이벤트 처리
	@Override
	public void keyPressed(KeyEvent event) {

		switch (event.getKeyCode()) {

		case KeyEvent.VK_UP:

			KeyUp = true;
			break;

		case KeyEvent.VK_DOWN:

			KeyDown = true;
			break;

		case KeyEvent.VK_LEFT:

			KeyLeft = true;
			break;

		case KeyEvent.VK_RIGHT:

			KeyRight = true;
			break;

		case KeyEvent.VK_Z:

			KeyZ = true;
			break;

		}

	}

	// implement된 KeyListener에 동적바인딩 되어있는 KeyEventDemo의 keyReleased를 실행
	// 키보드가 떼어질 때 이벤트 처리
	@Override
	public void keyReleased(KeyEvent event) {

		switch (event.getKeyCode()) {

		case KeyEvent.VK_UP:

			KeyUp = false;
			break;

		case KeyEvent.VK_DOWN:

			KeyDown = false;
			break;

		case KeyEvent.VK_LEFT:

			KeyLeft = false;
			break;

		case KeyEvent.VK_RIGHT:

			KeyRight = false;
			break;

		case KeyEvent.VK_Z:

			KeyZ = false;
			break;

		}

	}

	// implement된 KeyListener에 동적바인딩 되어있는 KeyEventDemo의 keyTyped를 실행
	// 키보드가 타이핑될 떄 이벤트 처리(필요없는 부분이기 때문에 오버라이딩만 함)
	@Override
	public void keyTyped(KeyEvent event) {

	}

	// 입력되는 키에 따라서 플레이어 기체의 좌표 움직임
	// if문에 조건을 달아 화면 밖으로 플레이어 기체가 벗어나지 못하게 가둠
	public void playerMoveProcess() {

		if (KeyUp == true) {

			if (playery > 30) {

				playery -= playerSpeed;

			}

			playerStatus = 0;

		}

		if (KeyDown == true) {

			if (playery + 60 < screenHeight) {

				playery += playerSpeed;

			}

			playerStatus = 0;

		}

		if (KeyLeft == true) {

			if (playerx > 0) {

				playerx -= playerSpeed;

			}

			playerStatus = 0;

		}

		if (KeyRight == true) {

			if (playerx + 460 < screenWidth) {

				playerx += playerSpeed;

			}

			playerStatus = 0;

		}

	}

	// !!중요!!
	// 투사체 연산
	public void missileProcess() {

		if (KeyZ == true) { // Z 키가 눌려졌을 떄

			// 플레이어 상태가 1번(발사중)으로 변한다
			// drawPlayer() 함수 switch문의 조건에 참조되는 값
			playerStatus = 1;

			// 루프카운터 / 투사제 발사속도 나눗셈의 결과값이 0일 때 투사체를 발사한다
			if ((loopCounter % fireSpeed) == 0) {

				// 인자값이 적용된 새로운 Missile 객체를 생성
				// missileList에 생성된 객체를 저장
				// 인자 마지막은 투사체의 피아구분 여부(0은 아군의 미사일, 1은 적의 미사일)
				missile = new Missile(playerx + 14, playery + 8, 90, missileSpeed, 0);
				missileList.add(missile);
				missile = new Missile(playerx + 22, playery + 4, 90, missileSpeed, 0);
				missileList.add(missile);
				missile = new Missile(playerx + 30, playery, 90, missileSpeed, 0);
				missileList.add(missile);
				missile = new Missile(playerx + 38, playery + 4, 90, missileSpeed, 0);
				missileList.add(missile);
				missile = new Missile(playerx + 46, playery + 8, 90, missileSpeed, 0);
				missileList.add(missile);

				// 해당 반복문 종료(Missile 객체 생성에 성공) 시 효과음 출력
				Sound("C:\\Workspace\\Java\\src\\private_190313\\Fire.wav", false);

			}

		}

		// 미사일 존재여부 확인
		for (int i = 0; i < missileList.size(); ++i) {

			// 위에서 저장한 missileList에 저장된 객체 지정
			missile = (Missile) missileList.get(i);

			// missile 객체가 가진 move()함수에 의해 연산된 값으로 객체 이동
			missile.move();

			// 만약 이동했을 때 화면을 벗어나게 된다면 해당 객체를 missileList에서 삭제
			if (missile.x > screenWidth - 160 || missile.x < -40 || missile.y < 0 || missile.y > screenHeight) {
				missileList.remove(i);
			}

			// crash()함수 호출
			//
			if (Crash(playerx, playery, missile.x, missile.y, playerImg[0], missileImg) && missile.who == 1) {

				playerLife--;

				explosion = new Explosion(playerx + playerImg[0].getWidth(null) / 2,
						playery + playerImg[0].getHeight(null) / 2, 0);
				explosionList.add(explosion);
				missileList.remove(i);

			}

			for (int j = 0; j < enemyList.size(); ++j) {

				enemy = (Enemy) enemyList.get(j);

				if (Crash(missile.x, missile.y, enemy.x, enemy.y, missileImg, enemyImg) && missile.who == 0) {

					missileList.remove(i);
					enemy.enemyLife--;
					if (enemy.enemyLife <= 0)
						enemyList.remove(j);

					gameScore += 10;

					explosion = new Explosion(enemy.x + enemyImg.getWidth(null) / 2,
							enemy.y + enemyImg.getHeight(null) / 2, 0);

					explosionList.add(explosion);
					Sound("C:\\Workspace\\Java\\src\\private_190313\\explosion.wav", false);

				}

			}

		}

	}

	public void enemyProcess() {

		for (int i = 0; i < enemyList.size(); ++i) {

			enemy = (Enemy) (enemyList.get(i));

			enemy.move();

			if (enemy.y > 900) {

				enemyList.remove(i);

			}

			if (loopCounter % 80 == 0 || loopCounter % 90 == 0 || loopCounter % 100 == 0) {

			}

			if (Crash(playerx, playery, enemy.x, enemy.y, playerImg[0], enemyImg)) {

				playerLife--;
				enemyList.remove(i);

				gameScore += 10;

				explosion = new Explosion(enemy.x + enemyImg.getWidth(null) / 2,
						enemy.y + playerImg[0].getHeight(null) / 2, 0);

				explosionList.add(explosion);

				explosion = new Explosion(playerx, playery, 1);

				explosionList.add(explosion);

			}

		}

		if (loopCounter % 200 == 0)

		{
			enemy = new Enemy(150, 50, enemySpeed);
			enemyList.add(enemy);
//			enemy = new Enemy(300, 50, enemySpeed);
//			enemyList.add(enemy);
//			enemy = new Enemy(450, 50, enemySpeed);
//			enemyList.add(enemy);
//			enemy = new Enemy(600, 50, enemySpeed);
//			enemyList.add(enemy);
//			enemy = new Enemy(750, 50, enemySpeed);
//			enemyList.add(enemy);

		}

	}

	public void explosionProcess() {

		for (int i = 0; i < explosionList.size(); ++i) {

			explosion = (Explosion) explosionList.get(i);
			explosion.effect();

		}

	}

	public void drawBackground() {

		buffGraphics.clearRect(0, 0, screenWidth, screenHeight);

		if (backgroundMove < screenHeight) {

			buffGraphics.drawImage(backgroundImg, 0, backgroundMove - screenHeight, this);
			backgroundMove += 2;

		} else {

			buffGraphics.drawImage(backgroundImg, 0, backgroundMove - screenHeight, this);
			backgroundMove = 0;

		}

	}

	public void drawPlayer() {

		switch (playerStatus) {

		case 0:

			if ((loopCounter / 5 % 2) == 0) {
				buffGraphics.drawImage(playerImg[1], playerx, playery, this);

			} else {

				buffGraphics.drawImage(playerImg[2], playerx, playery, this);

			}

			break;

		case 1:

			if ((loopCounter / 5 % 2) == 0) {

				buffGraphics.drawImage(playerImg[3], playerx, playery, this);

			} else {

				buffGraphics.drawImage(playerImg[4], playerx, playery, this);

			}

			playerStatus = 0;

			break;

		case 2:

			break;

		}

	}

	public void drawEnemy() {

		for (int i = 0; i < enemyList.size(); ++i) {

			enemy = (Enemy) (enemyList.get(i));
			buffGraphics.drawImage(enemyImg, enemy.x, enemy.y, this);

		}

	}

	public void drawMissile() {

		for (int i = 0; i < missileList.size(); i++) {

			missile = (Missile) (missileList.get(i));

			if (missile.who == 0) {

				buffGraphics.drawImage(missileImg, missile.x, missile.y, this);

			}

			if (missile.who == 1) {

				buffGraphics.drawImage(enemyMissileImg, missile.x, missile.y, this);

			}

		}

	}

	public void drawExplosion() {

		for (int i = 0; i < explosionList.size(); ++i) {

			explosion = (Explosion) explosionList.get(i);

			if (explosion.imgScroll == 0) {

				if (explosion.imgCounter < 7) {

					buffGraphics.drawImage(explosionImg[0], explosion.x - explosionImg[0].getWidth(null) / 2,
							explosion.y - explosionImg[0].getHeight(null) / 2, this);

				} else if (explosion.imgCounter < 14) {

					buffGraphics.drawImage(explosionImg[1], explosion.x - explosionImg[1].getWidth(null) / 2,
							explosion.y - explosionImg[1].getHeight(null) / 2, this);

				} else if (explosion.imgCounter < 21) {

					buffGraphics.drawImage(explosionImg[2], explosion.x - explosionImg[2].getWidth(null) / 2,
							explosion.y - explosionImg[2].getHeight(null) / 2, this);

				} else if (explosion.imgScroll > 21) {

					explosionList.remove(i);
					explosion.imgScroll = 0;

				}

			} else {

				if (explosion.imgCounter < 7) {

					buffGraphics.drawImage(explosionImg[0], explosion.x + 90, explosion.y + 15, this);

				} else if (explosion.imgScroll < 14) {

					buffGraphics.drawImage(explosionImg[1], explosion.x + 60, explosion.y + 5, this);

				} else if (explosion.imgScroll < 21) {

					buffGraphics.drawImage(explosionImg[0], explosion.x + 5, explosion.y + 10, this);

				} else if (explosion.imgScroll > 21) {

					explosionList.remove(i);
					explosion.imgScroll = 0;

				}

			}

		}

	}

	public void drawUI() {

		buffGraphics.drawImage(UIImg, 0, 0, this);

	}

	public void drawStatusText() {

		buffGraphics.setFont(new Font("Defualt", Font.BOLD, 18));
		buffGraphics.drawString("SCORE : " + gameScore, 920, 70);
		buffGraphics.drawString("HitPoint : " + playerLife, 920, 100);
		buffGraphics.drawString("Missile Count : " + missileList.size(), 920, 130);
		buffGraphics.drawString("Enemy Count : " + enemyList.size(), 920, 160);
		buffGraphics.drawString("x : " + playerx, 920, 190);
		buffGraphics.drawString("y : " + playery, 920, 220);
		buffGraphics.drawString("count : " + loopCounter, 920, 250);

	}

	public boolean Crash(int x1, int y1, int x2, int y2, Image i1, Image i2) {

		boolean check = false;

		if (Math.abs((x1 + i1.getWidth(null) / 2) - (x2 + i2.getWidth(null) / 2)) < (i2.getWidth(null) / 2
				+ i1.getWidth(null) / 2)
				&& Math.abs((y1 + i1.getHeight(null) / 2) - (y2 + i2.getHeight(null) / 2)) < (i2.getHeight(null) / 2
						+ i1.getHeight(null) / 2)) {

			check = true;

		} else {

			check = false;

		}

		return check;

	}

	private static double getAngle(int x1, int y1, int x2, int y2) {
		int dx = x2 - x1;
		int dy = y2 - y1;

		double rad = Math.atan2(dy, dx);
		double degree = (rad * 180) / Math.PI;

		return degree;
	}

	public void Sound(String file, boolean Loop) {

		Clip clip;

		try {

			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();

			if (Loop)

				clip.loop(-1);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	class Enemy {

		int x;
		int y;
		int enemySpeed;
		int enemyLife = 10;

		Enemy(int x, int y, int enemySpeed) {

			this.x = x;
			this.y = y;
			this.enemySpeed = enemySpeed;

		}

		public void move() {

			y += 3;

		}

	}

	class Explosion {

		int x;
		int y;
		int imgCounter;
		int imgScroll;

		Explosion(int x, int y, int imgScroll) {

			this.x = x;
			this.y = y;
			this.imgScroll = imgScroll;
			imgCounter = 0;

		}

		public void effect() {

			imgCounter++;

		}

	}

}
