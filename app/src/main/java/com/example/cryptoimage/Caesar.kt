package com.example.cryptoimage

class Caesar {

    private val key = 100

     public fun encrypt(pixels: ArrayList<Int>): List<Int>
    {
        // En(x) = (x + n) mod 255
        //alpha
        var alpha = (pixels.get(0) + key) % 256
        //red
        var red = (pixels.get(1) + key) % 256
        //green
        var green = (pixels.get(2) + key) % 256
        //blue
        var blue = (pixels.get(3) + key) % 256

        return listOf(alpha,red,green,blue)
    }

    public fun decrypt(pixels: ArrayList<Int>): List<Int>
    {
        // De(x) = (x - n) mod 255
        //alpha
        var alpha = (pixels.get(0) - key) % 256
        //red
        var red = (pixels.get(1) - key) % 256
        //green
        var green = (pixels.get(2) - key) % 256
        //blue
        var blue = (pixels.get(3) - key) % 256

        return listOf(alpha,red,green,blue)
    }
}