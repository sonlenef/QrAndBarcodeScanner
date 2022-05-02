package tech.sonle.barcodescanner.feature.tabs.create

import androidx.fragment.app.Fragment
import tech.sonle.barcodescanner.extension.*
import tech.sonle.barcodescanner.model.Contact
import tech.sonle.barcodescanner.model.schema.Other
import tech.sonle.barcodescanner.model.schema.Schema

abstract class BaseCreateBarcodeFragment : Fragment() {
    protected val parentActivity by unsafeLazy { requireActivity() as CreateBarcodeActivity }

    open val latitude: Double? = null
    open val longitude: Double? = null

    open fun getBarcodeSchema(): Schema = Other("")
    open fun showPhone(phone: String) {}
    open fun showContact(contact: Contact) {}
    open fun showLocation(latitude: Double?, longitude: Double?) {}
}