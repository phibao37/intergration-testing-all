int test(int a[3][3],int n){
	int dem = sqrt(3);
	if (a[2][2]>a[1][1])
		return 0;
	a[0][1] = pow(dem,2);
	a[1][1] = a[0][1] + dem;
	return a[1][1];
}