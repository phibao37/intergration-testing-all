int triangle(int a,int b,int c)
{
	if (a <= 0 || b <= 0 ||	c <= 0)
		return -1;
	if (a+b > c && a+c > b&& b+c > a)
		return 1;
	return 0;
}
