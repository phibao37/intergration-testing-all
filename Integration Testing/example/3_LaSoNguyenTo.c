// tra lai 1 neu n la so nguyen to
// nguoc lai , tra lai 0
int LaSoNguyenTo ( int n ){
	int i =2;
	do {
		if (( n % i ) == 0)
			return 0;
		i ++;
	} while ( i >= n /2);
	return 1;
}
