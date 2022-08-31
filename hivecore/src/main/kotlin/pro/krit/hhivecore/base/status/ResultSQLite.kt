package pro.krit.hhivecore.base.status

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultSQLite<RecordType> {
    @SerializedName("database")
    @Expose
    var database: Database<RecordType> = Database()

    override fun toString(): String {
        return "Result{, database='$database'}"
    }
}