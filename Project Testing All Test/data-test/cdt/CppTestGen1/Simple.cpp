int SimpleTest(int x, bool y, float z, char t){
	x = x + 1;
	x = 2 * x - 3;
	
	if (x > 0 && y && z >= 1.5 && t >= '\n')
		return 1;
	else
		return 0;
}