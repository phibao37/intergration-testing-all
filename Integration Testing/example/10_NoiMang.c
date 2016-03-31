// Noi 2 mang da duoc sxep ( a va b )
// vao mang c ( duoc sxep )
void NoiMang ( int a [] , int n , int b [] , int m ,int c [] , int  k ){
	int i =0 , j =0;
	k =0;
	while (i < n && j <m )
	{
		if ( a [ i ] <= b [ j ]){
			c[ k ] = a [ i ];
			i ++;
		} else {
			c[ k ] = b [ j ];
			j ++;
		} // end if
		k ++;
	} // end while
	while (i < n ){
		c[ k ]= a [ i ];
		i ++;
		k ++;
	} // end while
	while (j < m ){
		c[ k ]= b [ j ];
		j ++;
		k ++;
	} // end while
} // the end