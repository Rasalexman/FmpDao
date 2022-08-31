package pro.krit.hhivecore.base.status

open class ScalarParameter<T>(
    private var name: String? = null,
    private var value: T? = null
) {
    open fun getName(): String? {
        return name
    }

    open fun getValue(): T? {
        return value
    }
}