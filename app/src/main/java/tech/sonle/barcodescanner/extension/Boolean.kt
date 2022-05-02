package tech.sonle.barcodescanner.extension

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}