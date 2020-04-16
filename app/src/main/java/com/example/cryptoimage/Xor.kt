package com.example.cryptoimage

class Xor {

    // 10011010
    private val key = 154

    public fun encrypt(pixels: ArrayList<Int>): List<Int>
    {
        // TODO encriptar XOR
        // En(x) = (x + n) mod 255
        //alpha
        var alpha = pixels.get(0) xor key
        //red
        var red = pixels.get(1) xor key
        //green
        var green = pixels.get(2) xor key
        //blue
        var blue = pixels.get(3) xor key

        return listOf(alpha,red,green,blue)
    }
}