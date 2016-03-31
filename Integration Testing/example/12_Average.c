double average(int value[], int min, int max){
	int tcnt,vcnt,sum,i;
	while ((value[i] != -2)&&(tcnt<2)){
		tcnt++;
		if ((min <= value[i])&&(value[i]<=max)){
			sum = sum + value[i];
			vcnt++;
		}
		i++;
	}
	if (vcnt <= 0)
		return -9;
	return sum/vcnt;
}