void Switch(int grade){
	switch(grade)
	{
		case 0 : case 1 :case 2 :case 3 :case 4 :case 5 :
			printf("FAIL EXAM");
			break;
		case 6 : case 7 : case 8 : case 9 :
			printf("PASS EXAM");
			break;
		case 10:
			printf("FAIL EXAM. EXCELLENT!");
			break;
		default:
			printf("WRONG INPUT");
			break;
	}
}