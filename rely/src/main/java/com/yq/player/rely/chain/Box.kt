package com.yq.player.rely.chain

class Box : HashMap<Any, Any?>() {

    /**
     * 为指定键映射指定值
     */
    infix fun <B> Any.put(that: B) {
        put(this, that as? Any)
    }

    /**
     * 返回指定键映射的值
     * 如果不存在则返回null
     * @return 返回对象会强制转换为指定泛型
     */
    fun <B> take(a: Any): B? {
        return get(a) as? B
    }

    /**
     * 返回指定键隐射的值
     * 如果不存在则返回默认值[default]
     * @return 返回类型为默认值类型
     */
    infix fun <B> Any.take(default: B): B {
        return (get(this) as? B) ?: default
    }

    /**
     * 返回指定键隐射的值
     *
     * @return 返回类型为指定clz的类型
     */
    infix fun <B> Any.take(clz: Class<B>): B? {
        return get(this) as? B
    }

}