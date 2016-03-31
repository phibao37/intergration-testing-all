// tra lai so ky tu trong xau
int SoKyTu ( char a []){
	int i , total = 0;
	int len = strlen ( a );
	for ( i = 0; i < len ; i ++){
		if ((( a [ i] >='A' )&&( a [ i] <='Z' ))
			||(( a [ i] >='a' )&&( a [ i] <='z' )))
			total ++;
	} // end for
	return total ;
}