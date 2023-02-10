package com.blackace.util.ext

import android.os.Build
import com.blackace.util.holder.ContextHolder

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午7:15
 */
fun getString(id:Int,vararg args:Any):String{
    return if (args.isEmpty()){
        ContextHolder.get().getString(id)
    }else{
        ContextHolder.get().getString(id,*args)
    }

}

fun getColor(id: Int):Int{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ContextHolder.get().getColor(id)
    } else {
        ContextHolder.get().resources.getColor(id)
    }
}
