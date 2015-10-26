# Control Flow Testing for Java Unit
Kiểm thử theo dòng điều khiển cho các Unit Java

## Các bộ giải hệ ràng buộc hỗ trợ:
1. Random Solver
2. Z3 Solver

## Định dạng mã nguồn được hỗ trợ
- Các kiểu số nguyên (`int`, `long`), số thực (`float`, `double`), logic (`boolean`) và mảng của các kiểu này. *Chưa hỗ trợ* thuộc tính `length` của kiểu mảng Java
- Các phép tính tính toán thông thường (+, -, *, /, ...), so sánh logic
- Các phép gán thông thường (=, +=, ++, --. ...), *chưa hỗ trợ* phép gán lồng trong biểu thức (`a + (b=2)`, `b=c=d=1`,...)
