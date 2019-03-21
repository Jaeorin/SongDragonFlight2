package private_190313;

class Missile {

	int x;
	int y;
	int missileSpeed;
	double angle;
	int who;

	Missile(int x, int y, double angle, int missileSpeed, int who) {

		this.x = x;
		this.y = y;
		this.who = who;
		this.angle = angle;
		this.missileSpeed = missileSpeed;

	}

	public void move() {

		x -= Math.cos(Math.toRadians(angle)) * missileSpeed;
		y -= Math.sin(Math.toRadians(angle)) * missileSpeed;

	}

}
