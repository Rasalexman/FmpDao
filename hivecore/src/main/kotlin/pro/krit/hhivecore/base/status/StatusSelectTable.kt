package pro.krit.hhivecore.base.status


open class StatusSelectTable<T> : BaseStatus() {
    var result: ResultSQLite<T> = ResultSQLite()
    override fun toString(): String {
        return "StatusDataGeneric{result=" + result + '}' + super.toString()
    }
}