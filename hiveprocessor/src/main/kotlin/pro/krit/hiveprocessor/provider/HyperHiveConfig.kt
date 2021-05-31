package pro.krit.hiveprocessor.provider

data class HyperHiveConfig(
    val dbKey: String,
    val serverAddress: String,
    val environment: String,
    val project: String,
    val dbPath: String = "",
    val projectVersion: String = "app",
    val retryCount: Int = 5,
    val retryInterval: Int = 10,
    val logLevel: Int = 0
)
