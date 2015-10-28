int main(int a, int x, int b){
   if (a == b)
      return a;
   
   if (a < b){
      x = max(a, x);
      x = min(x, b);
   }
   else {
      x = min(x, a);
      x = max(b, x);
   }
   return x;
}
int min(int m, int n){
   if (m < n)
      return m;
   else
      return n;
}
int max(int m, int n){
   if (m > n)
      return m;
   else
      return n;
}