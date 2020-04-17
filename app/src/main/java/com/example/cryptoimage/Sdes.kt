package com.example.cryptoimage

import android.util.Log

class Sdes {

    val key = "1001011010"

    private fun generate_K1_k2(): Pair<String,String>
    {
        //P10
        val p10_bin = P10(key)

        //LS1
        val (ls1, ls2) = LS1(p10_bin)

        //P8 - K1
        val p8_bin_k1 = P8(ls1, ls2)

        //LS2
        val (ls1_2, ls2_2) = LS2(ls1, ls2)

        //P8 K2
        val p8_bin_k2 = P8(ls1_2, ls2_2)

        //Return both K1 and k2
        return Pair(p8_bin_k1,p8_bin_k2)
    }

    private fun P8(ls1: String, ls2:String):String
    {
        val bit_string = ls1 + ls2
        //6 3 7 4 8 5 10 9
        val result = bit_string[5]+ "" +
                bit_string[2] + "" +
                bit_string[6] + "" +
                bit_string[3] + "" +
                bit_string[7] + "" +
                bit_string[4] + "" +
                bit_string[9] + "" +
                bit_string[8]

        Log.d("DEBUGANDO", "RESULTADO DE P8 ${result} " )
        return result
    }

    private fun P10(bin_string: String):String
    {
        // 3 5 2 7 4 10 1 9 8 6
        val result = bin_string[2]+ "" +
                bin_string[4] + "" +
                bin_string[1] + "" +
                bin_string[6] + "" +
                bin_string[3] + "" +
                bin_string[9] + "" +
                bin_string[0] + "" +
                bin_string[8]+ "" +
                bin_string[7]+ "" +
                bin_string[5]

        Log.d("DEBUGANDO", "RESULTADO DE P10 ${result} " )
        return result
    }

    private fun LS1 (p10: String): Pair<String,String>
    {

        val ls_part1 = p10.substring(0,5)
        val ls_part2 = p10.substring(5)

        val left_shifted_1 = left_shift(ls_part1)
        val left_shifted_2 = left_shift(ls_part2)

        return Pair(left_shifted_1,left_shifted_2)
    }

    private fun LS2 (ls1: String, ls2: String): Pair<String,String>
    {
        //ls1
        val left_1 = left_shift(ls1)
        val left_2 = left_shift(left_1)

        //ls2
        val l1 = left_shift(ls2)
        val l2 = left_shift(l1)

        return Pair(left_2,l2)
    }

    private fun left_shift(p_string:String):String
    {
        // Get first bit in left
        val first_bit = p_string.get(0)

        // Add in the end
        val added_p10 = p_string + first_bit

        //Delete first char
        val left_shifted_p10 = added_p10.substring(1)

        return left_shifted_p10
    }

    private fun IP(bin_string:String):Pair<String,String>
    {
        //2 6 3 1 4 8 5 7
        val result = bin_string[1]+ "" +
                bin_string[5] + "" +
                bin_string[2] + "" +
                bin_string[0] + "" +
                bin_string[3] + "" +
                bin_string[7] + "" +
                bin_string[4] + "" +
                bin_string[6]

        Log.d("DEBUGANDO", "RESULTADO DE P8 ${result} " )

        val ip1 = result.substring(0,4)
        val ip2 = result.substring(4)

        return Pair(ip1, ip2)
    }

    private fun EP(bin_1:String):String
    {
        //4 1 2 3 | 2 3 4 1
        val p1 = bin_1[3]+ "" +
                bin_1[0] + "" +
                bin_1[1] + "" +
                bin_1[2]

        val p2 = bin_1[1]+ "" +
                bin_1[2] + "" +
                bin_1[3] + "" +
                bin_1[0]

        val result = p1 + p2
        return result
    }

    private fun xor_bin_string(bs1:String, bs2:String):String
    {
        var final_s = ""
        for(i in bs1.indices)
        {
            if( (bs1.get(i) == '0' && bs2.get(i) == '0') || (bs1.get(i) == '1' && bs2.get(i) == '1') )
            {
                final_s += "0"
            }else{
                final_s += "1"
            }
        }

        return final_s
    }

    private fun generate_s0_s1(xor_string:String):String
    {
        val p1 = xor_string.substring(0,4)
        val p2 = xor_string.substring(4)

        //s0
        val s0_m = arrayOf(intArrayOf(1, 0, 3, 2), intArrayOf(3, 2, 1, 0), intArrayOf(0, 2, 1, 3), intArrayOf(3, 1, 3, 2))

        //s1
        val s1_m = arrayOf(intArrayOf(0, 1, 2, 3), intArrayOf(2, 0, 1, 3), intArrayOf(3, 0, 1, 0), intArrayOf(2, 1, 0, 3))

        val row_1 = p1.get(0) + "" + p1.get(3)
        val column_1 = p1.get(1) + "" + p1.get(2)

        val num1 = s0_m[Integer.parseInt(row_1,2)][Integer.parseInt(column_1,2)]

        val row_2 = p2.get(0) + "" + p2.get(3)
        val column_2 = p2.get(1) + "" + p2.get(2)

        val num2 = s1_m[Integer.parseInt(row_2,2)][Integer.parseInt(column_2,2)]

        val numfinal_1 = Integer.toBinaryString(num1)
        val numf_1 = if(num1 == 0 || num1 == 1) {
            "0" + numfinal_1
        }else{
            numfinal_1
        }

        var numfinal_2 = Integer.toBinaryString(num2)
        var numf_2 = if(num2 == 0 || num2 == 1) {
            "0" + numfinal_2
        }else{
            numfinal_2
        }

        var result = numf_1 + "" + numf_2

        return result
    }

    private fun P4(s:String):String
    {
        //2 4 3 1
        val result = s[1]+ "" + s[3] + "" + s[2] + "" + s[0]

        Log.d("DEBUGANDO", "RESULTADO DE P10 ${result} " )
        return result
    }

    private fun IP_1(fin_s1:String, fin_s2:String):String
    {
        var res_concat_4 = fin_s1 + "" + fin_s2
        // 4 1 3 5 7 2 8 6
        val result = res_concat_4[3]+ "" +
                res_concat_4[0] + "" +
                res_concat_4[2] + "" +
                res_concat_4[4] + "" +
                res_concat_4[6] + "" +
                res_concat_4[1] + "" +
                res_concat_4[7] + "" +
                res_concat_4[5]

        return result
    }

    fun encrypt(pixels: ArrayList<Int>): List<Int>
    {
        val (k1, k2) = generate_K1_k2()

        val argb = ArrayList<Int>()

        for (pixel in pixels)
        {
            val bin_pixel = Integer.toBinaryString(pixel)

            println("binary String: " + bin_pixel)
            var zeroes = ""
            if(bin_pixel.length < 8){
                println("oi")
                val a = 8 - bin_pixel.length
                println(a)
                for(i in 1..a){
                    println(i)
                    zeroes+=0
                }
            }

            val complete_bin = zeroes + bin_pixel
            println("total binary string: $complete_bin")

            //IP
            val (p1, p2) = IP(complete_bin)

            //EP
            val ep = EP(p2)

            //xor
            var xor_f = xor_bin_string(ep,k1)

            //s0s1
            val s0s1 = generate_s0_s1(xor_f)

            //p4
            val p4 = P4(s0s1)

            //other xor
            val r1 = xor_bin_string(p1,p4)

            //Sw p2 / r1-----------------------------------//
            //EP
            var ep2 = EP(r1)

            //xor
            val xor_f_2 = xor_bin_string(ep2,k2)

            //s0s1
            val s0s1_2 = generate_s0_s1(xor_f_2)

            //p4
            val p4_2 = P4(s0s1_2)

            //other xor
            val r1_2 = xor_bin_string(p2,p4_2)

            //IP-1
            val res_final = IP_1(r1_2, r1)

            val int_final = Integer.parseInt(res_final, 2)

            argb.add(int_final)
        }

        return argb
    }

    fun decrypt(pixels: ArrayList<Int>): List<Int>
    {
        val (k1, k2) = generate_K1_k2()

        val argb = ArrayList<Int>()

        for (pixel in pixels)
        {
            val bin_pixel = Integer.toBinaryString(pixel)

            println("binary String: " + bin_pixel)
            var zeroes = ""
            if(bin_pixel.length < 8){
                println("oi")
                val a = 8 - bin_pixel.length
                println(a)
                for(i in 1..a){
                    println(i)
                    zeroes+=0
                }
            }

            val complete_bin = zeroes + bin_pixel
            println("total binary string: $complete_bin")

            //IP
            val (p1, p2) = IP(complete_bin)

            //EP
            val ep = EP(p2)

            //xor
            var xor_f = xor_bin_string(ep,k2)

            //s0s1
            val s0s1 = generate_s0_s1(xor_f)

            //p4
            val p4 = P4(s0s1)

            //other xor
            val r1 = xor_bin_string(p1,p4)

            //Sw p2 / r1-----------------------------------//
            //EP
            var ep2 = EP(r1)

            //xor
            val xor_f_2 = xor_bin_string(ep2,k1)

            //s0s1
            val s0s1_2 = generate_s0_s1(xor_f_2)

            //p4
            val p4_2 = P4(s0s1_2)

            //other xor
            val r1_2 = xor_bin_string(p2,p4_2)

            //IP-1
            val res_final = IP_1(r1_2, r1)

            val int_final = Integer.parseInt(res_final, 2)

            argb.add(int_final)
        }

        return argb
    }
}