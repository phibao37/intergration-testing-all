double Power ( double x , int n )
{
	double result = 1.0;
	int i;
	
	for (i =1; i < n ; i++)
		result *= x ;
	return result ;
}