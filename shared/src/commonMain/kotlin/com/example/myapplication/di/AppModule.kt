package com.example.myapplication.di

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.database.DatabaseBuilderFactory
import com.example.myapplication.data.repository.AppSettingsRepositoryImpl
import com.example.myapplication.data.repository.CompanyRepositoryImpl
import com.example.myapplication.data.repository.CustomerRepositoryImpl
import com.example.myapplication.data.repository.InvoiceItemRepositoryImpl
import com.example.myapplication.data.repository.InvoiceNumberSequenceRepositoryImpl
import com.example.myapplication.data.repository.InvoiceRepositoryImpl
import com.example.myapplication.data.repository.PdfRepositoryImpl
import com.example.myapplication.domain.repository.AppSettingsRepository
import com.example.myapplication.domain.repository.CompanyRepository
import com.example.myapplication.domain.repository.CustomerRepository
import com.example.myapplication.domain.repository.InvoiceItemRepository
import com.example.myapplication.domain.repository.InvoiceNumberSequenceRepository
import com.example.myapplication.domain.repository.InvoiceRepository
import com.example.myapplication.domain.repository.PdfRepository
import com.example.myapplication.domain.usecase.AddInvoiceItemUseCase
import com.example.myapplication.domain.usecase.CalculateInvoiceTotalsUseCase
import com.example.myapplication.domain.usecase.CancelInvoiceUseCase
import com.example.myapplication.domain.usecase.CreateCustomerUseCase
import com.example.myapplication.domain.usecase.CreateInvoiceDraftUseCase
import com.example.myapplication.domain.usecase.DeleteCustomerUseCase
import com.example.myapplication.domain.usecase.DeleteInvoiceDraftUseCase
import com.example.myapplication.domain.usecase.DuplicateCustomerUseCase
import com.example.myapplication.domain.usecase.DuplicateInvoiceUseCase
import com.example.myapplication.domain.usecase.FinalizeInvoiceUseCase
import com.example.myapplication.domain.usecase.GenerateInvoiceNumberUseCase
import com.example.myapplication.domain.usecase.GenerateInvoicePdfUseCase
import com.example.myapplication.domain.usecase.GetInvoiceDetailsUseCase
import com.example.myapplication.domain.usecase.MarkInvoiceAsPaidUseCase
import com.example.myapplication.domain.usecase.MarkInvoiceAsSentUseCase
import com.example.myapplication.domain.usecase.RemoveInvoiceItemUseCase
import com.example.myapplication.domain.usecase.SaveAppSettingsUseCase
import com.example.myapplication.domain.usecase.SaveCompanyProfileUseCase
import com.example.myapplication.domain.usecase.SearchInvoicesUseCase
import com.example.myapplication.domain.usecase.ShareInvoicePdfUseCase
import com.example.myapplication.domain.usecase.UpdateCustomerUseCase
import com.example.myapplication.domain.usecase.UpdateInvoiceDraftUseCase
import com.example.myapplication.domain.usecase.UpdateInvoiceItemUseCase
import com.example.myapplication.presentation.presentation.viewmodel.CompanyProfileViewModel
import com.example.myapplication.presentation.presentation.viewmodel.CustomerDetailViewModel
import com.example.myapplication.presentation.presentation.viewmodel.CustomerListViewModel
import com.example.myapplication.presentation.presentation.viewmodel.DashboardViewModel
import com.example.myapplication.presentation.presentation.viewmodel.InvoiceDetailViewModel
import com.example.myapplication.presentation.presentation.viewmodel.InvoiceEditorViewModel
import com.example.myapplication.presentation.presentation.viewmodel.InvoiceListViewModel
import com.example.myapplication.presentation.presentation.viewmodel.SettingsViewModel
import com.example.myapplication.util.AppClock
import com.example.myapplication.util.SystemAppClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable.get
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.coroutines.EmptyCoroutineContext.get

val databaseModule = module {
    single {
        get<DatabaseBuilderFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

val useCaseModule = module {
    // Kunden
    singleOf(::CreateCustomerUseCase)
    singleOf(::UpdateCustomerUseCase)
    singleOf(::DeleteCustomerUseCase)

    // Rechnungen - Erstellung & Positionen
    singleOf(::CreateInvoiceDraftUseCase)
    singleOf(::AddInvoiceItemUseCase)
    singleOf(::UpdateInvoiceItemUseCase)
    singleOf(::RemoveInvoiceItemUseCase)
    singleOf(::CalculateInvoiceTotalsUseCase)
    singleOf(::UpdateInvoiceDraftUseCase)

    // Rechnungen - Lebenszyklus
    singleOf(::GenerateInvoiceNumberUseCase)
    singleOf(::FinalizeInvoiceUseCase)
    singleOf(::MarkInvoiceAsSentUseCase)
    singleOf(::MarkInvoiceAsPaidUseCase)
    singleOf(::CancelInvoiceUseCase)
    singleOf(::DeleteInvoiceDraftUseCase)
    singleOf(::DuplicateInvoiceUseCase)
    singleOf(::DuplicateCustomerUseCase)

    // Rechnungen - Abfragen
    singleOf(::SearchInvoicesUseCase)
    singleOf(::GetInvoiceDetailsUseCase)
    singleOf(::SaveCompanyProfileUseCase)
    singleOf(::SaveAppSettingsUseCase)

    //Pdf
    singleOf(::GenerateInvoicePdfUseCase)
    singleOf(::ShareInvoicePdfUseCase)
}
val dataModule = module {

    // 1. DAOs bereitstellen (Zugriff über deine Room Database Instanz)
    single { get<AppDatabase>().companyDao() }
    single { get<AppDatabase>().customerDao() }
    single { get<AppDatabase>().invoiceDao() }
    single { get<AppDatabase>().invoiceItemDao() }
    single { get<AppDatabase>().invoiceNumberSequenceDao() }
    single { get<AppDatabase>().appSettingsDao() }

    // 2. Repositories an ihre Interfaces binden
    single<CompanyRepository> { CompanyRepositoryImpl(get()) }
    single <CustomerRepository> { CustomerRepositoryImpl(get()) }
    single <InvoiceRepository> { InvoiceRepositoryImpl(get()) }
    single <InvoiceItemRepository> { InvoiceItemRepositoryImpl(get()) }
    single <InvoiceNumberSequenceRepository> { InvoiceNumberSequenceRepositoryImpl(get()) }
    single <AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }
}
val clockModule = module {
    single<AppClock> { SystemAppClock() }
}

/**
 * ViewModels ohne Navigations-Argumente (Dashboard, Rechnungsliste, Einstellungen)
 * werden über viewModelOf verdrahtet - Koin löst alle Konstruktorparameter selbst auf.
 *
 * ViewModels, die eine ID oder companyId aus der Navigation brauchen (z. B. welche
 * Rechnung bearbeitet wird), nehmen sie über parametersOf entgegen - siehe
 * Aufrufbeispiele unten. Nullable Parameter (invoiceId, customerId, companyId bei
 * Neuanlage) unterscheiden "neu anlegen" von "bestehendes Objekt bearbeiten".
 */
val viewModelModule = module {

    viewModelOf(::DashboardViewModel)
    viewModelOf(::InvoiceListViewModel)
    viewModelOf(::SettingsViewModel)

    viewModel { (invoiceId: String) ->
        InvoiceDetailViewModel(
            invoiceId = invoiceId,
            getInvoiceDetails = get(),
            customerRepository = get(),
            markInvoiceAsSent = get(),
            markInvoiceAsPaid = get(),
            cancelInvoice = get(),
            duplicateInvoice = get()
        )
    }

    viewModel { (invoiceId: String?, companyId: String) ->
        InvoiceEditorViewModel(
            initialInvoiceId = invoiceId,
            companyId = companyId,
            clock = get(),
            invoiceRepository = get(),
            invoiceItemRepository = get(),
            customerRepository = get(),
            companyRepository = get(),
            appSettingsRepository = get(),
            createInvoiceDraft = get(),
            updateInvoiceDraft = get(),
            addInvoiceItem = get(),
            updateInvoiceItem = get(),
            removeInvoiceItem = get(),
            finalizeInvoice = get()
        )
    }

    viewModel { (companyId: String) ->
        CustomerListViewModel(
            companyId = companyId,
            clock = get(),
            customerRepository = get(),
            deleteCustomer = get()
        )
    }

    viewModel { (customerId: String?, companyId: String) ->
        CustomerDetailViewModel(
            customerId = customerId,
            companyId = companyId,
            clock = get(),
            customerRepository = get(),
            invoiceRepository = get(),
            createCustomer = get(),
            updateCustomer = get(),
            duplicateCustomer = get()
        )
    }

    viewModel { (companyId: String?) ->
        CompanyProfileViewModel(
            companyId = companyId,
            companyRepository = get(),
            appSettingsRepository = get(),
            saveCompanyProfile = get()
        )
    }
}
val pdfModule = module {
    singleOf(::PdfRepositoryImpl) { bind<PdfRepository>() }
    // PdfRenderer, FileStorage, FileSharer werden in androidMain/iosMain/desktopMain
    // je Plattform gebunden (platformModule) - siehe nächste Antwort für Android.
}
val appModule = module {
    includes(databaseModule, dataModule,useCaseModule,clockModule,viewModelModule,pdfModule)
}