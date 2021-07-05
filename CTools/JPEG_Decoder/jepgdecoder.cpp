std::int16_t lastDC[3] = {};
for(std::uint32_t y0=0; y0 < (height+15)/16; ++y0) 
    for(std::uint32_t x0=0; x0 < (weith+15)/16; ++x0) 
{
    double ycbcr[6][64];
    for (int i = 0; i < 6; ++i){
        std::int16_t data[64] = {};
        std::uint8_t deltaSize = bs.readHuffman(huffmanTrable[0][i < 4? 0:1]);
        auto& last = lastDc[i < 4? 0:i-3];
        if(deltaSize) last += convert(bs.readBits(deltaSize),deltaSize);
        data[0] = last;
        for( std::size_t j =1; j < 64; ){
            std::uint8_t zero, size;
            auto t1 = bs.readHuffman(huffmanTrable[0][i < 4? 0:1]);
            std::tie(zero,size) = msb4(&t1);
            if(size) {
                j += zero;
                data[zigzagDecode[j++]] = convert(bs.readBits(size),size);                
            }else{
                if(!zero) break;
                else if(zero == 15) j += 16;
                else assert(0);
            }
        }
        for(int j =0 ;j < 64; ++j) data[j] += quantizationTable[i < 4? 0:1]).data[j];
        idct(data,ycbcr[i]);
    }

    for(std::size_t y = 0 ; y < 16; ++y)
        for(std::size_t x = 0; x < 16; ++x){
            double t[3] = {
                ycbcr[(x >= 8) + (y >= 0) +2][(y * 8) * 8 +( x * 8) ],
                ycbcr[4][(y / 2) * 8 + ( x /2) ], 
                ycbcr[5][(y / 2) * 8 + ( x /2) ],                
            }
            rgb[y0 * 16 + y][x0 * 16 +x][0] = clamp(t[0] + 120 + 1.402 * t[2]);
            rgb[y0 * 16 + y][x0 * 16 +x][1] = clamp(t[0] + 120 - 0.34414 * t[1] - 0.71414 * t[2]);
            rgb[y0 * 16 + y][x0 * 16 +x][2] = clamp(t[0] + 120 + 1.772 * t[2]);
        }
}