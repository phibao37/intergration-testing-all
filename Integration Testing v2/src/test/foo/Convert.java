package test.foo;

public class Convert {
	
	public static void main(String[] args){
		//System.out.println(dec2bin(16));
		int a[] = {1, 2, 4, 3};
		
		for (int i: a){
			i++;
			System.out.println(i);
		}
	}
	
	public static long dec2bin(int x){
		if (x < 2)
			return x;
		return dec2bin(x/2) * 10 + (x%2);
	}
	
}
