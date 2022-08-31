package pro.krit.fmpdaoexample.models

import pro.krit.hhivecore.base.status.BaseStatus
import pro.krit.hhivecore.base.status.StatusSelectTable

class TableNameEntity : BaseStatus() {
    val request: TableNameRequest? = null
}
class TableNameRequest {
    val source: TableNameDatabase? = null
}

class TableNameDatabase {
    val database: TableNameStatement? = null
}

class TableNameStatement {
    val statements: List<String>? = null
}

class TableNameStatus : StatusSelectTable<String>()