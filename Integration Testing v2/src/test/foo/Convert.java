package test.foo;

public class Convert {
	
	public static void main(String[] args){
		//System.out.println(dec2bin(16));
		System.out.println(23 & ~-1);
	}
	
	public static long dec2bin(int x){
		if (x < 2)
			return x;
		return dec2bin(x/2) * 10 + (x%2);
	}
	
}
