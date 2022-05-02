package tech.sonle.barcodescanner.extension

fun Int?.orZero(): Int {
    return this ?: 0
}