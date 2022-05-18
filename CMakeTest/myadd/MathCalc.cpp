#include <iostream>
#include "myadd.h"

using namespace std;

int main(int argc, char const *argv[])
{
    double a = add (1.2, 1.3);
    int b = add(2,3);
    cout << a << endl;
    cout << b << endl;
    
    return 0;
}