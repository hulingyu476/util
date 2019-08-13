int bin2bcd(int val)
{
	return ((val / 10) << 4) + val % 10;
}

int bcd2bin(int val)
{
	return (val & 0x0f) + (val >> 4) * 10;
}
