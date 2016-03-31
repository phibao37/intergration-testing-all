float foo(int a, int b, int c, int d){
	if (a==b)
		return 0;
	int x = 0;
	a = b - 2;
	if ((a==b)||(c==d))
		x = 1;
	return 1/x;
}

