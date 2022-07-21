package cn.edu.scut;

import java.util.Random;

public class text {

	private static int encrypt(int x, int r, int d) {
		
		int c = x - r % d;
		c = c < 0 ? c + d : c;
		return c;
	}
	
	public static void main(String[] args) {
		
		Random rnd = new Random();
		
		int sumr = 0, sumc = 0, sumx = 0;
		int size = 400;
		int count = 0;
		for(int i = 0; i < size; i++) {
			
			int r = rnd.nextInt(256);
			sumr += r;
			int x = rnd.nextInt(256);
			sumx += x;
			int c = encrypt(x, r, 255);
//			int c = (x - r) % 255;
			sumc += c;
			if(x < r)
				count++;
		}
		System.out.println((sumr / size + sumc / size - 255 * count / size) + " - " + (sumx / size));
	}
}
