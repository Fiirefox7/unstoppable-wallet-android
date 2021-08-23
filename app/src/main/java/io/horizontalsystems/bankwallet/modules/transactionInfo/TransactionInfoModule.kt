package io.horizontalsystems.bankwallet.modules.transactionInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.ITransactionsAdapter
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.entities.transactionrecords.bitcoin.TransactionLockState
import io.horizontalsystems.bankwallet.modules.send.SendModule
import io.horizontalsystems.bankwallet.modules.transactions.TransactionType
import io.horizontalsystems.bankwallet.modules.transactions.q.TransactionItem
import io.horizontalsystems.core.helpers.DateHelper
import java.util.*

object TransactionInfoModule {

    class Factory(private val transactionItem: TransactionItem) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val adapter: ITransactionsAdapter = App.transactionAdapterManager.getAdapter(transactionItem.record.source)!!
            val service = TransactionInfoService(
                adapter,
                App.xRateManager,
                App.currencyManager,
                App.buildConfigProvider,
                App.accountSettingManager
            )
            val factory = TransactionInfoViewItemFactory(
                App.numberFormatter,
                Translator,
                DateHelper,
                TransactionInfoAddressMapper
            )
            return TransactionInfoViewModel(
                service,
                factory,
                transactionItem.record,
                transactionItem.record.source,
                listOf(service)
            ) as T
        }

    }

    data class ExplorerData(val title: String, val url: String?)
}

sealed class TransactionStatusViewItem {
    class Pending(val name: String) : TransactionStatusViewItem()

    //progress in 0.0 .. 1.0
    class Processing(val progress: Double, val name: String) : TransactionStatusViewItem()
    class Completed(val name: String) : TransactionStatusViewItem()
    object Failed : TransactionStatusViewItem()
}
