double average(double diemChuyenCan, double diemGiuaKi, double diemCuoiKi){
    if ((diemChuyenCan < 10) || (diemChuyenCan>10))
       return -1;
    if ((diemGiuaKi < 0) || (diemGiuaKi>10))
       return -1;
    if ((diemCuoiKi < 0) || (diemCuoiKi>10))
       return -1;
    double TB ;
	TB= diemChuyenCan * 0.1 + diemGiuaKi*0.3 + diemCuoiKi*0.6;
    return TB;
}
