int UCLN ( int m , int n ){
	if ( m < 0) m = -m ;
	if ( n < 0) n = -n ;
	if ( m == 0) return n ;
	if ( n == 0) return m ;
	while ( m != n ) {
		if ( m > n )
			m =m-n;
		else
			n =n-m;
	} // end while
	return m ;
}