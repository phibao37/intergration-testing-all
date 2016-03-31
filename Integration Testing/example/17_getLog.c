int getLog(int a[],int n){
	a[0] = a[0]<0 ? 0 :a[0];
	int i;
	for (i=1; i< n; i++)
	if (a[i] <0 || a[i] > n){
		a[i]= a[a[0]+1];
		a[a[i]+2] = 0;
	}
	return 1/a[0];
}