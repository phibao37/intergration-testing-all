package test.foo;

public class PassObjectOrReference {
	
	public static void main(String[] args){
		Object s = new String("hello");
		
		System.out.println(s.getClass());
		foo(s);
		foo((String)s);
	}
	
	public static void foo(Object o){
		System.out.println("Passing an object");
	}
	
	public static void foo(String o){
		System.out.println("Passing a string");
	}
}
