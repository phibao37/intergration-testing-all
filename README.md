# Intergration-testing-all
Ứng dụng kiểm thử tích hợp/kiểm thử đơn vị cho các chương trình viết bằng C/C++.<br/>
Ứng dụng hỗ trợ việc sinh testcase tự động cho các hàm trong chương trình nhằm đáp ứng các cấp độ phủ cơ bản (với kiểm thử đơn vị) và đáp ứng sự tích hợp của các cặp hàm trong chương trình (với kiểm thử tích hợp).

##Requirement
Nền tảng: JRE 8+<br/>
Cấu trúc mã nguồn C/C++ được hỗ trợ:
 - Kiểu: 
   * Kiểu cơ bản: bool, int, long, float, double
   * Kiểu mảng
   * Kiểu struct
 - Phép toán: 
   * Gán: =, +=, -=, *=, /=, %=, ++, --
   * Logic: ==, !=, <, <=, >, >=, &&, ||, ! 
   * Số học: +, -, *, /, %
   * Bitwise: &, |, ~, <<, >>
 - Bộ giải:
   * Z3
   * Random
 
##Development

1. Clone project
2. Tải bộ giải [Z3](https://github.com/Z3Prover/z3/releases), sau đó giải nén và đưa vào project, đường dẫn tới file thực thi như sau: `Project/Integration Testing/local/z3/bin/z3.exe`
3. Main Class: `main/GUIMain.java`

##Copyright
- Nhóm phát triển: 
  * [Sầm Đức Vũ](https://github.com/phibao37)
  * [Dương Tuấn Anh](https://github.com/duonganh2812)
  * [Nguyễn Đức Anh](https://github.com/ducanhnguyen)
- GVHD: [PGS. TS. Phạm Ngọc Hùng](http://uet.vnu.edu.vn/~hungpn/)
- Trường: [Đại học Công Nghệ - Đại học Quốc Gia Hà Nội](http://uet.vnu.edu.vn)
