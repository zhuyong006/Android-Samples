#include <stdio.h>
#include <string.h>
#include <stdlib.h>
char *mm = NULL;

void mem_leak(void)
{
    mm = malloc(4096);
    memset(mm,0x0,4096);

}