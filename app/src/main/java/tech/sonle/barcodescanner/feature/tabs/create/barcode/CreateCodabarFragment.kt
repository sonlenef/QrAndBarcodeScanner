package tech.sonle.barcodescanner.feature.tabs.create.barcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import tech.sonle.barcodescanner.R
import tech.sonle.barcodescanner.extension.isNotBlank
import tech.sonle.barcodescanner.extension.textString
import tech.sonle.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import tech.sonle.barcodescanner.model.schema.Other
import tech.sonle.barcodescanner.model.schema.Schema
import kotlinx.android.synthetic.main.fragment_create_codabar.*

class CreateCodabarFragment : BaseCreateBarcodeFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_codabar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edit_text.requestFocus()
        edit_text.addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled = edit_text.isNotBlank()
        }
    }

    override fun getBarcodeSchema(): Schema {
        return Other(edit_text.textString)
    }
}