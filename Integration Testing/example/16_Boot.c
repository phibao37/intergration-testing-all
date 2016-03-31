int test(int a, int b){
	int i,j;
	int c,d;
	c = 0;
	if (b>=a)
		return -1;
	for (i = 0; i<a;i++)
	{
		if (b!=a) 
			d = 1;
		else 
			d = 0;
		for (j=0;j<b;j++)
			c = c + i * j / d;
	}
	return c;
}