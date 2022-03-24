package pro.krit.fmpdaoexample.models

import com.google.gson.annotations.SerializedName
import com.mobrun.plugin.api.request_assistant.PrimaryKey

data class PmEtDataEntity(
    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @PrimaryKey
    @JvmField
    @SerializedName("LOCAL_ID")
    var dataId: String? = null,

    @JvmField
    @SerializedName("MARKER")
    var marker: String? = null,

    //  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("AUART")
    var auart: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("RFID")
    var rfid: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("TPLNR")
    var tplnr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("PLTXT")
    var pltxt: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("STAT")
    var stat: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @SerializedName("AUFPL")
    var aufpl: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @SerializedName("APLZL")
    var aplzl: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("VORNR")
    var vornr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("LTXA")
    var ltxa: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @SerializedName("SAPLZL")
    var saplzl: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'g'}
    @JvmField
    @SerializedName("DESCR")
    var descr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'N'}
    @JvmField
    @SerializedName("RNUM")
    var rnum: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("DEPART")
    var depart: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'D', 'format': '2019-11-19'}
    @JvmField
    @SerializedName("WDATE")
    var wdate: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("CODEGRUPPE")
    var codegruppe: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("CODE")
    var code: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("REPAIRED")
    var repaired: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'g'}
    @JvmField
    @SerializedName("DESTIN")
    var destin: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("POINT")
    var point: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("POINT_TYPE")
    var pointType: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("CODEKAT")
    var codekat: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("CODEVERS")
    var codevers: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("UNITS")
    var units: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("OWKAT")
    var owkat: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("AUFNR")
    var aufnr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("MOUNT_PART")
    var mount_part: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("EQUNR")
    var equnr: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @JvmField
    @SerializedName("EQKTX")
    var eqktx: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("QMNUM")
    var qmnum: String? = null,

//  type: TEXT, source: {'name': 'SAP', 'type': 'C'}
    @SerializedName("IS_BREAK")
    var isBreak: String? = null,

    @JvmField
    @SerializedName("IS_EQUNR")
    var isEqunr: String? = null,

    @SerializedName("KURZTEXT")
    var kurzText: String? = null,

    @SerializedName("KURZTEXT_CODE")
    var kurzTextCode: String? = "",

    @SerializedName("TXTCDMA")
    var correctiveAction: String? = "",

    @JvmField
    @SerializedName("BAUTL")
    var bautl: String? = null,

    @JvmField
    @SerializedName("BAUTX")
    var bautx: String? = null,

    @JvmField
    @SerializedName("BAUTL_O")
    var bautl0: String? = null,

    @JvmField
    @SerializedName("BAUTX_O")
    var bautx0: String? = null,

    @JvmField
    @SerializedName("CONFIRMATION")
    var confirmation: String? = null
)
