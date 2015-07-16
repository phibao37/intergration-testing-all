package test.foo;

abstract class Coffee{
	
	protected String mDescription;
	protected int mCost;
	
	public Coffee(String description, int cost){
		mDescription = description;
		mCost = cost;
	}
	
	/**
	 * Mô tả về món hàng
	 */
	public String getDescription(){
		return mDescription;
	}
	
	/**
	 * Giá của coffee
	 */
	public int cost(){
		return mCost;
	}

	@Override
	public String toString() {
		return String.format("%s, $%d", getDescription(), cost());
	}
	
	
}

/**
 * Một loại "coffee" có chứa coffee ở bên trong nó (mCoffee) 
 */
abstract class CoffeeWithGiavi extends Coffee{
	private Coffee mCoffee;
	
	/**
	 * Thêm gia vị
	 * @param coffee coffee cần được thêm gia vị
	 * @param description mô tả gia vị
	 * @param cost giá gia vị
	 */
	public CoffeeWithGiavi(Coffee coffee, String description, int cost){
		super(description, cost);
		mCoffee = coffee;
	}

	@Override
	public int cost() {
		return mCoffee.cost() + mCost;
	}

	@Override
	public String getDescription() {
		return mCoffee.getDescription() + ", " + mDescription;
	}
	
}

class Espresso extends Coffee{
	public Espresso() {
		super("Espresso", 10);
	}
}
class Cappucino extends Coffee{
	public Cappucino() {
		super("Cappucino", 15);
	}
}

class Milk extends CoffeeWithGiavi{

	public Milk(Coffee coffee) {
		super(coffee, "Milk", 1);
	}
	
}
class Cream extends CoffeeWithGiavi{

	public Cream(Coffee coffee) {
		super(coffee, "Cream", 2);
	}
	
}


public class Test {

	public static void main(String[] args) {
		Coffee coffee = new Cappucino();
		System.out.println(coffee);
		
		Coffee coffee1 = new Espresso();//1 espresso
		coffee1 = new Milk(coffee1); //Thêm 1 sữa
		coffee1 = new Milk(coffee1); //Thêm 1 sữa nữa
		coffee1 = new Cream(coffee1); //Thêm 1 cream
		System.out.println(coffee1);
	}

}
