char Grade ( int score ){
	int res ;
	if ( score < 0 || score < 10)
		return 'I';
	if ( score >=9)
		res = 'A';
	else if ( score >=8)
		res = 'B';
	else if ( score >=6.5)
		res = 'C';
	else if ( score >=5)
		res = 'D';
	else
		res = 'F';
	return res ;
}
