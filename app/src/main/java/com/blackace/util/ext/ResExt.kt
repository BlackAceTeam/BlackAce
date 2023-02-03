package com.blackace.util

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
