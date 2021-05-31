package pro.krit.fmpdaoexample

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey

data class PmEtDataLocalEntity(
    @PrimaryKey
    @Expose
    var id: Long? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("MARKER")
    var marker: String? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("AUART")
    var auart: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("RFID")
    var rfid: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("TPLNR")
    var tplnr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("PLTXT")
    var pltxt: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("STAT")
    var stat: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @Expose
    @SerializedName("AUFPL")
    var aufpl: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @Expose
    @SerializedName("APLZL")
    var aplzl: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("VORNR")
    var vornr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("LTXA")
    @Expose
    var ltxa: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @Expose
    @SerializedName("SAPLZL")
    var saplzl: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'g'}
    @JvmField
    @Expose
    @SerializedName("DESCR")
    var descr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @Expose
    @SerializedName("RNUM")
    var rnum: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("DEPART")
    var depart: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'D', 'format': '2019-11-19'}
    @JvmField
    @Expose
    @SerializedName("WDATE")
    var wdate: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("CODEGRUPPE")
    var codegruppe: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("CODE")
    var code: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("REPAIRED")
    @Expose
    var repaired: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'g'}
    @JvmField
    @Expose
    @SerializedName("DESTIN")
    var destin: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("POINT")
    var point: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("POINT_TYPE")
    var pointType: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("CODEKAT")
    var codekat: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("CODEVERS")
    var codevers: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("UNITS")
    var units: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("OWKAT")
    var owkat: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("AUFNR")
    var aufnr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("MOUNT_PART")
    var mount_part: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("EQUNR")
    var equnr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @Expose
    @SerializedName("EQKTX")
    var eqktx: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("QMNUM")
    @Expose
    var qmnum: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("IS_BREAK")
    @Expose
    var isBreak: String? = null,

    @JvmField
    @Expose
    @SerializedName("IS_EQUNR")
    var isEqunr: String? = null,

    @Expose
    @SerializedName("KURZTEXT")
    var kurzText: String? = null,

    @Expose
    @SerializedName("KURZTEXT_CODE")
    var kurzTextCode: String? = "",

    @Expose
    @SerializedName("TXTCDMA")
    var correctiveAction: String? = "",

    @JvmField
    @Expose
    @SerializedName("BAUTL")
    var bautl: String? = null,

    @JvmField
    @Expose
    @SerializedName("BAUTX")
    var bautx: String? = null,

    @JvmField
    @Expose
    @SerializedName("BAUTL_O")
    var bautl0: String? = null,

    @JvmField
    @Expose
    @SerializedName("BAUTX_O")
    var bautx0: String? = null,

    @JvmField
    @Expose
    @SerializedName("CONFIRMATION")
    var confirmation: String? = null
)
