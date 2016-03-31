int binary_octal(int n)  /* Function to convert binary to octal. */
{
    int octal=0, decimal=0, i=0;
    while(n!=0)
    {
        decimal+=(n%10)*pow(2,i);
        ++i;
        n/=10;
    }

/*At this point, the decimal variable contains corresponding decimal value of binary number. */

    i=1;
    while (decimal!=0)
    {
        octal+=(decimal%8)*i;
        decimal/=8;
        i*=10;
    }
    return octal;
}