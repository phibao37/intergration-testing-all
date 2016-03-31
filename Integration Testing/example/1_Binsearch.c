int Binsearch ( int x , int v [] , int n )   {
	int low = 0 , high , mid ;
	high = n - 1;
	while ( low <= high ) {
		mid = ( low + high ) / 2;
		if ( x < v[ mid ])
			high = mid - 1;
		else
		if ( x > v [ mid ])
			low = mid + 1;
		else
			return mid ;
	} // end while
	return -1;
} // the end