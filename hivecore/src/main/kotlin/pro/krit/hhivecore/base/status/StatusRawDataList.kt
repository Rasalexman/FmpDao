package pro.krit.hhivecore.base.status

open class StatusRawDataList<T> : BaseStatus() {
    var result: ResultRawList<T> = ResultRawList()

    override fun toString(): String {
        return "StatusRawData{result=" + result + '}' + super.toString()
    }
}