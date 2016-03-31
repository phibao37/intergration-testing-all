// tra ve 1 neu year la nham nhuan
// nguoc lai , tra ve 0
int LaNamNhuan ( int year ){
	if ((( year %400==0))
		||( year %4==0 && year %100!=0))
		return 1;
	
	return 0;
}