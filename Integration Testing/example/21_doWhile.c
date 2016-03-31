int testDoWhile(int a, int b){
	if ((a<b)||(a<0)||(b<0)){
		printf("a phai lon hon b va cung >=0");
		return 0;
	}
	int i=0, soLanChan=0, soLanle=0;
	do{
		a--;
		i++;
		/*switch (a%2){
			case 0: soLanChan++; break;
			case 1: soLanle++; break;
		}*/
	}while (a!=b);
	return i;
}
