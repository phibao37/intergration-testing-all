int complexSwap(int a[]){
	a[0] = a[1];
	a[1] = a[2];
	a[2] = a[0];
	
	a[0] = a[0] - a[2];
	return a[0];
}